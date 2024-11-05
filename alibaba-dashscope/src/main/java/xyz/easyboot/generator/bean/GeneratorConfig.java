package xyz.easyboot.generator.bean;

import cn.hutool.db.ds.simple.SimpleDataSource;
import lombok.Data;

@Data
public class GeneratorConfig {

    private EntityConfig entity = new EntityConfig();

    private MapperConfig mapper = new MapperConfig();

    private XmlConfig xml = new XmlConfig();

    private ServiceConfig service = new ServiceConfig();

    private ServiceImplConfig serviceImpl = new ServiceImplConfig();

    private EnumsConfig enums = new EnumsConfig();

    private String basePath = "/Users/wuu/Projects/opensource/springboot-demos/alibaba-dashscope/src/main/java/";

    private String basePackage = "xyz.easyboot.generator.demo";

    /** 是否覆盖 */
    private Boolean override = false;

    private SimpleDataSource dataSource;


}
