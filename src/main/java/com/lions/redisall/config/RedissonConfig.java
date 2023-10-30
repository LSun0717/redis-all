package com.lions.redisall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname RedissonConfig
 * @Description Redisson配置类
 * @Version 1.0.0
 * @Date 10/30/2023 3:52 PM
 * @Created by LIONS7
 */

/**
 * Redisson配置列
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.1.106:6379")
                .setPassword("root")
                .setConnectionPoolSize(50)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(3000)
                .setTimeout(3000)
                .setDatabase(5);
        return Redisson.create(config);
    }
}
