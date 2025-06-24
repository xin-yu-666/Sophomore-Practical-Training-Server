package com.training.service.impl;

import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.mapper.UserRoleMapper;
import com.training.service.UserService;
import com.training.util.JwtUtil;
import com.training.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

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
    public void updateAvatar(String username, byte[] avatar) {
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