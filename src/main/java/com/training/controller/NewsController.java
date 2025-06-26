package com.training.controller;

import com.training.entity.News;
import com.training.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import com.training.annotation.RequirePermission;
import javax.servlet.http.HttpServletRequest;
import com.training.mapper.UserMapper;
import com.training.entity.User;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserMapper userMapper;

    // 导出新闻数据
    @GetMapping("/news/export")
    public void exportNews(HttpServletResponse response) throws IOException {
        try {
            // 获取所有新闻数据
            List<News> newsList = newsService.getList(null, null, null, null, null);
            
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
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("message", "请选择要上传的图片");
                return map;
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("message", "只支持jpg/png格式的图片");
                return map;
            }

            // 检查文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("message", "图片大小不能超过2MB");
                return map;
            }

            // 创建上传目录
            String uploadDir = "uploads/images/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("message", "文件名无效，缺少扩展名");
                return map;
            }
            String filename = System.currentTimeMillis() + extension;
            
            // 保存文件，若已存在则覆盖
            Path filePath = Paths.get(uploadDir + filename);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 返回图片访问URL
            String imageUrl = "/api/images/" + filename;
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("url", imageUrl);
            map.put("message", "图片上传成功");
            return map;
            
        } catch (IOException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "图片上传失败: " + e.getMessage());
            return map;
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
            List<News> list = newsService.getList(null, null, null, null, null);
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("message", "数据库连接正常");
            map.put("count", list.size());
            map.put("data", list);
            return map;
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "数据库连接失败: " + e.getMessage());
            map.put("error", e.toString());
            return map;
        }
    }

    @GetMapping("/news")
    @RequirePermission("NEWS_VIEW")
    public Map<String, Object> list(@RequestParam(required = false) String title,
                                    @RequestParam(required = false) String summary,
                                    @RequestParam(required = false) String author,
                                    @RequestParam(required = false) Long userId,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String username = com.training.util.JwtUtil.getUsernameFromToken(token);
            User user = userMapper.findByUsername(username);
            List<String> roles = userMapper.findRolesByUserId(user.getId());
            boolean isAdmin = false;
            for (String role : roles) {
                if ("ROLE_ADMIN".equals(role)) {
                    isAdmin = true;
                    break;
                }
            }
            List<News> list;
            if (isAdmin) {
                // 管理员查全部或按userId查
                list = newsService.getList(title, summary, author, userId, null);
            } else {
                // 企业用户查所有已通过审核的动态，或查自己发布的
                if (userId != null && userId.equals(user.getId())) {
                    list = newsService.getList(title, summary, author, user.getId(), null);
                } else {
                    list = newsService.getList(title, summary, author, null, 1);
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", list.size());
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("data", data);
            return map;
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }

    @GetMapping("/news/{id}")
    @RequirePermission("NEWS_VIEW")
    public Map<String, Object> detail(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        News news = newsService.getById(id);
        if (news == null) {
            map.put("success", false);
            map.put("message", "新闻不存在");
            return map;
        }
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin && !Objects.equals(news.getUserId(), user.getId()) && !Objects.equals(news.getStatus(), 1)) {
            map.put("success", false);
            map.put("message", "无权查看该新闻");
            return map;
        }
        map.put("success", true);
        map.put("data", news);
        return map;
    }

    @PostMapping("/news")
    @RequirePermission("NEWS_PUBLISH")
    public Map<String, Object> add(@RequestBody News news, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        news.setUserId(user.getId());
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        news.setStatus(isAdmin ? 1 : 0);
        boolean ok = newsService.add(news);
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        return map;
    }

    @PutMapping("/news/{id}")
    @RequirePermission("NEWS_EDIT")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody News news, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        News old = newsService.getById(id);
        if (old == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "新闻不存在");
            return map;
        }
        if (!isAdmin && !Objects.equals(old.getUserId(), user.getId())) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "无权编辑该新闻");
            return map;
        }
        news.setId(id);
        news.setUserId(old.getUserId());
        news.setStatus(old.getStatus());
        boolean ok = newsService.update(news);
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        return map;
    }

    @DeleteMapping("/news/{id}")
    @RequirePermission("NEWS_DELETE")
    public Map<String, Object> delete(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        News old = newsService.getById(id);
        if (old == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "新闻不存在");
            return map;
        }
        if (!isAdmin && !Objects.equals(old.getUserId(), user.getId())) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "无权删除该新闻");
            return map;
        }
        boolean ok = newsService.delete(id);
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        return map;
    }

    @PutMapping("/news/audit/{id}")
    @RequirePermission("NEWS_AUDIT")
    public Map<String, Object> audit(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "无权审核新闻");
            return map;
        }
        News news = newsService.getById(id);
        if (news == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "新闻不存在");
            return map;
        }
        if (status != 1 && status != 2) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "状态值非法");
            return map;
        }
        news.setStatus(status);
        boolean ok = newsService.update(news);
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        return map;
    }

    // 管理员专用：查所有待审核动态
    @GetMapping("/news/pending")
    @RequirePermission("NEWS_AUDIT")
    public Map<String, Object> pendingList(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        Map<String, Object> map = new HashMap<>();
        if (!isAdmin) {
            map.put("success", false);
            map.put("message", "无权访问");
            return map;
        }
        List<News> list = newsService.getList(null, null, null, null, 0);
        map.put("success", true);
        map.put("data", list);
        return map;
    }
} 