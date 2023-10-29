package com.lions.redisall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.dto.LoginFormDTO;
import com.lions.redisall.dto.Result;
import com.lions.redisall.dto.UserDTO;
import com.lions.redisall.entity.User;
import com.lions.redisall.mapper.UserMapper;
import com.lions.redisall.service.IUserService;
import com.lions.redisall.utils.RedisConstants;
import com.lions.redisall.utils.RegexUtils;
import com.lions.redisall.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone) {
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
        String vCode = RandomUtil.randomNumbers(6);
        // redis存储验证码，并设置过期时间
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, vCode, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 替换为阿里云短信服务
        log.debug("发送验证码成功，验证码为：{}", vCode);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm) {
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
        String cacheVCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        String vCode = loginForm.getCode();

        if (cacheVCode == null || !cacheVCode.equals(vCode)) {
            return Result.fail("验证码错误");
        }
        // 初次登录，创建用户且在Redis中存储用户信息
        User user = userMapper.findUserByPhone(phone);
        if (user == null) {
            user = createUserWithPhone(phone);
        }
        // 自定义键值映射，解决Long id无法序列化为String的问题
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // Redis存储用户信息，及登录时长
        String token = UUID.randomUUID().toString(true);
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        return Result.ok(token);
    }

    /**
     * 根据手机号新建用户并保存
     * @param phone 手机号
     * @return 新建用户
     */
    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        save(user);
        return user;
    }

    /**
     * 获取分布式锁
     * @param key Key
     * @return 是否加锁成功
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放分布式锁
     * @param key 锁的Key
     * @return 是否释放锁成功
     */
    private boolean unLock(String key) {
        Boolean flag = stringRedisTemplate.delete(key);
        return BooleanUtil.isTrue(flag);
    }
}
