package com.training.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    /**
     * 测试生成token并解析用户名，验证主流程。
     * 覆盖 JwtUtil.generateToken 和 getUsernameFromToken 的正常分支。
     */
    @Test
    public void testGenerateAndParseToken() {
        String username = "testuser";
        String token = JwtUtil.generateToken(username);
        assertNotNull(token);
        String parsedUsername = JwtUtil.getUsernameFromToken(token);
        assertEquals(username, parsedUsername);
    }

    /**
     * 边界测试：用户名为空字符串，生成token后解析应为null。
     * 覆盖 JwtUtil.generateToken 和 getUsernameFromToken 的边界分支。
     */
    @Test
    public void testGenerateTokenWithEmptyUsername() {
        String token = JwtUtil.generateToken("");
        assertNotNull(token);
        String parsed = JwtUtil.getUsernameFromToken(token);
        assertNull(parsed);
    }

    /**
     * 边界测试：用户名为null，生成token后解析应为null。
     * 覆盖 JwtUtil.generateToken 和 getUsernameFromToken 的null分支。
     */
    @Test
    public void testGenerateTokenWithNullUsername() {
        String token = JwtUtil.generateToken(null);
        assertNotNull(token);
        String parsed = JwtUtil.getUsernameFromToken(token);
        assertNull(parsed);
    }

    /**
     * 测试token校验，包含合法和非法token。
     * 覆盖 JwtUtil.validateToken 的正常和异常分支。
     */
    @Test
    public void testValidateToken() {
        String token = JwtUtil.generateToken("user");
        assertTrue(JwtUtil.validateToken(token));
        assertFalse(JwtUtil.validateToken(token + "invalid"));
    }

    /**
     * 异常测试：解析非法token应抛出异常。
     * 覆盖 JwtUtil.getUsernameFromToken 的异常分支。
     */
    @Test
    public void testGetUsernameFromInvalidToken() {
        assertThrows(Exception.class, () -> JwtUtil.getUsernameFromToken("invalid.token.value"));
    }

    /**
     * 边界/异常测试：校验伪造或过期token应返回false。
     * 覆盖 JwtUtil.validateToken 的异常分支。
     */
    @Test
    public void testValidateExpiredToken() {
        String fakeExpiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDAwMDAwMDB9.signature";
        assertFalse(JwtUtil.validateToken(fakeExpiredToken));
    }
}