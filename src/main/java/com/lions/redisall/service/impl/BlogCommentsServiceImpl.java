package com.lions.redisall.service.impl;

import com.lions.redisall.entity.BlogComments;
import com.lions.redisall.mapper.BlogCommentsMapper;
import com.lions.redisall.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
