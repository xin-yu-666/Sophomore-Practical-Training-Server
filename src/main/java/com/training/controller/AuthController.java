package com.training.controller;

import com.training.common.Result;
import com.training.dto.LoginDTO;
import com.training.dto.EnterpriseRegisterDTO;
import com.training.dto.SimpleEnterpriseRegisterDTO;
import com.training.dto.UserRegisterDTO;
import com.training.dto.ResetPasswordDTO;
import com.training.entity.Enterprise;
import com.training.entity.User;
import com.training.service.EnterpriseService;
import com.training.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private EnterpriseService enterpriseService;

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            logger.info("用户登录请求: {}", loginDTO.getUsername());
            String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            logger.info("用户 {} 登录成功", loginDTO.getUsername());
            return Result.success(token);
        } catch (Exception e) {
            logger.error("用户 {} 登录失败: {}", loginDTO.getUsername(), e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody EnterpriseRegisterDTO registerDTO) {
        try {
            logger.info("企业注册请求: {}", registerDTO.getName());
            
            // 检查用户名是否已存在
            if (userService.findByUsername(registerDTO.getUsername()) != null) {
                return Result.error("用户名已存在");
            }

            // 检查企业名称是否已存在
            if (enterpriseService.findByName(registerDTO.getName()) != null) {
                return Result.error("企业名称已存在");
            }

            // 创建企业
            Enterprise enterprise = new Enterprise();
            enterprise.setName(registerDTO.getName());
            enterprise.setContact(registerDTO.getContact());
            enterprise.setPhone(registerDTO.getPhone());
            enterprise.setEmail(registerDTO.getEmail());
            enterpriseService.create(enterprise);

            // 创建用户
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(registerDTO.getPassword());
            user.setEnterpriseId(enterprise.getId());
            userService.create(user);

            logger.info("企业 {} 注册成功", registerDTO.getName());
            return Result.success();
        } catch (Exception e) {
            logger.error("企业注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/enterprise/register")
    public Result<Void> enterpriseRegister(@Valid @RequestBody SimpleEnterpriseRegisterDTO registerDTO) {
        try {
            logger.info("企业注册请求: {}", registerDTO.getName());
            
            // 检查企业名称是否已存在
            if (enterpriseService.findByName(registerDTO.getName()) != null) {
                return Result.error("企业名称已存在");
            }

            // 创建企业
            Enterprise enterprise = new Enterprise();
            enterprise.setName(registerDTO.getName());
            enterprise.setContact(registerDTO.getContact());
            enterprise.setPhone(registerDTO.getPhone());
            enterprise.setEmail(registerDTO.getEmail());
            enterprise.setStatus(1); // 默认启用状态
            enterpriseService.create(enterprise);

            logger.info("企业 {} 注册成功", registerDTO.getName());
            return Result.success();
        } catch (Exception e) {
            logger.error("企业注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/user/register")
    public Result<Void> userRegister(@Valid @RequestBody UserRegisterDTO registerDTO) {
        try {
            logger.info("用户注册请求: {}", registerDTO.getUsername());
            
            // 检查用户名是否已存在
            if (userService.findByUsername(registerDTO.getUsername()) != null) {
                return Result.error("用户名已存在");
            }

            // 创建用户
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setPassword(registerDTO.getPassword());
            user.setNickname(registerDTO.getNickname());
            user.setPhone(registerDTO.getPhone());
            user.setEmail(registerDTO.getEmail());
            user.setGender(registerDTO.getGender());
            user.setEnterpriseId(registerDTO.getEnterpriseId());
            user.setStatus(registerDTO.getStatus() != null ? registerDTO.getStatus() : 1);
            
            // 处理头像Base64数据
            if (registerDTO.getAvatar() != null && !registerDTO.getAvatar().isEmpty()) {
                try {
                    byte[] avatarBytes = Base64.getDecoder().decode(registerDTO.getAvatar());
                    user.setAvatar(avatarBytes);
                } catch (Exception e) {
                    logger.warn("头像Base64解码失败: {}", e.getMessage());
                }
            }
            
            userService.create(user);

            logger.info("用户 {} 注册成功", registerDTO.getUsername());
            return Result.success();
        } catch (Exception e) {
            logger.error("用户注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            logger.info("重置密码请求: {}", resetPasswordDTO.getUsername());
            logger.info("新密码长度: {}", resetPasswordDTO.getNewPassword() != null ? resetPasswordDTO.getNewPassword().length() : 0);
            
            // 检查必要参数
            if (resetPasswordDTO.getUsername() == null || resetPasswordDTO.getUsername().trim().isEmpty()) {
                logger.warn("重置密码失败: 用户名为空");
                return Result.error("用户名不能为空");
            }
            
            if (resetPasswordDTO.getNewPassword() == null || resetPasswordDTO.getNewPassword().trim().isEmpty()) {
                logger.warn("重置密码失败: 新密码为空");
                return Result.error("新密码不能为空");
            }
            
            userService.resetPassword(resetPasswordDTO.getUsername(), resetPasswordDTO.getNewPassword());
            
            logger.info("用户 {} 密码重置成功", resetPasswordDTO.getUsername());
            return Result.success();
        } catch (RuntimeException e) {
            // 业务异常，返回具体错误信息
            logger.error("用户 {} 密码重置失败: {}", resetPasswordDTO.getUsername(), e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // 系统异常，返回通用错误信息
            logger.error("用户 {} 密码重置系统错误: ", resetPasswordDTO.getUsername(), e);
            return Result.error("系统异常，请稍后重试");
        }
    }

    @GetMapping("/test-user")
    public Result<User> testUser(@RequestParam String username) {
        try {
            logger.info("测试用户查询: {}", username);
            User user = userService.findByUsername(username);
            if (user != null) {
                logger.info("找到用户: ID={}, Username={}, Status={}", user.getId(), user.getUsername(), user.getStatus());
                // 隐藏密码信息
                user.setPassword("***");
                return Result.success(user);
            } else {
                logger.warn("用户不存在: {}", username);
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            logger.error("测试用户查询失败: ", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
} 