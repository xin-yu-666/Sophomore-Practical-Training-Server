package com.example.trainingserver.controller;

import com.example.trainingserver.entity.News;
import com.example.trainingserver.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;

    // 导出新闻数据
    @GetMapping("/news/export")
    public void exportNews(HttpServletResponse response) throws IOException {
        try {
            // 获取所有新闻数据
            List<News> newsList = newsService.getList(null, null, null);
            
            // 设置响应头
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=news_export_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");
            
            // 写入CSV头部
            OutputStream out = response.getOutputStream();
            String header = "ID,新闻标题,新闻简介,作者,图片URL,新闻内容,创建时间\n";
            out.write(header.getBytes("UTF-8"));
            
            // 写入数据
            for (News news : newsList) {
                String line = String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    news.getId(),
                    news.getTitle() != null ? news.getTitle().replace("\"", "\"\"") : "",
                    news.getSummary() != null ? news.getSummary().replace("\"", "\"\"") : "",
                    news.getAuthor() != null ? news.getAuthor().replace("\"", "\"\"") : "",
                    news.getImageUrl() != null ? news.getImageUrl().replace("\"", "\"\"") : "",
                    news.getContent() != null ? news.getContent().replace("\"", "\"\"") : "",
                    news.getCreateTime() != null ? news.getCreateTime().toString() : ""
                );
                out.write(line.getBytes("UTF-8"));
            }
            
            out.flush();
            out.close();
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("导出失败: " + e.getMessage());
        }
    }

    // 图片上传接口
    @PostMapping("/upload")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Map.of("success", false, "message", "请选择要上传的图片");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                return Map.of("success", false, "message", "只支持jpg/png格式的图片");
            }

            // 检查文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Map.of("success", false, "message", "图片大小不能超过2MB");
            }

            // 创建上传目录
            String uploadDir = "uploads/images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = System.currentTimeMillis() + extension;
            
            // 保存文件
            Path filePath = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回图片访问URL
            String imageUrl = "/api/images/" + filename;
            
            return Map.of(
                "success", true,
                "url", imageUrl,
                "message", "图片上传成功"
            );
            
        } catch (IOException e) {
            return Map.of("success", false, "message", "图片上传失败: " + e.getMessage());
        }
    }

    // 图片访问接口
    @GetMapping("/images/{filename}")
    public byte[] getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get("uploads/images/" + filename);
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        }
        return new byte[0];
    }

    @GetMapping("/news/test")
    public Map<String, Object> test() {
        try {
            List<News> list = newsService.getList(null, null, null);
            return Map.of(
                "success", true, 
                "message", "数据库连接正常",
                "count", list.size(),
                "data", list
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "数据库连接失败: " + e.getMessage(),
                "error", e.toString()
            );
        }
    }

    @GetMapping("/news")
    public Map<String, Object> list(@RequestParam(required = false) String title,
                                    @RequestParam(required = false) String summary,
                                    @RequestParam(required = false) String author,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<News> list = newsService.getList(title, summary, author);
            return Map.of("success", true, "data", Map.of("list", list, "total", list.size()));
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @GetMapping("/news/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        return Map.of("success", true, "data", newsService.getById(id));
    }

    @PostMapping("/news")
    public Map<String, Object> add(@RequestBody News news) {
        boolean ok = newsService.add(news);
        return Map.of("success", ok);
    }

    @PutMapping("/news/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        boolean ok = newsService.update(news);
        if (ok) {
            return Map.of("success", true);
        } else {
            return Map.of("success", false, "message", "修改失败，可能是数据不存在或数据库错误");
        }
    }

    @DeleteMapping("/news/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = newsService.delete(id);
        if (ok) {
            return Map.of("success", true);
        } else {
            return Map.of("success", false, "message", "删除失败，可能是数据不存在或数据库错误");
        }
    }
}
