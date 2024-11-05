package xyz.easyboot.generator.bean;

import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.Table;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TableInfo {

    /**
     * table所在的schema
     */
    private String schema;
    /**
     * tables所在的catalog
     */
    private String catalog;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 注释
     */
    private String comment;

    /** 驼峰+首字母大写的表名称 */
    private String upperCamelTableName;

    /** 驼峰的表名称 */
    private String camelTableName;

    /** 主键的Java类型 */
    private String pkJavaType;

    /** 主键的字段名 */
    private String pkName;

    /** 主键的数据库类型 */
    private String pkDbType;

    /**
     * 列映射，列名-列对象
     */
    private List<Column> columns;

    /** 生成的表字段 */
    private String outputColumns;

}
