package com.lions.redisall.config;

import com.lions.redisall.interceptor.LoginInterceptor;
import com.lions.redisall.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Classname MVCConfig
 * @Description TODO
 * @Version 1.0.0
 * @Date 10/24/2023 9:53 PM
 * @Created by LIONS7
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private RefreshTokenInterceptor refreshTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有需要登录的接口
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/blog/**",
                        "/user/code",
                        "/user/login"
                ).order(1);
        // 拦截所有接口
        registry.addInterceptor(refreshTokenInterceptor)
                .addPathPatterns("/**").order(0);
    }
}
