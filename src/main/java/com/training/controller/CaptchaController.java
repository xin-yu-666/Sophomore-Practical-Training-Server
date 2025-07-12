package com.training.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping
    public void getCaptcha(HttpServletResponse response) throws Exception {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        // 生成验证码文本
        String text = defaultKaptcha.createText();
        // 生成验证码图片
        BufferedImage image = defaultKaptcha.createImage(text);

        // 生成验证码ID
        String captchaId = UUID.randomUUID().toString();
        // 将验证码保存到Redis，设置5分钟过期
        redisTemplate.opsForValue().set("captcha:" + captchaId, text, 5, TimeUnit.MINUTES);

        // 将验证码ID写入响应头
        response.setHeader("Captcha-Id", captchaId);

        // 输出图片
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
    }
} 