package com.lions.redisall.utils;

import com.lions.redisall.dto.UserDTO;

/**
 * ThreadLocal 线程私有context
 */
public class UserContext {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
