package com.training.controller;

import com.training.annotation.RequirePermission;
import com.training.entity.Course;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.service.CourseService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {
	@Autowired
	private CourseService courseService;

	@Autowired
	private UserMapper userMapper;

	@GetMapping("/list")
	public Map<String, Object> list(
			@RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "") String author,
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {
		int offset = (page - 1) * size;
		List<Course> list = courseService.findByCondition(name, author, userId, status, offset, size);
		int total = courseService.countByCondition(name, author, userId, status);
		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		result.put("total", total);
		return result;
	}

	@GetMapping("/detail/{id}")
	public ResponseEntity<?> getCourseDetail(@PathVariable Long id) {
		Course course = courseService.getById(id);
		if (course != null) {
			// 返回格式和前端预期一致
			Map<String, Object> result = new HashMap<>();
			result.put("code", 0);
			result.put("data", course);
			return ResponseEntity.ok(result);
		} else {
			Map<String, Object> result = new HashMap<>();
			result.put("code", 404);
			result.put("message", "课程不存在");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		}
	}

	@PostMapping("/add")
	public Map<String, Object> add(@RequestBody Course course, HttpServletRequest request) {
		try {
			// 从JWT token中获取用户信息
			String token = request.getHeader("Authorization");
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}
			String username = JwtUtil.getUsernameFromToken(token);
			User user = userMapper.findByUsername(username);

			if (user == null) {
				Map<String, Object> result = new HashMap<>();
				result.put("success", false);
				result.put("message", "用户不存在");
				return result;
			}

			// 设置课程的用户ID
			course.setUserId(user.getId().intValue());

			// 新增课程时，企业用户默认status=0（待审核），管理员可自定义
			if (course.getStatus() == null) {
				course.setStatus(0);
			}

			int result = courseService.addCourse(course);
			Map<String, Object> response = new HashMap<>();
			response.put("success", result > 0);
			response.put("data", course);
			return response;
		} catch (Exception e) {
			Map<String, Object> result = new HashMap<>();
			result.put("success", false);
			result.put("message", "添加课程失败：" + e.getMessage());
			return result;
		}
	}

	@PostMapping("/update")
	public int update(@RequestBody Course course) {
		return courseService.updateCourse(course);
	}

	@DeleteMapping("/delete/{id}")
	public int delete(@PathVariable Integer id) {
		return courseService.deleteCourse(id);
	}

	@RequirePermission("NEWS_AUDIT")
	@PostMapping("/approve/{id}")
	public int approve(@PathVariable Integer id, @RequestParam Integer status) {
		Course course = courseService.getById(Long.valueOf(id));
		if (course != null) {
			course.setStatus(status); // 1=通过，2=拒绝
			return courseService.updateCourse(course);
		}
		return 0;
	}

}