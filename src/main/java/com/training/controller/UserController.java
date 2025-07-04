package com.training.controller;

import com.training.annotation.RequirePermission;
import com.training.common.Result;
import com.training.dto.ChangePasswordParams;
import com.training.entity.User;
import com.training.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null || username.isEmpty()) {
            return Result.error("用户名不能为空");
        }
        User user = userService.findByUsernameWithEnterprise(username);
        if (user != null) {
            user.setPassword(null); // 清除密码信息
        }
        // 查询角色
        List<String> roles = userService.findRolesByUserId(user.getId());
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("user", user);
        result.put("roles", roles);
        return Result.success(result);
    }

    @PutMapping("/current")
    @RequirePermission("PROFILE_MANAGE")
    public Result<Void> updateCurrentUser(@Valid @RequestBody User user, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        
        if (username == null || username.isEmpty()) {
            return Result.error("用户名不能为空");
        }
        
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        // 只允许更新部分字段
        currentUser.setNickname(user.getNickname());
        currentUser.setPhone(user.getPhone());
        currentUser.setEmail(user.getEmail());
        currentUser.setGender(user.getGender());
        
        userService.update(currentUser);
        return Result.success();
    }

    @GetMapping
    @RequirePermission("USER_MANAGE")
    public Result<Map<String, Object>> list(@RequestParam(required = false) String username,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) Integer status,
                                 @RequestParam(required = false) String enterpriseName,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        List<User> users = userService.findByCondition(username, phone, status, enterpriseName);
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        
        // 简单的分页处理
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, users.size());
        List<User> pageUsers = users.subList(Math.max(0, start), end);
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", pageUsers);
        result.put("total", users.size());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @RequirePermission("USER_MANAGE")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null); // 清除密码信息
        }
        return Result.success(user);
    }

    @PostMapping
    @RequirePermission("USER_MANAGE")
    public Result<Void> create(@Valid @RequestBody User user) {
        userService.create(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequirePermission("USER_MANAGE")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            System.out.println("收到更新用户请求 - ID: " + id);
            System.out.println("请求体: " + user);
            
            user.setId(id);
            userService.update(user);
            System.out.println("用户更新成功 - ID: " + id);
            return Result.success();
        } catch (Exception e) {
            System.err.println("更新用户失败 - ID: " + id + ", 错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("USER_MANAGE")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @RequirePermission("USER_MANAGE")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> rawIds = (List<Object>) request.get("ids");
            
            if (rawIds == null || rawIds.isEmpty()) {
                return Result.error("请选择要删除的用户");
            }
            
            // 转换ID类型，兼容前端传递的number类型
            for (Object rawId : rawIds) {
                Long id;
                if (rawId instanceof Integer) {
                    id = ((Integer) rawId).longValue();
                } else if (rawId instanceof Long) {
                    id = (Long) rawId;
                } else if (rawId instanceof String) {
                    id = Long.parseLong((String) rawId);
                } else {
                    id = Long.valueOf(rawId.toString());
                }
                
                userService.deleteById(id);
            }
            return Result.success();
        } catch (Exception e) {
            System.err.println("批量删除用户失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("批量删除用户失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @RequirePermission("USER_MANAGE")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null) {
            return Result.error("状态值不能为空");
        }
        
        User user = userService.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setStatus(status);
        userService.update(user);
        return Result.success();
    }

    @PutMapping("/batch/status")
    @RequirePermission("USER_MANAGE")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("收到批量修改状态请求: " + request);
            
            @SuppressWarnings("unchecked")
            List<Object> rawIds = (List<Object>) request.get("ids");
            Integer status = (Integer) request.get("status");
            
            System.out.println("解析的IDs: " + rawIds);
            System.out.println("解析的状态: " + status);
            
            if (rawIds == null || rawIds.isEmpty()) {
                return Result.error("请选择要修改的用户");
            }
            if (status == null) {
                return Result.error("状态值不能为空");
            }
            
            int successCount = 0;
            int failCount = 0;
            
            // 转换ID类型，兼容前端传递的number类型
            for (Object rawId : rawIds) {
                try {
                    Long id;
                    if (rawId instanceof Integer) {
                        id = ((Integer) rawId).longValue();
                    } else if (rawId instanceof Long) {
                        id = (Long) rawId;
                    } else if (rawId instanceof String) {
                        id = Long.parseLong((String) rawId);
                    } else {
                        id = Long.valueOf(rawId.toString());
                    }
                    
                    System.out.println("处理用户ID: " + id);
                    
                    User user = userService.findById(id);
                    if (user != null) {
                        System.out.println("找到用户: " + user.getUsername() + ", 当前状态: " + user.getStatus());
                        user.setStatus(status);
                        userService.update(user);
                        successCount++;
                        System.out.println("用户 " + user.getUsername() + " 状态修改成功");
                    } else {
                        System.out.println("用户ID " + id + " 不存在");
                        failCount++;
                    }
                } catch (Exception e) {
                    System.err.println("处理用户ID " + rawId + " 时发生错误: " + e.getMessage());
                    e.printStackTrace();
                    failCount++;
                }
            }
            
            System.out.println("批量修改完成: 成功" + successCount + "个, 失败" + failCount + "个");
            
            if (failCount > 0 && successCount == 0) {
                return Result.error("批量修改失败");
            } else if (failCount > 0) {
                System.out.println("部分修改成功: 成功" + successCount + "个, 失败" + failCount + "个");
            }
            return Result.success();
        } catch (Exception e) {
            System.err.println("批量修改用户状态失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("批量修改用户状态失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    @RequirePermission("PROFILE_MANAGE")
    public Result<Void> updatePassword(@PathVariable Long id,
                                     @RequestParam String oldPassword,
                                     @RequestParam String newPassword) {
        userService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }

    @PutMapping("/current/password")
    @RequirePermission("PROFILE_MANAGE")
    public Result<Void> updateCurrentUserPassword(@RequestBody ChangePasswordParams params,
                                                 HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        
        if (username == null || username.isEmpty()) {
            return Result.error("用户名不能为空");
        }
        
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        userService.updatePassword(currentUser.getId(), params.getOldPassword(), params.getNewPassword());
        return Result.success();
    }

    @PostMapping("/current/avatar")
    @RequirePermission("PROFILE_MANAGE")
    public Result<Void> uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        if (file.isEmpty()) {
            return Result.error("请选择要上传的头像文件");
        }
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只允许上传图片文件");
        }
        // 检查文件大小 (限制为2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("头像文件大小不能超过2MB");
        }
        userService.updateAvatar(username, file.getBytes());
        return Result.success();
    }

    @GetMapping("/current/avatar")
    public ResponseEntity<byte[]> getCurrentUserAvatar(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userService.findByUsername(username);
        if (user == null || user.getAvatar() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.IMAGE_JPEG); // 默认为JPEG格式
        return ResponseEntity.ok()
                .headers(headers)
                .body(user.getAvatar());
    }

    @GetMapping("/{id}/avatar")
    @RequirePermission("USER_MANAGE")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable Long id) {
        User user = userService.findById(id);
        
        if (user == null || user.getAvatar() == null) {
            return ResponseEntity.notFound().build();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 默认为JPEG格式
        return ResponseEntity.ok()
                .headers(headers)
                .body(user.getAvatar());
    }
} 