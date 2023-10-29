package com.lions.redisall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lions.redisall.mapper")
@SpringBootApplication
public class RedisAllApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisAllApplication.class, args);
    }

}
