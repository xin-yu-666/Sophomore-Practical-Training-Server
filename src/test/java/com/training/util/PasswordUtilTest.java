package com.training.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {
    /**
     * 测试 encrypt 方法在 DEV_MODE=true 时，普通密码应原样返回。
     * 覆盖 PasswordUtil.encrypt 的 DEV_MODE 分支。
     */
    @Test
    public void testEncryptDevMode() {
        String password = "123456";
        String encrypted = PasswordUtil.encrypt(password);
        assertEquals(password, encrypted);
    }

    /**
     * 测试 encrypt 方法在 DEV_MODE=true 时，空字符串应原样返回。
     * 覆盖 PasswordUtil.encrypt 的 DEV_MODE 分支（边界情况）。
     */
    @Test
    public void testEncryptEmptyString() {
        String encrypted = PasswordUtil.encrypt("");
        assertEquals("", encrypted);
    }

    /**
     * 测试 encrypt 方法在 DEV_MODE=true 时，传入 null 应返回 null。
     * 覆盖 PasswordUtil.encrypt 的 DEV_MODE 分支（null 边界情况）。
     */
    @Test
    public void testEncryptNull() {
        assertNull(PasswordUtil.encrypt(null));
    }

    /**
     * 测试 match 方法在 DEV_MODE=true 时，普通字符串相等和不等的情况。
     * 覆盖 PasswordUtil.match 的 DEV_MODE 分支。
     */
    @Test
    public void testMatchDevMode() {
        assertTrue(PasswordUtil.match("abc", "abc"));
        assertFalse(PasswordUtil.match("abc", "def"));
        assertTrue(PasswordUtil.match("", ""));
    }

    /**
     * 测试 match 方法在 DEV_MODE=true 时，空字符串匹配的边界情况。
     * 覆盖 PasswordUtil.match 的 DEV_MODE 分支（空字符串边界）。
     */
    @Test
    public void testMatchEmptyString() {
        assertTrue(PasswordUtil.match("", ""));
        assertFalse(PasswordUtil.match("", "notempty"));
    }
}