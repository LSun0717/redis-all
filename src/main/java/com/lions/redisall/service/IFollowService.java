package com.lions.redisall.service;

import com.lions.redisall.dto.Result;
import com.lions.redisall.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * IFollowService业务逻辑层
 */
public interface IFollowService extends IService<Follow> {

    /**
     * 是否关注状态api
     * @param id 用户id
     * @param isFollow 关注 or 取关
     * @return 修改结果
     */
    Result changeFollowStatus(Long id, boolean isFollow);

    /**
     * 判断是否关注
     * @param id 指定用户id
     * @return json
     */
    Result isFollow(Long id);
}
