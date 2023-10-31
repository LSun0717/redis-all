package com.lions.redisall.mapper;

import com.lions.redisall.entity.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * BlogMapper
 */
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 查询博客信息
     * @param id 博客id
     * @return BlogDO
     */
    Blog queryBlogById(Long id);

    /**
     * 点赞数量更新
     * @param id 博客id
     * @return 是否更新成功
     */
    boolean updateLikedById(@Param("id") Long id, @Param("likeNum") Integer likeNum);
}
