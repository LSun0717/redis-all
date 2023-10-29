package com.lions.redisall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lions.redisall.dto.LoginFormDTO;
import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.User;

/**
 * IUserService
 * 业务逻辑层
 */
public interface IUserService extends IService<User> {

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 发送成功
     */
    Result sendCode(String phone);

    /**
     * 用户登录与初次登录注册
     * @param loginForm 请求体封装
     * @return 返回Token
     */
    Result login(LoginFormDTO loginForm);
}
