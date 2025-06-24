package com.training.service;

import com.training.entity.News;

import java.util.List;

public interface NewsService {
    List<News> getList(String title, String summary, String author);
    News getById(Long id);
    boolean add(News news);
    boolean update(News news);
    boolean delete(Long id);
} 