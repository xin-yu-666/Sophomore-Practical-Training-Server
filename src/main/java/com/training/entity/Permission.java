package com.training.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Permission {
    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 