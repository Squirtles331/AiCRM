package com.aicrm.module.log.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志（审计）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
@Schema(description = "操作日志")
public class OperLog extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 操作人用户 ID */
    @Schema(description = "操作人用户 ID")
    private Long userId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名", example = "张三")
    private String userName;

    /** 业务模块 */
    @Schema(description = "业务模块", example = "字典管理")
    private String module;

    /** 操作内容 */
    @Schema(description = "操作内容", example = "创建字典类型")
    private String operation;

    /** 执行方法（全限定名） */
    @Schema(description = "执行方法（全限定名）")
    private String method;

    /** 请求地址 */
    @Schema(description = "请求地址", example = "/api/dicts/types")
    private String requestUrl;

    /** 请求方式 */
    @Schema(description = "请求方式", example = "POST")
    private String httpMethod;

    /** 请求参数（JSON，敏感字段脱敏） */
    @Schema(description = "请求参数（JSON，敏感字段已脱敏）")
    private String requestParams;

    /** 结果：1 成功 / 0 失败 */
    @Schema(description = "结果：1 成功 / 0 失败", example = "1")
    private Integer result;

    /** 异常信息 */
    @Schema(description = "异常信息")
    private String errorMsg;

    /** 来源 IP */
    @Schema(description = "来源 IP", example = "127.0.0.1")
    private String ip;

    /** 耗时（毫秒） */
    @Schema(description = "耗时（毫秒）", example = "15")
    private Long durationMs;
}
