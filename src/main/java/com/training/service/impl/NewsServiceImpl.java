package com.training.service.impl;

import com.training.entity.News;
import com.training.mapper.NewsMapper;
import com.training.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<News> getList(String title, String summary, String author, Long userId, Integer status) {
        return newsMapper.findAll(title, summary, author, userId, status);
    }

    @Override
    public News getById(Long id) {
        return newsMapper.findById(id);
    }

    @Override
    public boolean add(News news) {
        news.setCreateTime(LocalDateTime.now());
        return newsMapper.insert(news) > 0;
    }

    @Override
    public boolean update(News news) {
        return newsMapper.update(news) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return newsMapper.delete(id) > 0;
    }
} 