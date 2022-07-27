package com.example.handle;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 接口鉴权
 * 方案一：注解+{@link HandlerInterceptor}
 * 方案二：Aspect
 * @author wujiawei
 * @see
 * @since 2022/6/27 10:31
 */
@Slf4j
@Component
public class ValidSignInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Encrypt encrypt = ((HandlerMethod) handler).getMethodAnnotation(Encrypt.class);
        if (encrypt == null) {
            return true;
        }
    
        Map<String, String> paramMap = ServletUtil.getParamMap(request);
        log.info(JSONUtil.toJsonStr(paramMap));
        
        // 检查时间戳（60秒内有效）
        String timestampStr = paramMap.get("timestamp");
        if (StrUtil.isEmpty(timestampStr)) {
            return false;
        }
        long timestamp = Long.parseLong(timestampStr);
        if (timestamp + 60 * 1000 < System.currentTimeMillis()) {
            return false;
        }
        
        // 签名请求结果
        String sign = paramMap.get("sign");
        if (StrUtil.isEmpty(sign)) {
            return false;
        }
        MapUtil.removeAny(paramMap, "sign");
        
        // 比对签名
        String newSign = sign(paramMap);
        return newSign.equals(sign);
    }
    
    private String sign(Map<String, String> params) {
        String sign = SecureUtil.md5(createLinkString(params));
        log.info(sign);
        return sign;
    }
    
    /**
     * 创建${key}=${value}的字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            // 拼接时，不包括最后一个&字符
            if (i == keys.size() - 1) {
                content.append(key + "=" + value);
            } else {
                content.append(key + "=" + value + "&");
            }
        }
        return content.toString();
    }
    
    
}
