package com.example.handle;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.example.dto.JsonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 响应结果加密
 * 方案一：注解+实现{@link ResponseBodyAdvice}
 * 方案二：Aspect
 * @author wujiawei
 * @see
 * @since 2022/6/27 09:36
 */
@ControllerAdvice
@Component
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<JsonResult> {
    
    @Value("${api.encrypt.key}")
    private String encryptKey;
    
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 标记了Encrypt的接口要进行返回结果加密
        Encrypt encrypt = returnType.getMethodAnnotation(Encrypt.class);
        if (encrypt != null) {
            return true;
        }
        return false;
    }
    
    @Override
    public JsonResult beforeBodyWrite(JsonResult body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        if (body.getData() == null) {
            return body;
        }
        // 仅加密内部的数据
        Object data = body.getData();
        String json = JSONUtil.toJsonStr(data);
        String result = SecureUtil.aes(encryptKey.getBytes()).encryptBase64(json);
        body.setData(result);
        return body;
    }
    
}
