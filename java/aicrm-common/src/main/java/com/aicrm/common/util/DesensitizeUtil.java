package com.aicrm.common.util;

/**
 * 数据脱敏工具
 * <p>
 * 用于接口返回/日志打印时隐藏敏感信息。
 */
public final class DesensitizeUtil {

    private DesensitizeUtil() {
    }

    /**
     * 手机号：138****1234
     */
    public static String mobile(String value) {
        return replaceRange(value, 3, 7);
    }

    /**
     * 座机/电话：保留前 3 后 4
     */
    public static String phone(String value) {
        return replaceRange(value, 3, value == null ? 0 : value.length() - 4);
    }

    /**
     * 邮箱：a***@domain.com
     */
    public static String email(String value) {
        if (value == null || value.isBlank() || !value.contains("@")) {
            return value;
        }
        int at = value.indexOf('@');
        String prefix = value.substring(0, at);
        String maskedPrefix = prefix.length() <= 2
                ? prefix.charAt(0) + "***"
                : prefix.substring(0, 2) + "***";
        return maskedPrefix + value.substring(at);
    }

    /**
     * 姓名：张* / 张*丰
     */
    public static String name(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() == 1) {
            return value;
        }
        if (value.length() == 2) {
            return value.charAt(0) + "*";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            sb.append(i == 0 || i == value.length() - 1 ? value.charAt(i) : '*');
        }
        return sb.toString();
    }

    /**
     * 身份证：110***********1234
     */
    public static String idCard(String value) {
        if (value == null || value.length() < 8) {
            return value;
        }
        return value.substring(0, 3) + "***********" + value.substring(value.length() - 4);
    }

    /**
     * 自定义区间打码
     *
     * @param start 保留前 start 位
     * @param end   从 end 位起保留（剩余保留 end..len）
     */
    private static String replaceRange(String value, int start, int end) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int len = value.length();
        if (start < 0 || end > len || start > end) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(i < start || i >= end ? value.charAt(i) : '*');
        }
        return sb.toString();
    }
}
