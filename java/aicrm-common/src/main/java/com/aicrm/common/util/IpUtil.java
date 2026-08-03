package com.aicrm.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端 IP 工具
 */
public final class IpUtil {

    private static final String UNKNOWN = "unknown";

    private IpUtil() {
    }

    /**
     * 获取真实客户端 IP（兼容反向代理 X-Forwarded-For / X-Real-IP）
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isInvalid(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isInvalid(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private static boolean isInvalid(String ip) {
        return ip == null || ip.isBlank() || UNKNOWN.equalsIgnoreCase(ip);
    }
}
