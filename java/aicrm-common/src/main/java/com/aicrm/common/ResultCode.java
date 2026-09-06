package com.aicrm.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务返回码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    SYSTEM_ERROR(500, "系统内部错误"),

    // ---- 业务码段 1000+ ----
    TENANT_NOT_FOUND(1001, "租户不存在"),
    TENANT_DISABLED(1002, "租户已停用"),
    TENANT_EXPIRED(1003, "租户已到期，请联系平台续费"),
    PLAN_NOT_FOUND(1004, "套餐不存在"),
    PLAN_DISABLED(1005, "套餐已下架"),
    USER_NOT_FOUND(1101, "用户不存在"),
    USER_DISABLED(1102, "用户已停用"),

    LEAD_NOT_FOUND(1201, "线索不存在"),
    LEAD_STATUS_INVALID(1202, "线索状态非法"),

    CHANNEL_NOT_FOUND(1301, "渠道不存在"),
    CHANNEL_ACCOUNT_NOT_FOUND(1302, "渠道账号不存在"),
    CHANNEL_EVENT_DUPLICATED(1303, "渠道事件重复"),
    CHANNEL_API_ERROR(1304, "渠道开放平台 API 调用失败"),

    CONVERSATION_NOT_FOUND(1401, "会话不存在"),

    RISK_BLOCKED(1501, "触发风控规则，操作被拦截"),

    FILE_UPLOAD_ERROR(1701, "文件上传失败"),
    FILE_TOO_LARGE(1702, "文件大小超出限制");

    private final int code;
    private final String message;
}
