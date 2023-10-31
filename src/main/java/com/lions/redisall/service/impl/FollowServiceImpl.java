package com.lions.redisall.service.impl;

import com.lions.redisall.dto.Result;
import com.lions.redisall.dto.UserDTO;
import com.lions.redisall.entity.Follow;
import com.lions.redisall.mapper.FollowMapper;
import com.lions.redisall.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lions.redisall.utils.UserContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private FollowMapper followMapper;

    /**
     * 是否关注状态api
     *
     * @param otherUserId       用户id
     * @param isFollow 关注 or 取关
     * @return 修改结果
     */
    @Override
    public Result changeFollowStatus(Long otherUserId, boolean isFollow) {
        UserDTO user = UserContext.getUser();
        Long meId = user.getId();
        // 关注执行
        if (isFollow) {
            Follow follow = new Follow();
            follow.setUserId(meId);
            follow.setFollowUserId(otherUserId);
            followMapper.insertFollow(follow);
        } else {
            // 取关执行
            followMapper.deleteFollow(otherUserId, meId);
        }
        return Result.ok();
    }

    /**
     * 判断是否关注
     *
     * @param bloggerId 博主id
     * @return json
     */
    @Override
    public Result isFollow(Long bloggerId) {
        UserDTO userContext = UserContext.getUser();
        Long fansId = userContext.getId();
        // 查询Follow表中是否存在关注记录
        int validNum = followMapper.selectIsFollow(bloggerId, fansId);
        boolean result = validNum > 0;
        return Result.ok(result);
    }
}
