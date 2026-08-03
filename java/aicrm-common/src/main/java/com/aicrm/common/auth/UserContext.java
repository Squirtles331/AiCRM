package com.aicrm.common.auth;

import com.aicrm.common.context.TenantContext;

import java.util.List;

/**
 * 当前登录用户上下文
 * <p>
 * 由登录拦截器从 JWT 解析后设置，请求结束清理。
 */
public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> ROLE_CODES = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> PERMS = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, List<String> roleCodes, List<String> perms) {
        USER_ID.set(userId);
        ROLE_CODES.set(roleCodes);
        PERMS.set(perms);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    /** 当前用户角色码集合（可能为空） */
    public static List<String> getRoleCodes() {
        return ROLE_CODES.get();
    }

    /** 便捷方法：主角色码（角色集合第一个），无角色返回 null */
    public static String getRoleCode() {
        List<String> roles = ROLE_CODES.get();
        return roles == null || roles.isEmpty() ? null : roles.get(0);
    }

    /** 当前用户按钮权限码集合（可能为空） */
    public static List<String> getPerms() {
        return PERMS.get();
    }

    /** 便捷方法：当前用户所属租户（优先登录上下文 → 租户上下文） */
    public static Long getTenantId() {
        return TenantContext.getTenantId();
    }

    public static void clear() {
        USER_ID.remove();
        ROLE_CODES.remove();
        PERMS.remove();
    }
}
