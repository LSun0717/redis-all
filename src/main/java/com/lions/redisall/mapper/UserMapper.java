package com.lions.redisall.mapper;

import com.lions.redisall.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserMapper
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    User findUserByPhone(@Param("phone") String phone);

    /**
     * 根据id查询用户信息
     * @param id 用户id
     * @return UserDO
     */
    User queryUserById(@Param("id") Long id);

    /**
     * 批量根据id查询
     * @param userIds id列表
     * @return 批量User
     */
    List<User> selectBatchByIds(@Param("userIds") List<Long> userIds);
}
