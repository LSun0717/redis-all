package com.lions.redisall.mapper;

import com.lions.redisall.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * UserMapper
 */
public interface UserMapper extends BaseMapper<User> {

    User findUserByPhone(String phone);
}
