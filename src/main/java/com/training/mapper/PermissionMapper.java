package com.training.mapper;

import com.training.entity.Permission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PermissionMapper {
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<Permission> findByRoleId(Long roleId);
    
    @Select("SELECT DISTINCT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Permission> findByUserId(Long userId);
    
    @Select("SELECT * FROM permission")
    List<Permission> findAll();
    
    @Select("SELECT * FROM permission WHERE id = #{id}")
    Permission findById(Long id);
    
    @Select("SELECT * FROM permission WHERE code = #{code}")
    Permission findByCode(String code);
} 