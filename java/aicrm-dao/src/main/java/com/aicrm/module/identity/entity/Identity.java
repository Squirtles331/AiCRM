package com.aicrm.module.identity.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 身份（跨渠道合并核心）：手机号/邮箱/社媒ID/企微ID/WhatsApp/企业域名
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("identity")
@Schema(description = "身份（跨渠道合并核心）：手机号/邮箱/社媒ID/企微ID/WhatsApp/企业域名")
public class Identity extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 身份类型：mobile/email/social/wecom/whatsapp/domain */
    @Schema(description = "身份类型：mobile手机号/email邮箱/social社媒ID/wecom企微ID/whatsapp/domain企业域名", example = "mobile")
    private String identityType;

    /** 身份值 */
    @Schema(description = "身份值（如手机号、邮箱地址、社媒/企微/WhatsApp ID、企业域名）", example = "13800138000")
    private String identityValue;

    /** 状态：1 有效 / 0 失效 */
    @Schema(description = "状态：1 有效 / 0 失效", example = "1")
    private Integer status;

    /** 同意记录：0 未同意 / 1 已同意（合规） */
    @Schema(description = "同意记录：0 未同意 / 1 已同意（合规）", example = "0")
    private Integer consent;

    /** 同意时间 */
    @Schema(description = "同意时间", example = "2026-08-03 12:00:00")
    private LocalDateTime consentTime;
}
