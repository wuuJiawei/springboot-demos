package xyz.easyboot.generator;

import cn.hutool.db.ds.simple.SimpleDataSource;
import lombok.extern.slf4j.Slf4j;
import xyz.easyboot.generator.bean.*;

@Slf4j
public class CodeGenerator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://rm-uf6fq6y0881p9q3ue5o.mysql.rds.aliyuncs.com:3306/bbh_htb?useUnicode=true&characterEncoding=UTF-8&useSSL=false&databaseTerm=SCHEMA&allowMultiQueries=true";
        String user = "bbh";
        String password = "bbh1234!@#$2*";

        // 全局的配置
        GeneratorConfig config = new GeneratorConfig();
        config.setDataSource(new SimpleDataSource(url, user, password));

        // enums
        EnumsConfig enumsConfig = new EnumsConfig()
                .setPackagePath("xyz.easyboot.generator.demo.dao.enums")
                .setFilePath("/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/java/xyz/easyboot/generator/demo/dao/enums/");
        config.setEnums(enumsConfig);
        // entity
        EntityConfig entityConfig = new EntityConfig()
                .setPackagePath("xyz.easyboot.generator.demo.dao.entity")
                .setFilePath("/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/java/xyz/easyboot/generator/demo/dao/entity/");
        config.setEntity(entityConfig);
        // mapper
        MapperConfig mapperConfig = new MapperConfig()
                .setPackagePath("xyz.easyboot.generator.demo.dao.mapper")
                .setFilePath("/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/java/xyz/easyboot/generator/demo/dao/mapper/");
        config.setMapper(mapperConfig);
        // xml
        XmlConfig xmlConfig = new XmlConfig()
                .setFilePath("/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/resources/mapper/");
        config.setXml(xmlConfig);
        // service
        ServiceConfig serviceConfig = new ServiceConfig()
                .setPackagePath("xyz.easyboot.generator.demo.dao.service")
                .setFilePath("/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/java/xyz/easyboot/generator/demo/dao/service/");
        config.setService(serviceConfig);
        // serviceImpl
        ServiceImplConfig serviceImplConfig = new ServiceImplConfig()
                .setPackagePath("xyz.easyboot.generator.demo.dao.service.impl")
                .setFilePath("/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/java/xyz/easyboot/generator/demo/dao/service/impl/");
        config.setServiceImpl(serviceImplConfig);
        // 其他配置
        config.setOverride(false);

        // 输出
        GenerateExecutor.output(config, "live_room");
    }



}
