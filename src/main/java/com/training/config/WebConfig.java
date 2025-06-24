package com.training.config;

import com.training.interceptor.JwtInterceptor;
import com.training.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private PermissionInterceptor permissionInterceptor;

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
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/user/register",
                    "/api/auth/verify-code",
                    "/api/captcha",
                    "/api/enterprises/public",
                    "/api/images/**",
                    "/error"
                );

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/user/register",
                    "/api/auth/verify-code",
                    "/api/captcha",
                    "/api/enterprises/public",
                    "/api/images/**",
                    "/error"
                );
    }
} 