package com.training.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Meeting {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private String creator;
    private String content;
    private LocalDateTime createTime;
    private Long userId; // 发布者ID
    private Integer status; // 0-待审核 1-已通过 2-已拒绝
} 