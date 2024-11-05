package xyz.easyboot.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeEnum {

    ONE(1),
    TWO(2),
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
}
