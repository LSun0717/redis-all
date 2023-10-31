package com.lions.redisall.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lions.redisall.dto.Result;
import com.lions.redisall.dto.UserDTO;
import com.lions.redisall.entity.Blog;
import com.lions.redisall.service.IBlogService;
import com.lions.redisall.utils.SystemConstants;
import com.lions.redisall.utils.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * BlogController API
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    /**
     * 博客信息查询api
     * @param id 博客id
     * @return 博客信息+部分用户信息
     */
    @GetMapping("/{id}")
    public Result getBlogInfoById(@PathVariable("id") Long id) {
        return blogService.queryBlogInfoById(id);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlogInfo(current);
    }

    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        // 获取登录用户
        UserDTO user = UserContext.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        blogService.save(blog);
        // 返回id
        return Result.ok(blog.getId());
    }

    /**
     * 点赞api
     * @param id 博客id
     * @return json
     */
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改点赞数量
        return blogService.likeBlog(id);
    }

    /**
     * 获取指定博客的所有点赞者
     * @param id id
     * @return json
     */
    @GetMapping("/likes/{id}")
    public Result getBlogLikes(@PathVariable("id") Long id) {
        return blogService.queryBlogLikes(id);
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserContext.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }
}
