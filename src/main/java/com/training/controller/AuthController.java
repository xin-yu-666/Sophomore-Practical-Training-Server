package com.training.controller;

import com.training.common.Result;
import com.training.dto.LoginDTO;
import com.training.dto.EnterpriseRegisterDTO;
import com.training.dto.UserRegisterDTO;
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
import java.util.List;

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

            // 注册后自动分配企业用户角色（ROLE_ENTERPRISE）
            User createdUser = userService.findByUsername(registerDTO.getUsername());
            if (createdUser != null) {
                // 查找企业用户角色id
                Long roleId = null;
                List<com.training.entity.Role> roles = userService.findAllRoles();
                for (com.training.entity.Role r : roles) {
                    if ("ROLE_ENTERPRISE".equals(r.getCode())) {
                        roleId = r.getId();
                        break;
                    }
                }
                if (roleId != null) {
                    userService.assignRole(createdUser.getId(), roleId);
                }
            }

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

            // 注册后自动分配企业用户角色（ROLE_ENTERPRISE）
            User createdUser = userService.findByUsername(registerDTO.getUsername());
            if (createdUser != null) {
                // 查找企业用户角色id
                Long roleId = null;
                List<com.training.entity.Role> roles = userService.findAllRoles();
                for (com.training.entity.Role r : roles) {
                    if ("ROLE_ENTERPRISE".equals(r.getCode())) {
                        roleId = r.getId();
                        break;
                    }
                }
                if (roleId != null) {
                    userService.assignRole(createdUser.getId(), roleId);
                }
            }

            logger.info("用户 {} 注册成功", registerDTO.getUsername());
            return Result.success();
        } catch (Exception e) {
            logger.error("用户注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
} 