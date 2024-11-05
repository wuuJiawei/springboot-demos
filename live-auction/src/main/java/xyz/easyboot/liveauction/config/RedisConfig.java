package xyz.easyboot.liveauction.config;

import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedisConfig {

//    public RedissonAutoConfiguration config(Environment environment) {
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
//        config.setCodec(new JsonJacksonCodec());
//        return config;
//    }

//    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
        return new RedissonAutoConfigurationCustomizer() {
            @Override
            public void customize(Config config) {
                config.setCodec(new JsonJacksonCodec());
            }
        };
    }

}
