package com.aicrm.module.identity.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 身份到实体映射（identity → lead/contact/customer）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("identity_mapping")
@Schema(description = "身份到实体映射（identity → lead/contact/customer）")
public class IdentityMapping extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 身份 ID */
    @Schema(description = "身份 ID")
    private Long identityId;

    /** 实体类型：lead/contact/customer */
    @Schema(description = "实体类型：lead线索/contact联系人/customer客户", example = "lead")
    private String entityType;

    /** 实体 ID */
    @Schema(description = "实体 ID")
    private Long entityId;

    /** 匹配置信度 */
    @Schema(description = "匹配置信度（0-1，默认 1）", example = "1")
    private BigDecimal confidence;

    /** 来源 */
    @Schema(description = "来源（如 manual手动/auto自动匹配）", example = "manual")
    private String source;
}
