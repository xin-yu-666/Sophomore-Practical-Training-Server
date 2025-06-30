package com.training.service;

import com.training.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseService {
	List<Course> findByNameAndAuthor(String name, String author, int offset, int size);

	Course getById(Long id);

	int countByNameAndAuthor(String name, String author);

	int addCourse(Course course);

	int updateCourse(Course course);

	int deleteCourse(Integer id);

	List<Course> findByCondition(String name, String author, Long userId, Integer status, int offset, int size);

	int countByCondition(String name, String author, Long userId, Integer status);
}
