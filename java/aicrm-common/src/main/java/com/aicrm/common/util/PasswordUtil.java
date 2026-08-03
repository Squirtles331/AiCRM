package com.aicrm.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具：BCrypt 哈希（登录密码不可逆存储）
 */
public final class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {
    }

    /** 明文密码 → BCrypt 哈希 */
    public static String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /** 校验明文密码是否匹配哈希 */
    public static boolean matches(String rawPassword, String encoded) {
        return encoded != null && ENCODER.matches(rawPassword, encoded);
    }
}
