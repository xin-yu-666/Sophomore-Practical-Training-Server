package com.training.mapper;

import com.training.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
	List<Course> selectByNameAndAuthor(@Param("name") String name, @Param("author") String author,
			@Param("offset") int offset, @Param("size") int size);

	Course getById(Long id);

	int countByNameAndAuthor(@Param("name") String name, @Param("author") String author);

	int insert(Course course);

	int update(Course course);

	int deleteById(Integer id);

	List<Course> selectByCondition(@Param("name") String name, @Param("author") String author,
			@Param("userId") Long userId, @Param("status") Integer status,
			@Param("offset") int offset, @Param("size") int size);

	int countByCondition(@Param("name") String name, @Param("author") String author,
			@Param("userId") Long userId, @Param("status") Integer status);
}
