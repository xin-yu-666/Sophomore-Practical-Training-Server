package com.training.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Industry {
    private Long id;
    private String title;
    private String category;
    private String content;
    private String author;
    private Integer status;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 