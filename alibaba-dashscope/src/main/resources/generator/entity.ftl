package ${config.entity.packagePath};

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
<#list config.entity.extraImportList as item>
${item}
</#list>

/**
 * ${table.comment}
 * @author code-generator
 */
@TableName(value ="${table.tableName}")
@Data
public class ${table.upperCamelTableName} implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

${table.outputColumns}

}