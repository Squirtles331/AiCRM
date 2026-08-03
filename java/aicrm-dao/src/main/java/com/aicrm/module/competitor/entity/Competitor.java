package com.aicrm.module.competitor.entity;

import com.aicrm.common.base.BaseEntity;
import com.aicrm.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 竞品主体档案（3.4.2）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "competitor", autoResultMap = true)
@Schema(description = "竞品主体档案")
public class Competitor extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 竞品名称 */
    @Schema(description = "竞品名称", example = "某某云 CRM")
    private String name;

    /** 竞品品类 */
    @Schema(description = "竞品品类", example = "CRM")
    private String category;

    /** 官网地址 */
    @Schema(description = "官网地址", example = "https://www.example.com")
    private String officialUrl;

    /** 主体描述 */
    @Schema(description = "主体描述")
    private String description;

    /** 优势列表（JSONB 数组） */
    @Schema(description = "优势列表（JSONB 数组，如 [\"功能全面\",\"价格低\"]）", example = "[\"功能全面\",\"价格低\"]")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String strengths;

    /** 劣势列表（JSONB 数组） */
    @Schema(description = "劣势列表（JSONB 数组，如 [\"实施复杂\",\"售后差\"]）", example = "[\"实施复杂\",\"售后差\"]")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String weaknesses;

    /** 攻防话术列表 [{scenario,tactic}]（JSONB） */
    @Schema(description = "攻防话术列表（JSONB 数组，元素含 scenario 场景/tactic 话术，如 [{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]）", example = "[{\"scenario\":\"比价格\",\"tactic\":\"强调总拥有成本\"}]")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String defenseTactics;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;
}
