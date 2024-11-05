package xyz.easyboot.generator.bean;

import lombok.Data;
import java.util.List;

@Data
public class EnumParamDefinition {

    private String tableName;
    private List<Field> fields;

    @Data
    public static class Field {
        private String fieldName;
        private String comment;
    }
}