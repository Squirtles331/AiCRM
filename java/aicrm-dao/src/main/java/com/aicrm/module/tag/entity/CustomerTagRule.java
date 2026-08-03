package com.aicrm.module.tag.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自动标签规则（3.2.3）
 * <p>condition_field: score/intent_level/stage/industry/region/source
 * <br>condition_op: gt/gte/lt/lte/eq/contains
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("customer_tag_rule")
@Schema(description = "自动标签规则（3.2.3）")
public class CustomerTagRule extends BaseEntity {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 规则名称 */
    @Schema(description = "规则名称", example = "评分高于80自动打标")
    private String ruleName;

    /** 命中后打的标签 ID */
    @Schema(description = "命中后打的标签 ID")
    private Long tagId;

    /** 条件字段 */
    @Schema(description = "条件字段：score评分/intent_level意向等级/stage客户阶段/industry行业/region地区/source来源", example = "score")
    private String conditionField;

    /** 条件操作符 */
    @Schema(description = "条件操作符：gt大于/gte大于等于/lt小于/lte小于等于/eq等于/contains包含", example = "gte")
    private String conditionOp;

    /** 条件值 */
    @Schema(description = "条件值（与 condition_field 对应，如评分阈值、阶段取值、行业名等）", example = "80")
    private String conditionValue;

    /** 状态：1 启用 / 0 停用 */
    @Schema(description = "状态：1 启用 / 0 停用", example = "1")
    private Integer status;
}
