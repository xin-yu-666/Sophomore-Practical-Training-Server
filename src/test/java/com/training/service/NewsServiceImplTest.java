package com.training.service;

import com.training.entity.News;
import com.training.mapper.NewsMapper;
import com.training.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsServiceImplTest {
    private NewsServiceImpl newsService;
    private NewsMapper newsMapper;

    @BeforeEach
    void setUp() throws Exception {
        newsMapper = mock(NewsMapper.class);
        newsService = new NewsServiceImpl();
        java.lang.reflect.Field f1 = NewsServiceImpl.class.getDeclaredField("newsMapper");
        f1.setAccessible(true);
        f1.set(newsService, newsMapper);
    }

    /**
     * 测试按条件查找新闻列表，验证返回非空列表。
     */
    @Test
    void testGetList() {
        when(newsMapper.findAll("a", "b", "c", 1L, 1)).thenReturn(Collections.emptyList());
        List<News> list = newsService.getList("a", "b", "c", 1L, 1);
        assertNotNull(list);
    }

    /**
     * 测试根据ID查找新闻，验证正常查询流程。
     */
    @Test
    void testGetById() {
        News news = new News();
        news.setId(1L);
        when(newsMapper.findById(1L)).thenReturn(news);
        assertEquals(1L, newsService.getById(1L).getId());
    }

    /**
     * 测试添加新闻，验证插入流程。
     */
    @Test
    void testAdd() {
        News news = new News();
        when(newsMapper.insert(any(News.class))).thenReturn(1);
        boolean result = newsService.add(news);
        assertTrue(result);
    }

    /**
     * 测试更新新闻，验证更新流程。
     */
    @Test
    void testUpdate() {
        News news = new News();
        when(newsMapper.update(news)).thenReturn(1);
        boolean result = newsService.update(news);
        assertTrue(result);
    }

    /**
     * 测试根据ID删除新闻，验证删除流程。
     */
    @Test
    void testDelete() {
        when(newsMapper.delete(1L)).thenReturn(1);
        boolean result = newsService.delete(1L);
        assertTrue(result);
    }

    /**
     * 测试更新新闻图片，验证更新流程。
     */
    @Test
    void testUpdateImage() {
        News news = new News();
        news.setId(1L);
        when(newsMapper.findById(1L)).thenReturn(news);
        when(newsMapper.update(news)).thenReturn(1);
        newsService.updateImage(1L, new byte[] { 1, 2, 3 });
        verify(newsMapper).update(news);
    }

    /**
     * 异常测试：添加新闻时传null应抛出异常。
     */
    @Test
    void testAddWithNull() {
        assertThrows(NullPointerException.class, () -> newsService.add(null));
    }

    /**
     * 异常测试：更新新闻时传null应抛出异常。
     */
    @Test
    void testUpdateWithNull() {
        assertThrows(NullPointerException.class, () -> newsService.update(null));
    }

    /**
     * 边界测试：删除负ID新闻，验证返回false。
     */
    @Test
    void testDeleteWithNegativeId() {
        when(newsMapper.delete(-1L)).thenReturn(0);
        boolean result = newsService.delete(-1L);
        assertFalse(result);
    }

    /**
     * 边界测试：按空条件查找新闻列表，验证返回空列表。
     */
    @Test
    void testGetListWithEmpty() {
        when(newsMapper.findAll("", "", "", null, null)).thenReturn(Collections.emptyList());
        List<News> list = newsService.getList("", "", "", null, null);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
}