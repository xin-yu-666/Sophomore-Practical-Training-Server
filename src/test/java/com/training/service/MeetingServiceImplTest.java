package com.training.service;

import com.training.entity.Meeting;
import com.training.mapper.MeetingMapper;
import com.training.service.impl.MeetingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingServiceImplTest {
    private MeetingServiceImpl meetingService;
    private MeetingMapper meetingMapper;

    @BeforeEach
    void setUp() throws Exception {
        meetingMapper = mock(MeetingMapper.class);
        meetingService = new MeetingServiceImpl();
        java.lang.reflect.Field f1 = MeetingServiceImpl.class.getDeclaredField("meetingMapper");
        f1.setAccessible(true);
        f1.set(meetingService, meetingMapper);
    }

    /**
     * 测试创建会议，验证插入流程。
     */
    @Test
    void testCreateMeeting() {
        Meeting meeting = new Meeting();
        when(meetingMapper.insertMeeting(meeting)).thenReturn(1);
        int result = meetingService.createMeeting(meeting);
        assertEquals(1, result);
    }

    /**
     * 测试更新会议，验证更新流程。
     */
    @Test
    void testUpdateMeeting() {
        Meeting meeting = new Meeting();
        when(meetingMapper.updateMeeting(meeting)).thenReturn(1);
        int result = meetingService.updateMeeting(meeting);
        assertEquals(1, result);
    }

    /**
     * 测试根据ID删除会议，验证删除流程。
     */
    @Test
    void testDeleteMeeting() {
        when(meetingMapper.deleteMeeting(1L)).thenReturn(1);
        int result = meetingService.deleteMeeting(1L);
        assertEquals(1, result);
    }

    /**
     * 测试根据ID查找会议，验证正常查询流程。
     */
    @Test
    void testGetMeetingById() {
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        when(meetingMapper.selectMeetingById(1L)).thenReturn(meeting);
        assertEquals(1L, meetingService.getMeetingById(1L).getId());
    }

    /**
     * 测试按条件查找会议列表，验证返回非空列表。
     */
    @Test
    void testGetMeetingList() {
        when(meetingMapper.selectMeetingList("a", "b", "2023-01-01", "2023-12-31", 1L, 1, 0, 10))
                .thenReturn(Collections.emptyList());
        List<Meeting> list = meetingService.getMeetingList("a", "b", "2023-01-01", "2023-12-31", 1L, 1, 0, 10);
        assertNotNull(list);
    }

    /**
     * 测试按条件统计会议数量。
     */
    @Test
    void testCountMeetingList() {
        when(meetingMapper.countMeetingList("a", "b", "2023-01-01", "2023-12-31", 1L, 1)).thenReturn(5);
        int result = meetingService.countMeetingList("a", "b", "2023-01-01", "2023-12-31", 1L, 1);
        assertEquals(5, result);
    }

    /**
     * 异常测试：创建会议时传null应抛出异常。
     */
    @Test
    void testCreateMeetingWithNull() {
        assertThrows(NullPointerException.class, () -> meetingService.createMeeting(null));
    }

    /**
     * 异常测试：更新会议时传null应抛出异常。
     */
    @Test
    void testUpdateMeetingWithNull() {
        assertThrows(NullPointerException.class, () -> meetingService.updateMeeting(null));
    }

    /**
     * 边界测试：删除负ID会议，验证返回0。
     */
    @Test
    void testDeleteMeetingWithNegativeId() {
        when(meetingMapper.deleteMeeting(-1L)).thenReturn(0);
        int result = meetingService.deleteMeeting(-1L);
        assertEquals(0, result);
    }

    /**
     * 边界测试：按空条件查找会议列表，验证返回空列表。
     */
    @Test
    void testGetMeetingListWithEmpty() {
        when(meetingMapper.selectMeetingList("", "", null, null, null, null, 0, 10))
                .thenReturn(Collections.emptyList());
        List<Meeting> list = meetingService.getMeetingList("", "", null, null, null, null, 0, 10);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
}