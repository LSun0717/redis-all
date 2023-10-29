package com.lions.redisall.dto;

import lombok.Data;

/**
 * LoginFormDTO
 */
@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
