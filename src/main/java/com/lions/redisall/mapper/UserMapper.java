package com.lions.redisall.mapper;

import com.lions.redisall.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * UserMapper
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    User findUserByPhone(String phone);
}
