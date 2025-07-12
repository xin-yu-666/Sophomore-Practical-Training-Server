package com.training.util;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CaptchaUtilTest {
    /**
     * 测试 defaultKaptcha 方法能正常生成 DefaultKaptcha 实例。
     * 覆盖 CaptchaUtil.defaultKaptcha 的正常分支。
     */
    @Test
    public void testDefaultKaptchaBean() {
        CaptchaUtil captchaUtil = new CaptchaUtil();
        DefaultKaptcha kaptcha = captchaUtil.defaultKaptcha();
        assertNotNull(kaptcha);
        assertNotNull(kaptcha.getConfig());
    }

    /**
     * 边界测试：多次生成 DefaultKaptcha 实例，确保每次都是新对象，互不影响。
     * 覆盖 CaptchaUtil.defaultKaptcha 的多次调用场景。
     */
    @Test
    public void testMultipleKaptchaInstances() {
        CaptchaUtil captchaUtil = new CaptchaUtil();
        DefaultKaptcha k1 = captchaUtil.defaultKaptcha();
        DefaultKaptcha k2 = captchaUtil.defaultKaptcha();
        assertNotSame(k1, k2); // 每次应为新实例
    }

    /**
     * 异常测试：调用 defaultKaptcha 方法时不应抛出异常。
     * 用 try-catch 包裹，断言不会抛出异常。
     */
    @Test
    public void testDefaultKaptchaException() {
        try {
            CaptchaUtil captchaUtil = new CaptchaUtil();
            DefaultKaptcha kaptcha = captchaUtil.defaultKaptcha();
            assertNotNull(kaptcha);
        } catch (Exception e) {
            fail("defaultKaptcha方法不应抛出异常");
        }
    }
}