package com.training.controller;

import com.training.entity.Meeting;
import com.training.service.MeetingService;
import com.training.annotation.RequirePermission;
import com.training.mapper.UserMapper;
import com.training.entity.User;
import com.training.dto.MeetingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meeting")
@CrossOrigin
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test")
    public Map<String, Object> test() {
        try {
            List<Meeting> list = meetingService.getMeetingList(null, null, null, null, null, null, 0, 10);
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("message", "数据库连接正常");
            map.put("count", list.size());
            map.put("data", list);
            return map;
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "数据库连接失败: " + e.getMessage());
            map.put("error", e.toString());
            return map;
        }
    }

    @GetMapping("")
    @RequirePermission("MEETING_VIEW")
    public Map<String, Object> list(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String creator,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   @RequestParam(required = false) Long userId,
                                   @RequestParam(defaultValue = "1", name = "pageNum") Integer pageNum,
                                   @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize,
                                   HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String username = com.training.util.JwtUtil.getUsernameFromToken(token);
            User user = userMapper.findByUsername(username);
            List<String> roles = userMapper.findRolesByUserId(user.getId());
            boolean isAdmin = false;
            for (String role : roles) {
                if ("ROLE_ADMIN".equals(role)) {
                    isAdmin = true;
                    break;
                }
            }
            int offset = (pageNum - 1) * pageSize;
            List<Meeting> list;
            int total;
            if (isAdmin) {
                list = meetingService.getMeetingList(name, creator, startDate, endDate, userId, null, offset, pageSize);
                total = meetingService.countMeetingList(name, creator, startDate, endDate, userId, null);
            } else {
                if (userId != null && userId.equals(user.getId())) {
                    list = meetingService.getMeetingList(name, creator, startDate, endDate, user.getId(), null, offset, pageSize);
                    total = meetingService.countMeetingList(name, creator, startDate, endDate, user.getId(), null);
                } else {
                    list = meetingService.getMeetingList(name, creator, startDate, endDate, null, 1, offset, pageSize);
                    total = meetingService.countMeetingList(name, creator, startDate, endDate, null, 1);
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("list", list.stream().map(this::toDTO).collect(Collectors.toList()));
            data.put("total", total);
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("data", data);
            return map;
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }

    @GetMapping("/{id}")
    @RequirePermission("MEETING_VIEW")
    public Map<String, Object> detail(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            map.put("success", false);
            map.put("message", "会议不存在");
            return map;
        }
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin && !Objects.equals(meeting.getUserId(), user.getId()) && !Objects.equals(meeting.getStatus(), 1)) {
            map.put("success", false);
            map.put("message", "无权查看该会议");
            return map;
        }
        map.put("success", true);
        map.put("data", toDTO(meeting));
        return map;
    }

    @PostMapping("")
    @RequirePermission("MEETING_PUBLISH")
    public Map<String, Object> add(@RequestBody MeetingDTO meetingDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        Meeting meeting = dtoToEntity(meetingDTO);
        meeting.setUserId(user.getId());
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        meeting.setStatus(isAdmin ? 1 : 0);
        boolean ok = meetingService.createMeeting(meeting) > 0;
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        map.put("data", toDTO(meeting));
        return map;
    }

    @PutMapping("/{id}")
    @RequirePermission("MEETING_EDIT")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody MeetingDTO meetingDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        Meeting old = meetingService.getMeetingById(id);
        if (old == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "会议不存在");
            return map;
        }
        if (!isAdmin && !Objects.equals(old.getUserId(), user.getId())) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "无权编辑该会议");
            return map;
        }
        Meeting meeting = dtoToEntity(meetingDTO);
        meeting.setId(id);
        meeting.setUserId(old.getUserId());
        meeting.setStatus(old.getStatus());
        boolean ok = meetingService.updateMeeting(meeting) > 0;
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        map.put("data", toDTO(meeting));
        return map;
    }

    @DeleteMapping("/{id}")
    @RequirePermission("MEETING_DELETE")
    public Map<String, Object> delete(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByUsername(username);
        List<String> roles = userMapper.findRolesByUserId(user.getId());
        boolean isAdmin = false;
        for (String role : roles) {
            if ("ROLE_ADMIN".equals(role)) {
                isAdmin = true;
                break;
            }
        }
        Meeting old = meetingService.getMeetingById(id);
        if (old == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "会议不存在");
            return map;
        }
        if (!isAdmin && !Objects.equals(old.getUserId(), user.getId())) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "无权删除该会议");
            return map;
        }
        boolean ok = meetingService.deleteMeeting(id) > 0;
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        return map;
    }

    @PutMapping("/audit/{id}")
    @RequirePermission("MEETING_AUDIT")
    public Map<String, Object> audit(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
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
            map.put("message", "无权审核会议");
            return map;
        }
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "会议不存在");
            return map;
        }
        if (status != 1 && status != 2) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "状态值非法");
            return map;
        }
        meeting.setStatus(status);
        boolean ok = meetingService.updateMeeting(meeting) > 0;
        Map<String, Object> map = new HashMap<>();
        map.put("success", ok);
        return map;
    }

    // 管理员专用：查所有待审核会议
    @GetMapping("/pending")
    @RequirePermission("MEETING_AUDIT")
    public Map<String, Object> pendingList(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = com.training.util.JwtUtil.getUsernameFromToken(token);
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
        List<Meeting> list = meetingService.getMeetingList(null, null, null, null, null, 0, 0, 10);
        map.put("success", true);
        map.put("data", list.stream().map(this::toDTO).collect(Collectors.toList()));
        return map;
    }

    private MeetingDTO toDTO(Meeting meeting) {
        if (meeting == null) return null;
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getId());
        dto.setName(meeting.getName());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        dto.setCreator(meeting.getCreator());
        dto.setContent(meeting.getContent());
        dto.setCreateTime(meeting.getCreateTime());
        dto.setUserId(meeting.getUserId());
        dto.setStatus(meeting.getStatus());
        return dto;
    }

    private Meeting dtoToEntity(MeetingDTO dto) {
        if (dto == null) return null;
        Meeting meeting = new Meeting();
        meeting.setId(dto.getId());
        meeting.setName(dto.getName());
        meeting.setStartTime(dto.getStartTime());
        meeting.setEndTime(dto.getEndTime());
        meeting.setCreator(dto.getCreator());
        meeting.setContent(dto.getContent());
        meeting.setCreateTime(dto.getCreateTime());
        meeting.setUserId(dto.getUserId());
        meeting.setStatus(dto.getStatus());
        return meeting;
    }
} 