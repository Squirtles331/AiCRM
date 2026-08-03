package com.aicrm.module.intent.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 抽取字段（报价前信息收集）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("extracted_field")
public class ExtractedField extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;

    /** 线索 ID */
    private Long leadId;

    /** 字段键：scene/qty/budget/lead_time/model */
    private String fieldKey;

    /** 字段值 */
    private String fieldValue;

    /** 置信度 */
    private BigDecimal confidence;

    /** 来源会话 ID */
    private Long sourceConversationId;
}
