package com.training.config;

import com.training.interceptor.JwtInterceptor;
import com.training.interceptor.PermissionInterceptor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    // 文件上传目录配置
    @Value("${upload.base-dir:uploads}")
    private String uploadBaseDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/user/register",
                    "/api/auth/enterprise/register",
                    "/api/auth/reset-password",
                    "/api/auth/verify-code",
                    "/api/captcha",
                    "/api/upload",
                    "/uploads/**",
                    "/api/enterprises/public",
                    "/api/users/*/avatar",
                    "/api/news/*/image",
                    "/error"
                );

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/user/register",
                    "/api/auth/enterprise/register",
                    "/api/auth/reset-password",
                    "/api/auth/verify-code",
                    "/api/captcha",
                    "/api/upload",
                    "/uploads/**",
                    "/api/enterprises/public",
                    "/api/users/*/avatar",
                    "/api/news/*/image",
                    "/error"
                );
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问映射，支持上传文件的访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadBaseDir + "/");
    }
} 