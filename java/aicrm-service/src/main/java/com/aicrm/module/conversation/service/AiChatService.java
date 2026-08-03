package com.aicrm.module.conversation.service;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * AI 对话内部服务（3.3.3）：统一封装意图识别 + 对话生成，供 AI 接待 / 转接 / 侧边栏复用
 * <p>AI 服务不可用时自动降级为规则话术，不阻塞接待流程
 */
public interface AiChatService {

    /**
     * AI 接待入口：追加客户消息 → 意向判定 → 生成回复（AI 优先，降级规则话术）→ 落库
     *
     * @param conversationId 会话 ID
     * @param customerMessage 客户消息内容
     * @return AI 回复结果（含转人工建议）
     */
    AiReply handleIncoming(Long conversationId, String customerMessage);

    /**
     * 生成推荐回复话术（人工会话 / 企微侧边栏用，按意向返回规则话术）
     *
     * @param intent quote/sample/selection/other
     */
    String suggestReply(String intent);

    /**
     * 转人工条件评估：基于最近意向分析结果判断是否需要转人工（3.3.4 触发条件）
     */
    TransferEvaluation evaluate(Long conversationId);

    /** AI 回复结果 */
    @Schema(description = "AI 回复结果")
    record AiReply(
            @Schema(description = "回复内容") String content,
            @Schema(description = "意向：quote报价/sample样品/selection选型/other其他", example = "quote") String intent,
            @Schema(description = "置信度（0-1）", example = "0.82") BigDecimal confidence,
            @Schema(description = "是否建议转人工") boolean shouldTransfer) {
    }

    /** 转人工评估结果 */
    @Schema(description = "转人工评估结果")
    record TransferEvaluation(
            @Schema(description = "是否建议转人工") boolean shouldTransfer,
            @Schema(description = "意向：quote报价/sample样品/selection选型/other其他", example = "selection") String intent,
            @Schema(description = "置信度（0-1）", example = "0.76") BigDecimal confidence,
            @Schema(description = "判定原因") String reason) {
    }
}
