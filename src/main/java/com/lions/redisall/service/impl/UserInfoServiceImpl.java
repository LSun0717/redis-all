package com.lions.redisall.service.impl;

import com.lions.redisall.entity.UserInfo;
import com.lions.redisall.mapper.UserInfoMapper;
import com.lions.redisall.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
