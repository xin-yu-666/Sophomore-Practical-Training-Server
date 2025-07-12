package com.training.service;

import com.training.entity.Course;
import com.training.mapper.CourseMapper;
import com.training.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceImplTest {
    private CourseServiceImpl courseService;
    private CourseMapper courseMapper;

    @BeforeEach
    void setUp() throws Exception {
        courseMapper = mock(CourseMapper.class);
        courseService = new CourseServiceImpl();
        java.lang.reflect.Field f1 = CourseServiceImpl.class.getDeclaredField("courseMapper");
        f1.setAccessible(true);
        f1.set(courseService, courseMapper);
    }

    /**
     * 测试按名称和作者查找课程，验证正常查询流程。
     */
    @Test
    void testFindByNameAndAuthor() {
        when(courseMapper.selectByNameAndAuthor("a", "b", 0, 10)).thenReturn(Collections.emptyList());
        List<Course> list = courseService.findByNameAndAuthor("a", "b", 0, 10);
        assertNotNull(list);
    }

    /**
     * 测试根据ID查找课程，验证正常查询流程。
     */
    @Test
    void testGetById() {
        Course course = new Course();
        course.setId(1);
        when(courseMapper.getById(1L)).thenReturn(course);
        assertEquals(1, courseService.getById(1L).getId());
    }

    /**
     * 测试按名称和作者统计课程数量。
     */
    @Test
    void testCountByNameAndAuthor() {
        when(courseMapper.countByNameAndAuthor("a", "b")).thenReturn(5);
        assertEquals(5, courseService.countByNameAndAuthor("a", "b"));
    }

    /**
     * 测试添加课程，验证插入流程。
     */
    @Test
    void testAddCourse() {
        Course course = new Course();
        when(courseMapper.insert(any(Course.class))).thenReturn(1);
        int result = courseService.addCourse(course);
        assertEquals(1, result);
    }

    /**
     * 测试更新课程，验证更新流程。
     */
    @Test
    void testUpdateCourse() {
        Course course = new Course();
        when(courseMapper.update(course)).thenReturn(1);
        int result = courseService.updateCourse(course);
        assertEquals(1, result);
    }

    /**
     * 测试根据ID删除课程，验证删除流程。
     */
    @Test
    void testDeleteCourse() {
        when(courseMapper.deleteById(1)).thenReturn(1);
        int result = courseService.deleteCourse(1);
        assertEquals(1, result);
    }

    /**
     * 测试按条件查找课程，验证返回非空列表。
     */
    @Test
    void testFindByCondition() {
        when(courseMapper.selectByCondition("a", "b", 1L, 1, 0, 10)).thenReturn(Collections.emptyList());
        List<Course> list = courseService.findByCondition("a", "b", 1L, 1, 0, 10);
        assertNotNull(list);
    }

    /**
     * 测试按条件统计课程数量。
     */
    @Test
    void testCountByCondition() {
        when(courseMapper.countByCondition("a", "b", 1L, 1)).thenReturn(3);
        int result = courseService.countByCondition("a", "b", 1L, 1);
        assertEquals(3, result);
    }

    /**
     * 异常测试：添加课程时传null应抛出异常。
     */
    @Test
    void testAddCourseWithNull() {
        assertThrows(NullPointerException.class, () -> courseService.addCourse(null));
    }

    /**
     * 异常测试：更新课程时传null应抛出异常。
     */
    @Test
    void testUpdateCourseWithNull() {
        assertThrows(NullPointerException.class, () -> courseService.updateCourse(null));
    }

    /**
     * 边界测试：删除负ID课程，验证返回0。
     */
    @Test
    void testDeleteCourseWithNegativeId() {
        when(courseMapper.deleteById(-1)).thenReturn(0);
        int result = courseService.deleteCourse(-1);
        assertEquals(0, result);
    }

    /**
     * 边界测试：按空字符串查找课程，验证返回空列表。
     */
    @Test
    void testFindByNameAndAuthorWithEmpty() {
        when(courseMapper.selectByNameAndAuthor("", "", 0, 10)).thenReturn(Collections.emptyList());
        List<Course> list = courseService.findByNameAndAuthor("", "", 0, 10);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
}