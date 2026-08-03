package com.aicrm.module.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建租户请求（含初始化管理员账号）
 */
@Data
@Schema(description = "创建租户请求（含初始化管理员账号）")
public class TenantCreateRequest {

    /** 企业名称 */
    @NotBlank(message = "企业名称不能为空")
    @Schema(description = "企业名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 套餐编码，为空默认 starter */
    @Schema(description = "套餐编码：starter/pro/enterprise，为空默认 starter", example = "starter")
    private String planCode;

    /** 坐席数上限，为空按套餐默认 */
    @Schema(description = "坐席数上限，为空按套餐默认")
    private Integer seatCount;

    /** 月度 AI 调用额度，为空按套餐默认 */
    @Schema(description = "月度 AI 调用额度，为空按套餐默认")
    private Long aiQuotaMonth;

    /** 到期时间，为空表示长期有效 */
    @Schema(description = "到期时间，为空表示长期有效")
    private LocalDateTime expireAt;

    /** 平台对接联系人 */
    @Schema(description = "平台对接联系人")
    private String contactName;

    /** 平台对接联系电话 */
    @Schema(description = "平台对接联系电话")
    private String contactMobile;

    /** 状态，为空默认启用 */
    @Schema(description = "状态：1启用/0停用，为空默认启用", example = "1")
    private Integer status;

    /** 初始化管理员手机号（登录账号） */
    @NotBlank(message = "管理员手机号不能为空")
    @Schema(description = "初始化管理员手机号（登录账号）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminMobile;

    /** 初始化管理员密码，为空使用默认密码 */
    @Schema(description = "初始化管理员密码，为空使用默认密码")
    private String adminPassword;
}
