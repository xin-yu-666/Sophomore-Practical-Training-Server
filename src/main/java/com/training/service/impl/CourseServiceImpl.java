package com.training.service.impl;

import com.training.entity.Course;
import com.training.mapper.CourseMapper;
import com.training.service.CourseService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
	@Autowired
	private CourseMapper courseMapper;

	@Override
	public List<Course> findByNameAndAuthor(String name, String author, int offset, int size) {
		return courseMapper.selectByNameAndAuthor(name, author, offset, size);
	}

	@Override
	public Course getById(Long id) {
		return courseMapper.getById(id);
	}

	@Override
	public int countByNameAndAuthor(String name, String author) {
		return courseMapper.countByNameAndAuthor(name, author);
	}

	@Override
	public int addCourse(Course course) {
		course.setCreateTime(new Date());
		return courseMapper.insert(course);
	}

	@Override
	public int updateCourse(Course course) {
		return courseMapper.update(course);
	}

	@Override
	public int deleteCourse(Integer id) {
		return courseMapper.deleteById(id);
	}

	@Override
	public List<Course> findByCondition(String name, String author, Long userId, Integer status, int offset, int size) {
		return courseMapper.selectByCondition(name, author, userId, status, offset, size);
	}

	@Override
	public int countByCondition(String name, String author, Long userId, Integer status) {
		return courseMapper.countByCondition(name, author, userId, status);
	}
}
