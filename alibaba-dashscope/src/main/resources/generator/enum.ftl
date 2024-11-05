package ${config.enums.packagePath};

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ${enums.comment}
 *
 * @author code-generator
 */
@AllArgsConstructor
@Getter
public enum ${enums.className} {
<#list enums.items as item>

    /** ${item.comment} */
    ${item.name}(${item.code}),
</#list>
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
}

