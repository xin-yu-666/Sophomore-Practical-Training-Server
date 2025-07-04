package com.training.service.impl;

import com.training.entity.Meeting;
import com.training.mapper.MeetingMapper;
import com.training.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private MeetingMapper meetingMapper;

    @Override
    public int createMeeting(Meeting meeting) {
        if (meeting == null) {
            throw new NullPointerException("参数不能为空");
        }
        meeting.setCreateTime(LocalDateTime.now());
        return meetingMapper.insertMeeting(meeting);
    }

    @Override
    public int updateMeeting(Meeting meeting) {
        if (meeting == null) {
            throw new NullPointerException("参数不能为空");
        }
        return meetingMapper.updateMeeting(meeting);
    }

    @Override
    public int deleteMeeting(Long id) {
        return meetingMapper.deleteMeeting(id);
    }

    @Override
    public Meeting getMeetingById(Long id) {
        return meetingMapper.selectMeetingById(id);
    }

    @Override
    public List<Meeting> getMeetingList(String name, String creator, String startDate, String endDate, Long userId,
            Integer status, int offset, int limit) {
        return meetingMapper.selectMeetingList(name, creator, startDate, endDate, userId, status, offset, limit);
    }

    @Override
    public int countMeetingList(String name, String creator, String startDate, String endDate, Long userId,
            Integer status) {
        return meetingMapper.countMeetingList(name, creator, startDate, endDate, userId, status);
    }
}