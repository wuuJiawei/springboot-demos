package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wujiawei
 * @see
 * @since 2022/6/27 09:34
 */
@Data
@AllArgsConstructor
public class JsonResult<T> {
    
    private Integer code;
    private String message;
    private T data;
    
}
