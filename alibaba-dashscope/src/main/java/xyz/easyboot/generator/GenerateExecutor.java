package xyz.easyboot.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.Table;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import xyz.easyboot.generator.bean.EnumParamDefinition;
import xyz.easyboot.generator.bean.EnumResultDefinition;
import xyz.easyboot.generator.bean.GeneratorConfig;
import xyz.easyboot.generator.bean.TableInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class GenerateExecutor {

    private static GeneratorConfig generatorConfig = new GeneratorConfig();

    public static void output(GeneratorConfig config, String...taleName) {
        for (String tableName : taleName) {
            output(tableName, config);
        }
    }

    public static void output(String tableName, GeneratorConfig config) {
        // 初始化一些配置
        String upperCamelTableName = StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
        config.getMapper().setFileName(upperCamelTableName + "Mapper.java");
        config.getXml().setFileName(upperCamelTableName + "Mapper.xml");
        config.getService().setFileName(upperCamelTableName + "Service.java");
        config.getServiceImpl().setFileName(upperCamelTableName + "ServiceImpl.java");
        config.getEntity().setFileName(upperCamelTableName + ".java");
        config.getEntity().setExtraImportList(new ArrayList<>());
        // 放到静态变量里面，方便后面直接拿
        generatorConfig = config;

        // 获取表信息
        DbReader reader = new DbReader(config.getDataSource());
        Table table = reader.getTable(tableName);
        log.info("{}", JSONUtil.toJsonPrettyStr(table));
        tableName = table.getTableName();
        String pkName = table.getPkNames().iterator().next();
        List<Column> columns = new ArrayList<>(table.getColumns());

        // 单表的信息
        TableInfo tableInfo = new TableInfo();
        BeanUtil.copyProperties(table, tableInfo);
        tableInfo.setColumns(columns);
        // 转换表名
        tableInfo.setCamelTableName(StrUtil.toCamelCase(tableName));
        tableInfo.setUpperCamelTableName(StrUtil.upperFirst(StrUtil.toCamelCase(tableName)));
        // 主键
        Column pk = getPk(columns);
        tableInfo.setPkName(pkName);
        tableInfo.setPkJavaType(translateJavaType(pk.getTypeName(), 1));
        tableInfo.setPkDbType(pk.getTypeName());

        // 构建枚举
        List<EnumResultDefinition> enumResultDefinitions = buildEnumResult(tableInfo, config);

        // 直接解析entity字符串，注意要先枚举后entity
        String outputColumns = buildOutputColumns(tableInfo, config);
        log.info("outputColumns: {}", outputColumns);
        tableInfo.setOutputColumns(outputColumns);

        // 模板变量
        Dict templateDict = Dict.of();
        templateDict.set("table", tableInfo);
        templateDict.set("config", config);

        // freemarker渲染
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("generator", TemplateConfig.ResourceMode.CLASSPATH));

        // 优先枚举类
        if (CollUtil.isNotEmpty(enumResultDefinitions)) {
            Template enumTemplate = engine.getTemplate("enum.ftl");
            outputEnumFiles(enumTemplate, templateDict, config, enumResultDefinitions);
        }

        // 渲染实体类
        Template entityTemplate = engine.getTemplate("entity.ftl");
        String entityResult = entityTemplate.render(templateDict);
        log.info("{}", entityResult);
        // 渲染mapper
        String mapperResult = engine.getTemplate("mapper.ftl").render(templateDict);
        String xmlResult = engine.getTemplate("xml.ftl").render(templateDict);
        // service
        String serviceResult = engine.getTemplate("service.ftl").render(templateDict);
        // serviceImpl
        String serviceImplResult = engine.getTemplate("serviceImpl.ftl").render(templateDict);

        // 输出到文件
        outputFile(entityResult, config.getEntity().getFilePath(), config.getEntity().getFileName());
        outputFile(mapperResult, config.getMapper().getFilePath(), config.getMapper().getFileName());
        outputFile(xmlResult, config.getXml().getFilePath(), config.getXml().getFileName());
        outputFile(serviceResult, config.getService().getFilePath(), config.getService().getFileName());
        outputFile(serviceImplResult, config.getServiceImpl().getFilePath(), config.getServiceImpl().getFileName());
    }

    private static void outputEnumFiles(Template enumTemplate,Dict templateDict, GeneratorConfig config, List<EnumResultDefinition> enumResultDefinitions) {
        String filePath = config.getEnums().getFilePath();
        for (EnumResultDefinition enumResultDefinition : enumResultDefinitions) {
            if (!enumResultDefinition.getIsEnum()) {
                continue;
            }
            String fileName = enumResultDefinition.getClassName() + ".java";
            templateDict.set("enums", enumResultDefinition);

            String enumTemplateContent = enumTemplate.render(templateDict);
            log.info("{}", enumTemplateContent);

            outputFile(enumTemplateContent, filePath, fileName);
        }
    }

    private static List<EnumResultDefinition> buildEnumResult(TableInfo tableInfo, GeneratorConfig config) {
        List<Column> columns = tableInfo.getColumns();
        String tableName = tableInfo.getTableName();
        // 检查字段类型是否为整型
        // 检查注释中是否包含所有指定的符号
        List<EnumParamDefinition.Field> fieldList = columns.stream().filter(x -> {
            return ("INT".equals(x.getTypeName()) || "TINYINT".equals(x.getTypeName()))
                    && x.getSize() > 4
                    && (x.getComment().contains(":") || x.getComment().contains("-") || x.getComment().contains("："));
        }).map(x -> {
            String comment = x.getComment();
            String fieldName = x.getName();
            EnumParamDefinition.Field field = new EnumParamDefinition.Field();
            field.setComment(comment);
            field.setFieldName(fieldName);
            return field;
        }).collect(Collectors.toList());

        EnumParamDefinition enumParamDefinition = new EnumParamDefinition();
        enumParamDefinition.setTableName(tableName);
        enumParamDefinition.setFields(fieldList);
        List<EnumResultDefinition> enumResultDefinitions = AiEnumBuilder.generate(Collections.singletonList(enumParamDefinition));
        log.info("enumResultDefinitions: {}", JSONUtil.toJsonPrettyStr(enumResultDefinitions));

        String enumPackage = config.getEnums().getPackagePath();

        for (EnumResultDefinition enumResultDefinition : enumResultDefinitions) {
            if (!enumResultDefinition.getIsEnum() || !enumResultDefinition.getTableName().equals(tableInfo.getTableName())) {
                continue;
            }
            // 标记table的columns的类型
            tableInfo.getColumns().stream()
                    .filter(x -> x.getName().equals(enumResultDefinition.getFieldName()))
                    .forEach(x -> {
                        x.setTypeName(enumResultDefinition.getClassName()).setType(-999);
                        x.setComment(enumResultDefinition.getComment());
                    });
            config.getEntity().getExtraImportList().add("import " + enumPackage + "." + enumResultDefinition.getClassName() + ";");
        }

        return enumResultDefinitions;
    }

    private static void outputFile(String content, String filePath, String fileName) {
        FileUtil.mkdir(filePath);
        String fileFullPath = filePath + fileName;
        // 检查是否已经存在
        if (!generatorConfig.getOverride() && FileUtil.exist(fileFullPath)) {
            return;
        }
        FileUtil.writeString(content, fileFullPath, CharsetUtil.CHARSET_UTF_8);
    }

    private static Column getPk(List<Column> columns) {
        return columns.stream().filter(Column::isPk).findFirst().orElse(null);
    }

    private static String translateJavaType(String dbType, int type) {
        switch (dbType.toUpperCase()) {
            case "BIGINT":
                return "Long";
            case "INT":
                return "Integer";
            case "VARCHAR":
                return "String";
            case "DATETIME":
                return "Date";
            case "FLOAT":
                return "Float";
            case "DOUBLE":
                return "Double";
            case "DECIMAL":
                return "BigDecimal";
            case "TINYINT":
                // 如果没有长度，就是Boolean，否则是Integer，还得处理枚举
                if (type == -999) {
                    return dbType;
                }
                return "Boolean";
            case "BIT":
                return "Boolean";
            default:
                if (type == -999) {
                    return dbType;
                }
                return "Object";
        }
    }

    private static String buildOutputColumns(TableInfo tableInfo, GeneratorConfig config) {
        StringBuilder builder = new StringBuilder();
        AtomicBoolean hasDate = new AtomicBoolean(false);
        AtomicBoolean hasBigDecimal = new AtomicBoolean(false);
        tableInfo.getColumns().forEach(column -> {
            // 1. 注释
            String comment = column.getComment();
            if (StrUtil.isNotBlank(comment)) {
                comment = StrUtil.upperFirst(StrUtil.toCamelCase(comment));
            }
            builder.append("\n").append("\t/** ").append(comment).append(" */\n");
            // 2. mp主键
            if (column.isPk()) {
                if (column.isAutoIncrement()) {
                    builder.append("\t").append("@TableId(type = IdType.AUTO)").append("\n");
                } else {
                    builder.append("\t").append("@TableId(type = IdType.ASSIGN_ID)").append("\n");
                }
            } else {
                // 审计字段
                switch (column.getName()) {
                    case "created_at":
                    case "create_user_id":
                    case "create_seat_id":
                    case "create_org_id":
                        builder.append("\t").append("@TableField(fill = FieldFill.INSERT)").append("\n");
                        break;
                    case "updated_at":
                    case "update_user_id":
                    case "update_seat_id":
                    case "update_org_id":
                        builder.append("\t").append("@TableField(fill = FieldFill.INSERT_UPDATE)").append("\n");
                        break;
                    case "deleted_at":
                        builder.append("\t").append("@TableLogic").append("\n");
                        break;
                    default:
                        break;
                }
            }
            // 3. 字段名
            builder.append("\t").append("private ");
            String javaType = translateJavaType(column.getTypeName(), column.getType());
            builder.append(javaType).append(" ");
            builder.append(StrUtil.toCamelCase(column.getName())).append(";");
            builder.append("\n");

            // 其他配置的检查
            switch (javaType) {
                case "Date":
                    hasDate.set(true);
                    break;
                case "BigDecimal":
                    hasBigDecimal.set(true);
                    break;
                default:
                    break;
            }
        });

        if (hasDate.get()) {
            config.getEntity().getExtraImportList().add("import java.util.Date;");
        }
        if (hasBigDecimal.get()) {
            config.getEntity().getExtraImportList().add("import java.math.BigDecimal;");
        }

        return builder.toString();
    }

}
