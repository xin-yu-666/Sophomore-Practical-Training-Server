package com.training.interceptor;

import com.training.annotation.RequirePermission;
import com.training.entity.Permission;
import com.training.entity.User;
import com.training.mapper.PermissionMapper;
import com.training.mapper.UserMapper;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 处理 Bearer token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = JwtUtil.getUsernameFromToken(token);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 根据用户名查找用户ID
        User user = userMapper.findByUsername(username);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        List<Permission> permissions = permissionMapper.findByUserId(user.getId());
        String requiredPermission = requirePermission.value();

        for (Permission permission : permissions) {
            if (permission.getCode().equals(requiredPermission)) {
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
} 