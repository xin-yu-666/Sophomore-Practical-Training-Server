package com.training.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {
    /**
     * 测试无参success方法，返回默认成功结果。
     * 覆盖Result.success()的正常分支。
     */
    @Test
    public void testSuccessNoArg() {
        Result<String> result = Result.success();
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 测试带数据的success方法，返回带数据的成功结果。
     * 覆盖Result.success(T data)的正常分支。
     */
    @Test
    public void testSuccessWithData() {
        String data = "hello";
        Result<String> result = Result.success(data);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(data, result.getData());
    }

    /**
     * 测试只传错误信息的error方法，返回默认错误码和自定义信息。
     * 覆盖Result.error(String message)的正常分支。
     */
    @Test
    public void testErrorWithMessage() {
        String msg = "出错了";
        Result<String> result = Result.error(msg);
        assertEquals(500, result.getCode());
        assertEquals(msg, result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 测试传错误码和信息的error方法，返回自定义错误码和信息。
     * 覆盖Result.error(Integer code, String message)的正常分支。
     */
    @Test
    public void testErrorWithCodeAndMessage() {
        int code = 404;
        String msg = "未找到";
        Result<String> result = Result.error(code, msg);
        assertEquals(code, result.getCode());
        assertEquals(msg, result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 边界测试：success传null，data应为null。
     * 覆盖Result.success(T data)的null分支。
     */
    @Test
    public void testSuccessWithNull() {
        Result<Object> result = Result.success(null);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 边界测试：error传null message，message应为null。
     * 覆盖Result.error(String message)的null分支。
     */
    @Test
    public void testErrorWithNullMessage() {
        Result<Object> result = Result.error(null);
        assertEquals(500, result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 边界测试：error传空字符串，message应为空字符串。
     * 覆盖Result.error(Integer code, String message)的空字符串分支。
     */
    @Test
    public void testErrorWithEmptyMessage() {
        Result<Object> result = Result.error(400, "");
        assertEquals(400, result.getCode());
        assertEquals("", result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 边界测试：error传极端code值，能正常返回。
     * 覆盖Result.error(Integer code, String message)的极端数值分支。
     */
    @Test
    public void testErrorWithExtremeCode() {
        Result<Object> result = Result.error(Integer.MIN_VALUE, "极小值");
        assertEquals(Integer.MIN_VALUE, result.getCode());
        assertEquals("极小值", result.getMessage());
        assertNull(result.getData());

        result = Result.error(Integer.MAX_VALUE, "极大值");
        assertEquals(Integer.MAX_VALUE, result.getCode());
        assertEquals("极大值", result.getMessage());
        assertNull(result.getData());
    }

    /**
     * 异常/反向测试：success结果不应为错误码和错误信息。
     * 用于反向断言。
     */
    @Test
    public void testSuccessNotError() {
        Result<String> result = Result.success("ok");
        assertNotEquals(500, result.getCode());
        assertNotEquals("出错了", result.getMessage());
    }
}
