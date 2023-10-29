package com.lions.redisall.service.impl;

import com.lions.redisall.entity.Blog;
import com.lions.redisall.mapper.BlogMapper;
import com.lions.redisall.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}
