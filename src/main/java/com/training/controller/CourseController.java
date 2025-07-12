package com.training.controller;

import com.training.annotation.RequirePermission;
import com.training.entity.Course;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.service.CourseService;
import com.training.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.io.IOException;

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
			@RequestParam(required = false, defaultValue = "10") int size,
			HttpServletRequest request) {
		try {
			int offset = (page - 1) * size;
			List<Course> list;
			int total;
			
			String token = request.getHeader("Authorization");
			boolean isAdmin = false;
			Long currentUserId = null;
			
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
				String username = JwtUtil.getUsernameFromToken(token);
				User user = userMapper.findByUsername(username);
				if (user != null) {
					currentUserId = user.getId();
					List<String> roles = userMapper.findRolesByUserId(user.getId());
					for (String role : roles) {
						if ("ROLE_ADMIN".equals(role)) {
							isAdmin = true;
							break;
						}
					}
				}
			}
			
			if (isAdmin) {
				// 管理员可查看所有课程
				list = courseService.findByCondition(name, author, userId, status, offset, size);
				total = courseService.countByCondition(name, author, userId, status);
			} else if (userId != null && currentUserId != null && userId.equals(currentUserId)) {
				// 普通用户查看自己的课程
				list = courseService.findByCondition(name, author, currentUserId, status, offset, size);
				total = courseService.countByCondition(name, author, currentUserId, status);
			} else {
				// 普通用户或匿名用户只能查看已通过审核的课程
				list = courseService.findByCondition(name, author, null, 1, offset, size);
				total = courseService.countByCondition(name, author, null, 1);
			}
			
			Map<String, Object> result = new HashMap<>();
			result.put("list", list);
			result.put("total", total);
			return result;
		} catch (Exception e) {
			Map<String, Object> result = new HashMap<>();
			result.put("success", false);
			result.put("message", e.getMessage());
			return result;
		}
	}

	@GetMapping("/detail/{id}")
	public Map<String, Object> getCourseDetail(@PathVariable Long id, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Course course = courseService.getById(id);
		if (course == null) {
			map.put("success", false);
			map.put("message", "课程不存在");
			return map;
		}
		
		String token = request.getHeader("Authorization");
		boolean isAdmin = false;
		Long currentUserId = null;
		
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			String username = JwtUtil.getUsernameFromToken(token);
			User user = userMapper.findByUsername(username);
			if (user != null) {
				currentUserId = user.getId();
				List<String> roles = userMapper.findRolesByUserId(user.getId());
				for (String role : roles) {
					if ("ROLE_ADMIN".equals(role)) {
						isAdmin = true;
						break;
					}
				}
			}
		}
		
		// 权限检查：管理员可查看所有课程，普通用户只能查看已通过审核的课程或自己发布的课程
		if (!isAdmin && 
			!Objects.equals(course.getStatus(), 1) && 
			(currentUserId == null || !Objects.equals(course.getUserId().longValue(), currentUserId))) {
			map.put("success", false);
			map.put("message", "无权查看该课程");
			return map;
		}
		
		map.put("success", true);
		map.put("data", course);
		return map;
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

			// 根据用户权限设置课程状态
			List<String> roles = userMapper.findRolesByUserId(user.getId());
			boolean isAdmin = false;
			for (String role : roles) {
				if ("ROLE_ADMIN".equals(role)) {
					isAdmin = true;
					break;
				}
			}
			course.setStatus(isAdmin ? 1 : 0); // 管理员发布的课程直接通过，普通用户需要审核

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

	@PutMapping("/update/{id}")
	public Map<String, Object> update(@PathVariable Long id, @RequestBody Course course, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		String username = JwtUtil.getUsernameFromToken(token);
		User user = userMapper.findByUsername(username);
		List<String> roles = userMapper.findRolesByUserId(user.getId());
		boolean isAdmin = false;
		for (String role : roles) {
			if ("ROLE_ADMIN".equals(role)) {
				isAdmin = true;
				break;
			}
		}
		Course old = courseService.getById(id);
		if (old == null) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "课程不存在");
			return map;
		}
		if (!isAdmin && !Objects.equals(old.getUserId().longValue(), user.getId())) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "无权编辑该课程");
			return map;
		}
		course.setId(id.intValue());
		course.setUserId(old.getUserId());
		
		// 根据用户权限设置课程状态：
		// 管理员编辑后状态不变，普通用户编辑后需要重新审核
		if (isAdmin) {
			course.setStatus(old.getStatus()); // 管理员编辑，状态保持不变
		} else {
			course.setStatus(0); // 普通用户编辑，重新进入待审核状态
		}
		
		int result = courseService.updateCourse(course);
		Map<String, Object> map = new HashMap<>();
		map.put("success", result > 0);
		map.put("data", course);
		return map;
	}

	@DeleteMapping("/delete/{id}")
	public Map<String, Object> delete(@PathVariable Long id, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		String username = JwtUtil.getUsernameFromToken(token);
		User user = userMapper.findByUsername(username);
		List<String> roles = userMapper.findRolesByUserId(user.getId());
		boolean isAdmin = false;
		for (String role : roles) {
			if ("ROLE_ADMIN".equals(role)) {
				isAdmin = true;
				break;
			}
		}
		Course old = courseService.getById(id);
		if (old == null) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "课程不存在");
			return map;
		}
		if (!isAdmin && !Objects.equals(old.getUserId().longValue(), user.getId())) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "无权删除该课程");
			return map;
		}
		int result = courseService.deleteCourse(id.intValue());
		Map<String, Object> map = new HashMap<>();
		map.put("success", result > 0);
		return map;
	}

	@PutMapping("/audit/{id}")
	public Map<String, Object> audit(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		String username = JwtUtil.getUsernameFromToken(token);
		User user = userMapper.findByUsername(username);
		List<String> roles = userMapper.findRolesByUserId(user.getId());
		boolean isAdmin = false;
		for (String role : roles) {
			if ("ROLE_ADMIN".equals(role)) {
				isAdmin = true;
				break;
			}
		}
		if (!isAdmin) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "无权审核课程");
			return map;
		}
		Course course = courseService.getById(id);
		if (course == null) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "课程不存在");
			return map;
		}
		if (status != 1 && status != 2) {
			Map<String, Object> map = new HashMap<>();
			map.put("success", false);
			map.put("message", "状态值非法");
			return map;
		}
		course.setStatus(status); // 1=通过，2=拒绝
		int result = courseService.updateCourse(course);
		Map<String, Object> map = new HashMap<>();
		map.put("success", result > 0);
		return map;
	}

	// 管理员专用：查看所有待审核课程
	@GetMapping("/pending")
	public Map<String, Object> pendingList(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		String username = JwtUtil.getUsernameFromToken(token);
		User user = userMapper.findByUsername(username);
		List<String> roles = userMapper.findRolesByUserId(user.getId());
		boolean isAdmin = false;
		for (String role : roles) {
			if ("ROLE_ADMIN".equals(role)) {
				isAdmin = true;
				break;
			}
		}
		Map<String, Object> map = new HashMap<>();
		if (!isAdmin) {
			map.put("success", false);
			map.put("message", "无权访问");
			return map;
		}
		List<Course> list = courseService.findByCondition(null, null, null, 0, 0, 100);
		map.put("success", true);
		map.put("data", list);
		return map;
	}

	@PostMapping("/ai-generate")
	public ResponseEntity<?> aiGenerate(@RequestBody Map<String, String> body) {
		String query = body.get("query");
		if (query == null || query.trim().isEmpty()) {
			Map<String, String> result = new HashMap<>();
			result.put("generated", "请求内容不能为空");
			return ResponseEntity.badRequest().body(result);
		}
		Map<String, String> result = new HashMap<>();
		result.put("generated", "AI功能已下线");
		return ResponseEntity.ok(result);
	}

}