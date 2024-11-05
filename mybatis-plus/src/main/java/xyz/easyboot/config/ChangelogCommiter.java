package xyz.easyboot.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author wuu
 */
@FunctionalInterface
public interface ChangelogCommiter {

    void commit(OperationResult operationResult);

    @Data
    public static class OperationResult {
        private String operation;
        private boolean recordStatus;
        private String tableName;
        private List<ChangedData> changedData;
        private long cost;

        @Data
        public static class ChangedData {
            private String pkColumnName;
            private int pkColumnVal;
            private List<OriginalColumnData> originalColumnDatas;
            private List<UpdatedColumn> updatedColumns;

            @Data
            public static class OriginalColumnData {
                private String columnName;
                private String originalValue;
                private String updateValue;
            }

            @Data
            public static class UpdatedColumn {
                private String columnName;
                private String originalValue;
                private String updateValue;
            }
        }
    }

}
