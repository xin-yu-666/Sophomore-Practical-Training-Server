package com.training.service;

import com.training.entity.Role;
import com.training.entity.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    
    User findById(Long id);
    
    List<User> findAll();
    
    List<User> findByEnterpriseId(Long enterpriseId);
    
    void create(User user);
    
    void update(User user);
    
    void deleteById(Long id);
    
    List<User> findByCondition(String username, String phone, Integer status);
    
    List<User> findByCondition(String username, String phone, Integer status, String enterpriseName);
    
    String login(String username, String password);
    
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    void updateAvatar(String username, byte[] avatar);
    
    User findByUsernameWithEnterprise(String username);
    void assignRole(Long userId, Long roleId);
    
    List<Role> findAllRoles();
    
    List<String> findRolesByUserId(Long userId);
} 