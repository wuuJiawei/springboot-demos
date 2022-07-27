package com.example.jsonserialization.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author wujiawei
 * @see
 * @since 2022/6/6 08:53
 */
@Configuration
public class JsonMessageConverter {

    @Bean
    @ConditionalOnMissingBean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    
        ObjectMapper objectMapper = new ObjectMapper();
        
        
        converter.setObjectMapper();
        return converter;
    }
    
}
