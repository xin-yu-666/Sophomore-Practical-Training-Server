package com.training.mapper;

import com.training.entity.Industry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IndustryMapper {
    @Select("SELECT * FROM industry WHERE title LIKE CONCAT('%', #{query}, '%') ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Industry> selectList(@Param("query") String query, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM industry WHERE title LIKE CONCAT('%', #{query}, '%')")
    int selectCount(@Param("query") String query);

    @Insert("INSERT INTO industry (title, category, content, author, status, publish_time, create_time, update_time) " +
            "VALUES (#{title}, #{category}, #{content}, #{author}, #{status}, #{publishTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Industry industry);

    @Update("UPDATE industry SET title = #{title}, category = #{category}, content = #{content}, " +
            "status = #{status}, publish_time = #{publishTime}, update_time = NOW() WHERE id = #{id}")
    int update(Industry industry);

    @Delete("DELETE FROM industry WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT * FROM industry WHERE id = #{id}")
    Industry selectById(@Param("id") Long id);
} 