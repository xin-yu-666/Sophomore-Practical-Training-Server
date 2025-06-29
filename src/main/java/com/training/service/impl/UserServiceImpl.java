package com.training.service.impl;

import com.training.entity.User;
import com.training.entity.UserRole;
import com.training.mapper.UserMapper;
import com.training.mapper.UserRoleMapper;
import com.training.service.UserService;
import com.training.util.JwtUtil;
import com.training.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public List<User> findByEnterpriseId(Long enterpriseId) {
        return userMapper.findByEnterpriseId(enterpriseId);
    }

    @Override
    @Transactional
    public void create(User user) {
        // 加密密码
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        userMapper.insert(user);
        
        // 为新用户分配企业用户角色（role_id = 2）
        Long roleId = "admin".equals(user.getUsername()) ? 1L : 2L; // admin为超级管理员，其他为企业用户
        jdbcTemplate.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", 
                           user.getId(), roleId);
    }

    @Override
    @Transactional
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<User> findByCondition(String username, String phone, Integer status) {
        return userMapper.findByCondition(username, phone, status);
    }

    @Override
    public List<User> findByCondition(String username, String phone, Integer status, String enterpriseName) {
        return userMapper.findByConditionWithEnterprise(username, phone, status, enterpriseName);
    }

    @Override
    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!PasswordUtil.match(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        return JwtUtil.generateToken(username);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!PasswordUtil.match(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(PasswordUtil.encrypt(newPassword));
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void resetPassword(String username, String newPassword) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new RuntimeException("密码长度必须在6-20个字符之间");
        }
        
        // 日志记录
        System.out.println("开始重置密码 - 用户名: " + username.trim());
        System.out.println("新密码长度: " + newPassword.length());
        
        User user = userMapper.findByUsername(username.trim());
        if (user == null) {
            System.out.println("用户查找失败 - 用户名: " + username.trim());
            throw new RuntimeException("用户名不存在，请检查用户名是否正确");
        }
        
        System.out.println("找到用户 - ID: " + user.getId() + ", 用户名: " + user.getUsername());
        System.out.println("当前密码: " + user.getPassword());

        if (user.getStatus() == 0) {
            System.out.println("用户状态异常 - 状态: " + user.getStatus());
            throw new RuntimeException("账号已被禁用，无法重置密码");
        }

        // 加密新密码
        String encryptedPassword = PasswordUtil.encrypt(newPassword);
        System.out.println("加密后的新密码: " + encryptedPassword);
        
        String oldPassword = user.getPassword();
        user.setPassword(encryptedPassword);
        
        // 更新数据库
        System.out.println("开始更新数据库...");
        int updateResult = userMapper.update(user);
        System.out.println("数据库更新结果: " + updateResult);
        
        if (updateResult <= 0) {
            System.out.println("数据库更新失败!");
            throw new RuntimeException("密码更新失败，请稍后重试");
        }
        
        // 验证更新结果
        User updatedUser = userMapper.findByUsername(username.trim());
        System.out.println("更新后的密码: " + updatedUser.getPassword());
        
        if (!encryptedPassword.equals(updatedUser.getPassword())) {
            System.out.println("密码更新验证失败!");
            System.out.println("期望密码: " + encryptedPassword);
            System.out.println("实际密码: " + updatedUser.getPassword());
            throw new RuntimeException("密码更新验证失败");
        }
        
        System.out.println("用户 " + username + " 密码重置成功");
        System.out.println("原密码: " + oldPassword + " -> 新密码: " + encryptedPassword);
    }

    @Override
    @Transactional
    public void updateAvatar(String username, byte[] avatar) {
//        System.out.println("updateAvatar username = " + username);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setAvatar(avatar);
        userMapper.update(user);
    }

    @Override
    public User findByUsernameWithEnterprise(String username) {
        return userMapper.findByUsernameWithEnterprise(username);
    }
    @Override
    public void assignRole(Long userId, Long roleId) {
        userRoleMapper.insertUserRole(userId, roleId);
    }

    @Override
    public List<com.training.entity.Role> findAllRoles() {
        return userMapper.findAllRoles();
    }

    @Override
    public List<String> findRolesByUserId(Long userId) {
        return userMapper.findRolesByUserId(userId);
    }
} 