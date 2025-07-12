package com.training.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Enterprise {
    private Long id;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private Integer status; // 0-禁用 1-启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 