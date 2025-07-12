package com.training.util;

import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;

public class PasswordUtil {
    private static final String SALT = "training";
    private static final boolean DEV_MODE = true; // 开发模式标志

    public static String encrypt(String password) {
        if (DEV_MODE) {
            return password; // 开发模式下不加密
        }
        return DigestUtils.md5DigestAsHex((password + SALT).getBytes(StandardCharsets.UTF_8));
    }

    public static boolean match(String rawPassword, String encodedPassword) {
        if (DEV_MODE) {
            return rawPassword.equals(encodedPassword); // 开发模式下直接比较
        }
        return encrypt(rawPassword).equals(encodedPassword);
    }
} 