package com.training.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RolePermission {
    private Long id;
    private Long roleId;
    private Long permissionId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 