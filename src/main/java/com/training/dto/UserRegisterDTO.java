package com.training.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String gender;
    private String avatar; // Base64格式的头像数据
    private Long enterpriseId;
    private Integer status;
} 