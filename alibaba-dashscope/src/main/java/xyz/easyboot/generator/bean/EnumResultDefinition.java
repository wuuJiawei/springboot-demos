package xyz.easyboot.generator.bean;

import lombok.Data;

import java.util.List;

@Data
public class EnumResultDefinition {

    private String comment;
    private String className;
    private List<EnumItem> items;
    private Boolean isEnum;
    private String fieldName;
    private String tableName;

    @Data
    public static class EnumItem {
        private String name;
        private String comment;
        private String code;
    }
}