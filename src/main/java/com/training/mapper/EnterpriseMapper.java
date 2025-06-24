package com.training.mapper;

import com.training.entity.Enterprise;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EnterpriseMapper {
    @Select("SELECT id, name, contact, phone, email, status, create_time, update_time " +
            "FROM enterprise WHERE id = #{id}")
    Enterprise findById(Long id);
    
    @Select("SELECT id, name, contact, phone, email, status, create_time, update_time " +
            "FROM enterprise WHERE name = #{name}")
    Enterprise findByName(String name);
    
    @Select("SELECT id, name, contact, phone, email, status, create_time, update_time " +
            "FROM enterprise")
    List<Enterprise> findAll();
    
    @Insert("INSERT INTO enterprise (name, contact, phone, email, status) " +
            "VALUES (#{name}, #{contact}, #{phone}, #{email}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Enterprise enterprise);
    
    @Update("<script>" +
            "UPDATE enterprise " +
            "<set>" +
            "  <if test='name != null'>name = #{name},</if>" +
            "  <if test='contact != null'>contact = #{contact},</if>" +
            "  <if test='phone != null'>phone = #{phone},</if>" +
            "  <if test='email != null'>email = #{email},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(Enterprise enterprise);
    
    @Delete("DELETE FROM enterprise WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("<script>" +
            "SELECT id, name, contact, phone, email, status, create_time, update_time " +
            "FROM enterprise " +
            "<where>" +
            "  <if test='name != null and name != \"\"'>" +
            "    AND name LIKE CONCAT('%', #{name}, '%')" +
            "  </if>" +
            "  <if test='contact != null and contact != \"\"'>" +
            "    AND contact LIKE CONCAT('%', #{contact}, '%')" +
            "  </if>" +
            "  <if test='status != null'>" +
            "    AND status = #{status}" +
            "  </if>" +
            "</where>" +
            "</script>")
    List<Enterprise> findByCondition(@Param("name") String name,
                                   @Param("contact") String contact,
                                   @Param("status") Integer status);
} 