package com.aicrm.module.intent.entity;

import com.aicrm.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 生成日志（成本核算与审计）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_generation_log")
public class AiGenerationLog extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;

    /** AI 服务标识（Python 侧） */
    private String service;

    /** 提示词哈希（去重/审计） */
    private String promptHash;

    /** 模型 */
    private String model;

    /** 输入 token 数 */
    private Integer inputTokens;

    /** 输出 token 数 */
    private Integer outputTokens;

    /** 耗时（毫秒） */
    private Integer latencyMs;

    /** 状态：success/failed */
    private String status;
}
