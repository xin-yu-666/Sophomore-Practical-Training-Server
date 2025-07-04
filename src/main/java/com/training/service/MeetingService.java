package com.training.service;

import com.training.entity.Meeting;

import java.util.List;

public interface MeetingService {
    int createMeeting(Meeting meeting);
    int updateMeeting(Meeting meeting);
    int deleteMeeting(Long id);
    Meeting getMeetingById(Long id);
    List<Meeting> getMeetingList(String name, String creator, String startDate, String endDate, Long userId, Integer status, int offset, int limit);
    int countMeetingList(String name, String creator, String startDate, String endDate, Long userId, Integer status);
} 