package com.training.controller;

import com.training.common.Result;
import com.training.entity.Enterprise;
import com.training.entity.Permission;
import com.training.entity.User;
import com.training.service.EnterpriseService;
import com.training.service.UserService;
import com.training.mapper.PermissionMapper;
import com.training.mapper.UserMapper;
import com.training.mapper.UserRoleMapper;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private EnterpriseService enterpriseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @GetMapping("/check-data")
    public Result<Map<String, Object>> checkData() {
        Map<String, Object> result = new HashMap<>();
        
        // 检查所有企业
        List<Enterprise> enterprises = enterpriseService.findAll();
        result.put("enterprises", enterprises);
        
        // 检查所有用户
        List<User> users = userService.findAll();
        result.put("users", users);
        
        // 检查test1用户
        User test1User = userService.findByUsername("test1");
        result.put("test1User", test1User);
        
        if (test1User != null && test1User.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseService.findById(test1User.getEnterpriseId());
            result.put("test1Enterprise", enterprise);
        }
        
        return Result.success(result);
    }
    
    @GetMapping("/test-join")
    public Result<User> testJoin() {
        User user = userService.findByUsernameWithEnterprise("test1");
        return Result.success(user);
    }

    @GetMapping("/user-permissions")
    public Result<Map<String, Object>> getUserPermissions(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String username = JwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return Result.error("无效的token");
        }
        
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 获取用户权限
        List<Permission> permissions = permissionMapper.findByUserId(user.getId());
        
        // 获取用户角色
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", username);
        result.put("roles", roles);
        result.put("permissions", permissions);
        result.put("hasProfileManage", permissions.stream().anyMatch(p -> "PROFILE_MANAGE".equals(p.getCode())));
        
        return Result.success(result);
    }
    
    @GetMapping("/test-change-password")
    public Result<String> testChangePasswordPermission(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String username = JwtUtil.getUsernameFromToken(token);
        if (username == null) {
            return Result.error("无效的token");
        }
        
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        List<Permission> permissions = permissionMapper.findByUserId(user.getId());
        boolean hasPermission = permissions.stream().anyMatch(p -> "PROFILE_MANAGE".equals(p.getCode()));
        
        if (hasPermission) {
            return Result.success("用户有修改密码权限");
        } else {
            return Result.error("用户没有修改密码权限");
        }
    }
} 