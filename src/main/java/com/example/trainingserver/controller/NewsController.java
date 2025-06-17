package com.example.trainingserver.controller;

import com.example.trainingserver.entity.News;
import com.example.trainingserver.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String title,
                                    @RequestParam(required = false) String summary,
                                    @RequestParam(required = false) String author) {
        List<News> list = newsService.getList(title, summary, author);
        return Map.of("success", true, "data", Map.of("list", list, "total", list.size()));
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        return Map.of("success", true, "data", newsService.getById(id));
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody News news) {
        boolean ok = newsService.add(news);
        return Map.of("success", ok);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        boolean ok = newsService.update(news);
        return Map.of("success", ok);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = newsService.delete(id);
        return Map.of("success", ok);
    }
}
