package com.training.test;

import com.training.entity.Enterprise;
import com.training.mapper.EnterpriseMapper;
import com.training.service.impl.EnterpriseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnterpriseServiceImplTest {
    private EnterpriseServiceImpl enterpriseService;
    private EnterpriseMapper enterpriseMapper;

    @BeforeEach
    void setUp() throws Exception {
        enterpriseMapper = mock(EnterpriseMapper.class);
        enterpriseService = new EnterpriseServiceImpl();
        java.lang.reflect.Field f1 = EnterpriseServiceImpl.class.getDeclaredField("enterpriseMapper");
        f1.setAccessible(true);
        f1.set(enterpriseService, enterpriseMapper);
    }

    /**
     * 测试根据ID查找企业，验证正常查询流程。
     */
    @Test
    void testFindById() {
        Enterprise e = new Enterprise();
        e.setId(1L);
        when(enterpriseMapper.findById(1L)).thenReturn(e);
        assertEquals(1L, enterpriseService.findById(1L).getId());
    }

    /**
     * 测试根据名称查找企业，验证正常查询流程。
     */
    @Test
    void testFindByName() {
        Enterprise e = new Enterprise();
        e.setName("test");
        when(enterpriseMapper.findByName("test")).thenReturn(e);
        assertEquals("test", enterpriseService.findByName("test").getName());
    }

    /**
     * 测试查找所有企业，验证返回非空列表。
     */
    @Test
    void testFindAll() {
        when(enterpriseMapper.findAll()).thenReturn(Collections.emptyList());
        List<Enterprise> list = enterpriseService.findAll();
        assertNotNull(list);
    }

    /**
     * 测试创建企业，验证插入流程。
     */
    @Test
    void testCreate() {
        Enterprise e = new Enterprise();
        when(enterpriseMapper.insert(e)).thenReturn(1);
        enterpriseService.create(e);
        verify(enterpriseMapper).insert(e);
    }

    /**
     * 测试更新企业，验证更新流程。
     */
    @Test
    void testUpdate() {
        Enterprise e = new Enterprise();
        when(enterpriseMapper.update(e)).thenReturn(1);
        enterpriseService.update(e);
        verify(enterpriseMapper).update(e);
    }

    /**
     * 测试根据ID删除企业，验证删除流程。
     */
    @Test
    void testDeleteById() {
        when(enterpriseMapper.deleteById(1L)).thenReturn(1);
        enterpriseService.deleteById(1L);
        verify(enterpriseMapper).deleteById(1L);
    }

    /**
     * 测试条件查询企业，验证返回非空列表。
     */
    @Test
    void testFindByCondition() {
        when(enterpriseMapper.findByCondition("a", "b", 1)).thenReturn(Collections.emptyList());
        List<Enterprise> list = enterpriseService.findByCondition("a", "b", 1);
        assertNotNull(list);
    }

    /**
     * 异常测试：创建企业时传null应抛出异常。
     */
    @Test
    void testCreateWithNull() {
        assertThrows(NullPointerException.class, () -> enterpriseService.create(null));
    }

    /**
     * 异常测试：更新企业时传null应抛出异常。
     */
    @Test
    void testUpdateWithNull() {
        assertThrows(NullPointerException.class, () -> enterpriseService.update(null));
    }

    /**
     * 边界测试：删除负ID企业，验证deleteById被调用。
     */
    @Test
    void testDeleteByIdWithNegativeId() {
        when(enterpriseMapper.deleteById(-1L)).thenReturn(0);
        enterpriseService.deleteById(-1L);
        verify(enterpriseMapper).deleteById(-1L);
    }

    /**
     * 边界测试：查找名称为null的企业，验证返回null。
     */
    @Test
    void testFindByNameWithNull() {
        when(enterpriseMapper.findByName(null)).thenReturn(null);
        assertNull(enterpriseService.findByName(null));
    }
}