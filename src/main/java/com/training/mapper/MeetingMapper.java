package com.training.mapper;

import com.training.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MeetingMapper {
    int insertMeeting(Meeting meeting);
    int updateMeeting(Meeting meeting);
    int deleteMeeting(Long id);
    Meeting selectMeetingById(Long id);
    List<Meeting> selectMeetingList(@Param("name") String name, 
                                   @Param("creator") String creator, 
                                   @Param("startDate") String startDate, 
                                   @Param("endDate") String endDate,
                                   @Param("userId") Long userId,
                                   @Param("status") Integer status,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    int countMeetingList(@Param("name") String name, @Param("creator") String creator, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userId") Long userId, @Param("status") Integer status);
} 