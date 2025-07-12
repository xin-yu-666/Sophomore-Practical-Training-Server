package com.training.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    // 文件保存根目录，可在 application.properties 配置
    @Value("${upload.base-dir:uploads}")
    private String baseDir;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();

        logger.info("开始处理文件上传，文件名: {}, 大小: {} bytes",
                file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件为空");
            logger.warn("上传文件为空");
            return result;
        }

        try {
            // 判断文件类型，决定保存到哪个子目录
            String contentType = file.getContentType();
            String subDir = "images";
            if (contentType != null && contentType.startsWith("video")) {
                subDir = "videos";
            }

            // 创建保存目录
            File saveDir = new File(baseDir, subDir);
            if (!saveDir.exists()) {
                boolean created = saveDir.mkdirs();
                if (!created) {
                    logger.error("无法创建目录: {}", saveDir.getAbsolutePath());
                    result.put("success", false);
                    result.put("message", "服务器存储目录创建失败");
                    return result;
                }
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;

            // 保存文件
            File dest = new File(saveDir, filename);
            file.transferTo(dest);

            // 返回可访问的URL
            String url = "/uploads/" + subDir + "/" + filename;
            result.put("success", true);
            result.put("url", url);
            result.put("filename", filename);

            logger.info("文件上传成功，保存路径: {}, 访问URL: {}",
                    dest.getAbsolutePath(), url);

            return result;

        } catch (Exception e) {
            logger.error("文件上传失败", e);
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            return result;
        }
    }
}
