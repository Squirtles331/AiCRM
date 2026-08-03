package com.aicrm.common.context;

/**
 * 租户上下文：当前线程的 tenantId
 * <p>
 * 由请求拦截器 / MQ 消费者设置，MyBatis-Plus 租户拦截器自动读取并注入 SQL 条件。
 * 使用后必须 clear()，避免线程池复用导致串租户。
 */
public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /** 当前租户 ID，未设置返回 null */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /** 当前租户 ID，未设置返回 -1（查询时匹配不到数据，保证不越权） */
    public static long getTenantIdOrDefault() {
        Long tenantId = TENANT_ID.get();
        return tenantId == null ? -1L : tenantId;
    }

    public static void clear() {
        TENANT_ID.remove();
    }
}
