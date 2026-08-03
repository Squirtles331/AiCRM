package com.aicrm.module.lead.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 线索自动分配规则（3.2.4）
 * <p>rule_type: product（按产品线）/region（按地域）/round_robin（轮询组）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "lead_assign_rule", autoResultMap = true)
@Schema(description = "线索自动分配规则（3.2.4）")
public class LeadAssignRule extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 规则名称 */
    @Schema(description = "规则名称", example = "华东区域线索分配")
    private String ruleName;

    /** 规则类型：product/region/round_robin */
    @Schema(description = "规则类型：product按产品线/region按地域/round_robin轮询组", example = "region")
    private String ruleType;

    /** 匹配值：产品线或地域（product/region 规则用） */
    @Schema(description = "匹配值：产品线或地域（product/region 规则用）", example = "华东")
    private String matchValue;

    /** 指定销售 ID（product/region 规则用） */
    @Schema(description = "指定销售 ID（product/region 规则用）")
    private Long targetUserId;

    /** 轮询组销售 ID 数组（round_robin 规则用，JSONB） */
    @TableField(typeHandler = JsonbTypeHandler.class)
    @Schema(description = "轮询组销售 ID 数组（round_robin 规则用，JSONB），存储结构示例：[1,2,3]")
    private String targetGroupIds;

    /** 优先级（小在前） */
    @Schema(description = "优先级（数值越小越先匹配）", example = "1")
    private Integer sort;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;
}
