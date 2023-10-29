package com.lions.redisall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * SpringBoot启动类
 */
@MapperScan("com.lions.redisall.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class RedisAllApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisAllApplication.class, args);
    }

}
