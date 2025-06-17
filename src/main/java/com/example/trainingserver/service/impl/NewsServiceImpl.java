package com.example.trainingserver.service.impl;

import com.example.trainingserver.entity.News;
import com.example.trainingserver.mapper.NewsMapper;
import com.example.trainingserver.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<News> getList(String title, String summary, String author) {
        return newsMapper.findAll(title, summary, author);
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
