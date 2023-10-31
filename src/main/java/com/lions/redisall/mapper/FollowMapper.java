package com.lions.redisall.mapper;

import com.lions.redisall.entity.Follow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * FollowMapper
 */
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 新增关注关系
     * @param follow FollowDO
     * @return 是否插入成功
     */
    boolean insertFollow(Follow follow);

    /**
     * 取关逻辑
     * @param bloggerId 博主id
     * @param fansId 粉丝id
     * @return 是否取关成功
     */
    boolean deleteFollow(@Param("bloggerId") Long bloggerId, @Param("fansId") Long fansId);

    /**
     * 查询是否已关注指定博主
     *
     * @param bloggerId 博主id
     * @param fansId 粉丝id
     * @return 条数
     */
    int selectIsFollow(@Param("bloggerId") Long bloggerId, Long fansId);
}
