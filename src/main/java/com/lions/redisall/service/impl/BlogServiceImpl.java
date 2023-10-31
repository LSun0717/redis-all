package com.lions.redisall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.dto.Result;
import com.lions.redisall.dto.UserDTO;
import com.lions.redisall.entity.Blog;
import com.lions.redisall.entity.User;
import com.lions.redisall.mapper.BlogMapper;
import com.lions.redisall.mapper.UserMapper;
import com.lions.redisall.service.IBlogService;
import com.lions.redisall.service.IUserService;
import com.lions.redisall.utils.SystemConstants;
import com.lions.redisall.utils.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lions.redisall.utils.RedisConstants.BLOG_LIKED_KEY;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询博客信息
     * @param id 博客id
     * @return 博客信息+用户信息
     */
    @Override
    public Result queryBlogInfoById(Long id) {
        // 1.查询博客信息
        Blog blog = blogMapper.queryBlogById(id);
        if (blog == null) {
            return Result.fail("博客不存在");
        }
        // 2.查询博客发布者信息
        Long userId = blog.getUserId();
        User user = userMapper.queryUserById(userId);
        boolean likedByMe = isLikedByMe(id);
        // 3.组装BlogVO返回控制器
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
        blog.setIsLike(BooleanUtil.isTrue(likedByMe));
        return Result.ok(blog);
    }

    /**
     * 查询热门博客信息
     *
     * @param current 分页查询当前页码
     * @return 当前页博客
     */
    @Override
    public Result queryHotBlogInfo(Integer current) {
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog ->{
            Long userId = blog.getUserId();
            User user = userService.getById(userId);
            blog.setName(user.getNickName());
            blog.setIcon(user.getIcon());
            boolean likedByMe = this.isLikedByMe(blog.getId());
            if (likedByMe) {
                blog.setIsLike(true);
            }
        });
        return Result.ok(records);
    }

    /**
     * 点赞业务接口
     *
     * @param id 博客id
     * @return json
     */
    @Override
    @Transactional
    public Result likeBlog(Long id) {
        UserDTO userDTO = UserContext.getUser();
        Long userId = userDTO.getId();
        String key = BLOG_LIKED_KEY + id;
        boolean isLiked = isLikedByMe(id);
        int likeNum = Boolean.TRUE.equals(isLiked) ? -1 : 1;
        // 2.1 如果未点赞，可以点赞
        if (Boolean.FALSE.equals(isLiked)) {
            stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
        }
        // 2.2 如果已点赞，取消点赞
        if (Boolean.TRUE.equals(isLiked)) {
            stringRedisTemplate.opsForZSet().remove(key, userId.toString());
        }
        // 3.更新数据库点赞数量
        blogMapper.updateLikedById(id, likeNum);
        return Result.ok();
    }

    /**
     * 查询指定博客的所有点赞者
     *
     * @param id 博客id
     * @return UserDto列表
     */
    @Override
    public Result queryBlogLikes(Long id) {
        // 获取排名前五的点赞用户
        String key = BLOG_LIKED_KEY + id;
        Set<String> likedUserKeys = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (likedUserKeys == null || likedUserKeys.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        // 批量查询User
        List<Long> userIds = likedUserKeys.stream().map(Long::valueOf).collect(Collectors.toList());
        List<User> users = userMapper.selectBatchByIds(userIds);
        // User -> UserDTO
        List<UserDTO> userDTOS = users
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }

    /**
     * 判断当前博客是否被我点赞
     * 用于控制前端按钮高亮
     * @param id 博客id
     */
    private boolean isLikedByMe(Long id) {
        // 1.获取登录用户
        UserDTO userDTO = UserContext.getUser();
        if (userDTO == null) {
            return false;
        }
        Long userId = userDTO.getId();
        String key = BLOG_LIKED_KEY + id;
        // 2.判断当前用户是否已经点过赞
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        return score != null;
    }
}
