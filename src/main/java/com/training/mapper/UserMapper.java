package com.training.mapper;

import com.training.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT id, username, password, nickname, phone, email, gender, avatar, enterprise_id, status, create_time, update_time " +
            "FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT u.id, u.username, u.password, u.nickname, u.phone, u.email, u.gender, u.avatar, u.enterprise_id, u.status, u.create_time, u.update_time, e.name as enterprise_name " +
            "FROM user u " +
            "LEFT JOIN enterprise e ON u.enterprise_id = e.id " +
            "WHERE u.username = #{username}")
    @Results({
        @Result(property = "enterpriseName", column = "enterprise_name")
    })
    User findByUsernameWithEnterprise(String username);
    
    @Select("SELECT id, username, password, nickname, phone, email, gender, avatar, enterprise_id, status, create_time, update_time " +
            "FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT id, username, password, nickname, phone, email, gender, avatar, enterprise_id, status, create_time, update_time " +
            "FROM user")
    List<User> findAll();
    
    @Select("SELECT id, username, password, nickname, phone, email, gender, avatar, enterprise_id, status, create_time, update_time " +
            "FROM user WHERE enterprise_id = #{enterpriseId}")
    List<User> findByEnterpriseId(Long enterpriseId);
    
    @Insert("INSERT INTO user (username, password, nickname, phone, email, gender, avatar, enterprise_id, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{gender}, #{avatar}, #{enterpriseId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("<script>" +
            "UPDATE user " +
            "<set>" +
            "  <if test='password != null'>password = #{password},</if>" +
            "  <if test='nickname != null'>nickname = #{nickname},</if>" +
            "  <if test='phone != null'>phone = #{phone},</if>" +
            "  <if test='email != null'>email = #{email},</if>" +
            "  <if test='gender != null'>gender = #{gender},</if>" +
            "  <if test='avatar != null'>avatar = #{avatar},</if>" +
            "  <if test='enterpriseId != null'>enterprise_id = #{enterpriseId},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(User user);
    
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("<script>" +
            "SELECT id, username, password, nickname, phone, email, gender, avatar, enterprise_id, status, create_time, update_time " +
            "FROM user " +
            "<where>" +
            "  <if test='username != null and username != \"\"'>" +
            "    AND username LIKE CONCAT('%', #{username}, '%')" +
            "  </if>" +
            "  <if test='phone != null and phone != \"\"'>" +
            "    AND phone LIKE CONCAT('%', #{phone}, '%')" +
            "  </if>" +
            "  <if test='status != null'>" +
            "    AND status = #{status}" +
            "  </if>" +
            "</where>" +
            "</script>")
    List<User> findByCondition(@Param("username") String username, 
                             @Param("phone") String phone,
                             @Param("status") Integer status);

    @Select("<script>" +
            "SELECT u.id, u.username, u.password, u.nickname, u.phone, u.email, u.gender, u.avatar, u.enterprise_id, u.status, u.create_time, u.update_time, e.name as enterprise_name " +
            "FROM user u " +
            "LEFT JOIN enterprise e ON u.enterprise_id = e.id " +
            "<where>" +
            "  <if test='username != null and username != \"\"'>" +
            "    AND u.username LIKE CONCAT('%', #{username}, '%')" +
            "  </if>" +
            "  <if test='phone != null and phone != \"\"'>" +
            "    AND u.phone LIKE CONCAT('%', #{phone}, '%')" +
            "  </if>" +
            "  <if test='status != null'>" +
            "    AND u.status = #{status}" +
            "  </if>" +
            "  <if test='enterpriseName != null and enterpriseName != \"\"'>" +
            "    AND e.name LIKE CONCAT('%', #{enterpriseName}, '%')" +
            "  </if>" +
            "</where>" +
            "</script>")
    @Results({
        @Result(property = "enterpriseName", column = "enterprise_name")
    })
    List<User> findByConditionWithEnterprise(@Param("username") String username, 
                                           @Param("phone") String phone,
                                           @Param("status") Integer status,
                                           @Param("enterpriseName") String enterpriseName);
} 