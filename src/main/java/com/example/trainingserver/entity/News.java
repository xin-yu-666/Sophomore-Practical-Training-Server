package com.example.trainingserver.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class News {
    private Long id;
    private String title;
    private String summary;
    private String author;
    private String imageUrl;
    private String content;
    private LocalDateTime createTime;
}
