package com.lions.redisall.service;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IBlogService业务逻辑层
 */
public interface IBlogService extends IService<Blog> {

    /**
     * 查询博客信息
     * @param id 博客id
     * @return 博客信息+用户信息
     */
    Result queryBlogInfoById(Long id);

    /**
     * 查询热门博客信息
     * @param current
     * @return
     */
    Result queryHotBlogInfo(Integer current);

    /**
     * 点赞业务接口
     * @param id 博客id
     * @return json
     */
    Result likeBlog(Long id);

    /**
     * 查询指定博客的所有点赞者
     * @param id 博客id
     * @return UserDto列表
     */
    Result queryBlogLikes(Long id);
}
