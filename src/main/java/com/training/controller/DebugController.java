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
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

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

    @PostMapping("/ai-generate")
    public Map<String, Object> aiGenerate(@RequestBody Map<String, String> body) {
        String keywords = body.getOrDefault("keywords", "");
        String apiUrl = "http://127.0.0.1/v1/completion-messages";
        String apiKey = "app-mfvWeaQwLwynnPbTdlFWbQiu"; 

        // 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> inputs = new HashMap<>();
        inputs.put("keywords", keywords);
        requestBody.put("inputs", inputs);
        requestBody.put("response_mode", "blocking");
        requestBody.put("user", "user-001");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> result = new HashMap<>();
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object choicesObj = response.getBody().get("choices");
                if (choicesObj instanceof java.util.List) {
                    java.util.List choices = (java.util.List) choicesObj;
                    if (!choices.isEmpty()) {
                        Object choiceObj = choices.get(0);
                        if (choiceObj instanceof Map) {
                            Map choice = (Map) choiceObj;
                            Object messageObj = choice.get("message");
                            if (messageObj instanceof Map) {
                                Map message = (Map) messageObj;
                                result.put("generated", message.get("content"));
                            }
                        }
                    }
                }
            } else {
                result.put("generated", "AI生成失败");
            }
        } catch (Exception e) {
            result.put("generated", "AI生成异常: " + e.getMessage());
        }
        return result;
    }
} 