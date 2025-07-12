package com.training.service;

import com.training.dto.UserRegisterDTO;
import com.training.entity.User;
import com.training.mapper.UserMapper;
import com.training.mapper.UserRoleMapper;
import com.training.service.impl.UserServiceImpl;
import com.training.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserMapper userMapper;
    private UserRoleMapper userRoleMapper;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws Exception {
        userMapper = mock(UserMapper.class);
        userRoleMapper = mock(UserRoleMapper.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        userService = new UserServiceImpl();
        java.lang.reflect.Field f1 = UserServiceImpl.class.getDeclaredField("userMapper");
        f1.setAccessible(true);
        f1.set(userService, userMapper);
        java.lang.reflect.Field f2 = UserServiceImpl.class.getDeclaredField("userRoleMapper");
        f2.setAccessible(true);
        f2.set(userService, userRoleMapper);
        java.lang.reflect.Field f3 = UserServiceImpl.class.getDeclaredField("jdbcTemplate");
        f3.setAccessible(true);
        f3.set(userService, jdbcTemplate);
    }

    /**
     * 测试根据用户名查找用户，验证正常查询流程。
     */
    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername("test");
        when(userMapper.findByUsername("test")).thenReturn(user);
        assertEquals("test", userService.findByUsername("test").getUsername());
    }

    /**
     * 测试根据ID查找用户，验证正常查询流程。
     */
    @Test
    void testFindById() {
        User user = new User();
        user.setId(1L);
        when(userMapper.findById(1L)).thenReturn(user);
        assertEquals(1L, userService.findById(1L).getId());
    }

    /**
     * 测试查找所有用户，验证返回非空列表。
     */
    @Test
    void testFindAll() {
        when(userMapper.findAll()).thenReturn(Collections.emptyList());
        List<User> users = userService.findAll();
        assertNotNull(users);
    }

    /**
     * 测试根据企业ID查找用户，验证返回非空列表。
     */
    @Test
    void testFindByEnterpriseId() {
        when(userMapper.findByEnterpriseId(2L)).thenReturn(Collections.emptyList());
        List<User> users = userService.findByEnterpriseId(2L);
        assertNotNull(users);
    }

    /**
     * 测试创建用户，验证插入流程和数据库操作。
     */
    @Test
    void testCreate() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setId(1L);
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);
        userService.create(user);
        verify(userMapper).insert(any(User.class));
    }

    /**
     * 测试更新用户，验证更新流程和数据库操作。
     */
    @Test
    void testUpdate() {
        User user = new User();
        user.setId(1L);
        when(userMapper.update(user)).thenReturn(1);
        userService.update(user);
        verify(userMapper).update(user);
    }

    /**
     * 测试根据ID删除用户，验证删除流程和数据库操作。
     */
    @Test
    void testDeleteById() {
        when(userMapper.deleteById(1L)).thenReturn(1);
        userService.deleteById(1L);
        verify(userMapper).deleteById(1L);
    }

    /**
     * 测试条件查询用户，验证返回非空列表。
     */
    @Test
    void testFindByCondition() {
        when(userMapper.findByCondition("a", "b", 1)).thenReturn(Collections.emptyList());
        List<User> users = userService.findByCondition("a", "b", 1);
        assertNotNull(users);
    }

    /**
     * 测试带企业名的条件查询用户，验证返回非空列表。
     */
    @Test
    void testFindByConditionWithEnterprise() {
        when(userMapper.findByConditionWithEnterprise("a", "b", 1, "c")).thenReturn(Collections.emptyList());
        List<User> users = userService.findByCondition("a", "b", 1, "c");
        assertNotNull(users);
    }

    /**
     * 测试登录成功流程，验证token生成。
     */
    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.encrypt("123456"));
        user.setStatus(1);
        when(userMapper.findByUsername("test")).thenReturn(user);
        String token = userService.login("test", "123456");
        assertNotNull(token);
    }

    /**
     * 测试登录时用户不存在的异常分支。
     */
    @Test
    void testLoginUserNotExist() {
        when(userMapper.findByUsername("notexist")).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.login("notexist", "123456"));
        assertTrue(ex.getMessage().contains("用户不存在"));
    }

    /**
     * 测试登录时密码错误的异常分支。
     */
    @Test
    void testLoginPasswordError() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.encrypt("123456"));
        user.setStatus(1);
        when(userMapper.findByUsername("test")).thenReturn(user);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.login("test", "wrong"));
        assertTrue(ex.getMessage().contains("密码错误"));
    }

    /**
     * 测试登录时账号被禁用的异常分支。
     */
    @Test
    void testLoginUserDisabled() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(PasswordUtil.encrypt("123456"));
        user.setStatus(0);
        when(userMapper.findByUsername("test")).thenReturn(user);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.login("test", "123456"));
        assertTrue(ex.getMessage().contains("账号已被禁用"));
    }

    /**
     * 异常测试：重置密码时用户名为null，断言抛出异常。
     */
    @Test
    void testResetPasswordWithNullUsername() {
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword(null, "123456"));
        assertTrue(ex.getMessage().contains("用户名不能为空"));
    }

    /**
     * 异常测试：重置密码时用户名为空字符串，断言抛出异常。
     */
    @Test
    void testResetPasswordWithEmptyUsername() {
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("   ", "123456"));
        assertTrue(ex.getMessage().contains("用户名不能为空"));
    }

    /**
     * 异常测试：重置密码时新密码为null，断言抛出异常。
     */
    @Test
    void testResetPasswordWithNullPassword() {
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("user", null));
        assertTrue(ex.getMessage().contains("新密码不能为空"));
    }

    /**
     * 异常测试：重置密码时新密码过短，断言抛出异常。
     */
    @Test
    void testResetPasswordWithShortPassword() {
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("user", "123"));
        assertTrue(ex.getMessage().contains("密码长度必须在6-20个字符之间"));
    }

    /**
     * 异常测试：重置密码时用户不存在，断言抛出异常。
     */
    @Test
    void testResetPasswordUserNotExist() {
        when(userMapper.findByUsername("notexist")).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("notexist", "123456"));
        assertTrue(ex.getMessage().contains("用户名不存在"));
    }

    /**
     * 异常测试：重置密码时账号被禁用，断言抛出异常。
     */
    @Test
    void testResetPasswordUserDisabled() {
        User user = new User();
        user.setStatus(0);
        when(userMapper.findByUsername("user")).thenReturn(user);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("user", "123456"));
        assertTrue(ex.getMessage().contains("账号已被禁用"));
    }

    /**
     * 异常测试：修改密码时用户不存在，断言抛出异常。
     */
    @Test
    void testUpdatePasswordUserNotExist() {
        when(userMapper.findById(1L)).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.updatePassword(1L, "old", "new"));
        assertTrue(ex.getMessage().contains("用户不存在"));
    }

    /**
     * 异常测试：修改密码时原密码错误，断言抛出异常。
     */
    @Test
    void testUpdatePasswordOldPasswordError() {
        User user = new User();
        user.setPassword(PasswordUtil.encrypt("old"));
        when(userMapper.findById(1L)).thenReturn(user);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.updatePassword(1L, "wrong", "new"));
        assertTrue(ex.getMessage().contains("原密码错误"));
    }

    /**
     * 异常测试：更新头像时用户不存在，断言抛出异常。
     */
    @Test
    void testUpdateAvatarUserNotExist() {
        when(userMapper.findByUsername("notexist")).thenReturn(null);
        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.updateAvatar("notexist", new byte[] { 1, 2 }));
        assertTrue(ex.getMessage().contains("用户不存在"));
    }

    /**
     * 测试更新头像时传null头像，验证正常流程。
     */
    @Test
    void testUpdateAvatarWithNullAvatar() {
        User user = new User();
        when(userMapper.findByUsername("test")).thenReturn(user);
        when(userMapper.update(user)).thenReturn(1);
        userService.updateAvatar("test", null);
        verify(userMapper).update(user);
    }

    /**
     * 测试带企业信息的用户名查找，验证正常流程。
     */
    @Test
    void testFindByUsernameWithEnterpriseNormal() {
        User user = new User();
        when(userMapper.findByUsernameWithEnterprise("test")).thenReturn(user);
        assertEquals(user, userService.findByUsernameWithEnterprise("test"));
    }

    /**
     * 边界测试：带企业信息的用户名查找为null，验证返回null。
     */
    @Test
    void testFindByUsernameWithEnterpriseNull() {
        when(userMapper.findByUsernameWithEnterprise(null)).thenReturn(null);
        assertNull(userService.findByUsernameWithEnterprise(null));
    }

    /**
     * 测试分配角色正常流程。
     */
    @Test
    void testAssignRoleNormal() {
        doNothing().when(userRoleMapper).insertUserRole(1L, 2L);
        userService.assignRole(1L, 2L);
        verify(userRoleMapper).insertUserRole(1L, 2L);
    }

    /**
     * 边界测试：分配角色时传null，验证正常流程。
     */
    @Test
    void testAssignRoleWithNull() {
        doNothing().when(userRoleMapper).insertUserRole(null, null);
        userService.assignRole(null, null);
        verify(userRoleMapper).insertUserRole(null, null);
    }

    /**
     * 测试查找所有角色，验证正常流程。
     */
    @Test
    void testFindAllRoles() {
        List<com.training.entity.Role> roles = Collections.emptyList();
        when(userMapper.findAllRoles()).thenReturn(roles);
        assertEquals(roles, userService.findAllRoles());
    }

    /**
     * 测试根据用户ID查找角色，验证正常流程。
     */
    @Test
    void testFindRolesByUserIdNormal() {
        List<String> roles = List.of("admin", "user");
        when(userMapper.findRolesByUserId(1L)).thenReturn(roles);
        assertEquals(roles, userService.findRolesByUserId(1L));
    }

    /**
     * 边界测试：根据null用户ID查找角色，验证返回空列表。
     */
    @Test
    void testFindRolesByUserIdNull() {
        when(userMapper.findRolesByUserId(null)).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), userService.findRolesByUserId(null));
    }

    /**
     * 异常测试：重置密码时数据库更新结果为0，断言抛出异常。
     */
    @Test
    void testResetPasswordUpdateResultZero() {
        User user = new User();
        user.setStatus(1);
        user.setPassword(PasswordUtil.encrypt("oldpass"));
        when(userMapper.findByUsername("user")).thenReturn(user);
        when(userMapper.update(user)).thenReturn(0);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("user", "newpass123"));
        assertTrue(ex.getMessage().contains("密码更新失败"));
    }

    /**
     * 异常测试：重置密码后验证失败，断言抛出异常。
     */
    @Test
    void testResetPasswordUpdateVerifyFail() {
        User user = new User();
        user.setStatus(1);
        user.setPassword(PasswordUtil.encrypt("oldpass"));
        when(userMapper.findByUsername("user")).thenReturn(user);
        when(userMapper.update(user)).thenReturn(1);
        User updatedUser = new User();
        updatedUser.setPassword("notmatch");
        when(userMapper.findByUsername("user")).thenReturn(user, updatedUser);
        Exception ex = assertThrows(RuntimeException.class, () -> userService.resetPassword("user", "newpass123"));
        assertTrue(ex.getMessage().contains("密码更新验证失败"));
    }

    /**
     * 测试重置密码成功流程。
     */
    @Test
    void testResetPasswordSuccess() {
        User user = new User();
        user.setStatus(1);
        user.setPassword(PasswordUtil.encrypt("oldpass"));
        // 第一次findByUsername返回user，第二次返回user（模拟更新后密码一致）
        when(userMapper.findByUsername("user")).thenReturn(user, user);
        when(userMapper.update(user)).thenReturn(1);
        userService.resetPassword("user", "newpass123");
        verify(userMapper).update(user);
    }

    /**
     * 异常测试：创建用户时传null应抛出异常。
     */
    @Test
    void testCreateWithNullUser() {
        assertThrows(NullPointerException.class, () -> userService.create(null));
    }

    /**
     * 异常测试：更新用户时传null应抛出异常。
     */
    @Test
    void testUpdateWithNullUser() {
        assertThrows(NullPointerException.class, () -> userService.update(null));
    }

    /**
     * 异常测试：修改密码时userId为null，断言抛出异常。
     */
    @Test
    void testUpdatePasswordWithNullUserId() {
        assertThrows(NullPointerException.class, () -> userService.updatePassword(null, "old", "new"));
    }

    /**
     * 异常测试：修改密码时原密码为null，断言抛出异常。
     */
    @Test
    void testUpdatePasswordWithNullOldPassword() {
        assertThrows(NullPointerException.class, () -> userService.updatePassword(1L, null, "new"));
    }

    /**
     * 异常测试：修改密码时新密码为null，断言抛出异常。
     */
    @Test
    void testUpdatePasswordWithNullNewPassword() {
        assertThrows(NullPointerException.class, () -> userService.updatePassword(1L, "old", null));
    }

    /**
     * 测试修改密码成功流程。
     */
    @Test
    void testUpdatePasswordSuccess() {
        User user = new User();
        user.setPassword(PasswordUtil.encrypt("oldpass"));
        when(userMapper.findById(1L)).thenReturn(user);
        when(userMapper.update(user)).thenReturn(1);
        userService.updatePassword(1L, "oldpass", "newpass123");
        verify(userMapper).update(user);
    }
}
