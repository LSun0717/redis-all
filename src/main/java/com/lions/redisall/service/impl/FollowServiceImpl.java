package com.lions.redisall.service.impl;

import com.lions.redisall.entity.Follow;
import com.lions.redisall.mapper.FollowMapper;
import com.lions.redisall.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

}
