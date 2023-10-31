package com.lions.redisall.controller;


import com.lions.redisall.dto.Result;
import com.lions.redisall.service.IFollowService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * FollowController API
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private IFollowService followService;

    /**
     * 是否关注状态api
     * @param id 用户id
     * @param isFollow 关注 or 取关
     * @return 修改结果
     */
    @PutMapping("/{id}/{isFollow}")
    public Result changeFollowStatus(@PathVariable("id") Long id, @PathVariable("isFollow") boolean isFollow) {
        return followService.changeFollowStatus(id, isFollow);
    }

    /**
     * 判断是否关注
     * @param id 指定用户id
     * @return json
     */
    @GetMapping("/or/not/{id}")
    public Result isFollow(@PathVariable("id") Long id) {
        return followService.isFollow(id);
    }
}
