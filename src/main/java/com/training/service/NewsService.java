package com.training.service;

import com.training.entity.News;

import java.util.List;

public interface NewsService {
    List<News> getList(String title, String summary, String author, Long userId, Integer status);
    
    // 新增：分页查询方法
    List<News> getListByPage(String title, String summary, String author, Long userId, Integer status, int offset, int size);
    
    // 新增：统计总数方法
    int countByCondition(String title, String summary, String author, Long userId, Integer status);
    
    News getById(Long id);
    boolean add(News news);
    boolean update(News news);
    boolean delete(Long id);
    void updateImage(Long id, byte[] imageBytes);
} 