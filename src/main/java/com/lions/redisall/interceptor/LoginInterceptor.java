package com.lions.redisall.interceptor;

import com.lions.redisall.dto.UserDTO;
import com.lions.redisall.utils.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname LoginInterceptor
 * @Description 第二道拦截器，判断是否登录
 * @Version 1.0.0
 * @Date 10/24/2023 9:48 PM
 * @Created by LIONS7
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDTO userDTO = UserContext.getUser();
        if (userDTO == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
