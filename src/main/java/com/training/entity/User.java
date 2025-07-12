package com.training.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String gender;
    private byte[] avatar; // 头像BLOB数据
    private Long enterpriseId;
    private String enterpriseName; // 企业名称，仅用于查询返回
    private Integer status; // 0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 