package com.example.handle;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author wujiawei
 * @see
 * @since 2022/6/27 11:05
 */
@Component
public class WebAppConfig implements WebMvcConfigurer {
    
    @Resource
    private ValidSignInterceptor decryptInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(decryptInterceptor).addPathPatterns("/**");
    }
}
