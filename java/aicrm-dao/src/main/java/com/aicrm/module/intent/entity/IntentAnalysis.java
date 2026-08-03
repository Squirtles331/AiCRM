package com.aicrm.module.intent.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 意向分析结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("intent_analysis")
public class IntentAnalysis extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;

    /** 会话 ID */
    private Long conversationId;

    /** 意向：quote/sample/selection/other */
    private String intent;

    /** 置信度 */
    private BigDecimal confidence;

    /** 模型版本 */
    private String modelVersion;

    /** 判定依据片段 */
    private String evidence;
}
