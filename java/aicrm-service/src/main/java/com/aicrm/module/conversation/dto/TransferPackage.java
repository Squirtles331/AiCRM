package com.aicrm.module.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 转人工交接包：坐席接手时看到的会话上下文
 * <p>
 * 包含：对话摘要、意向判定、缺失字段、推荐回复、下一步动作清单。
 * M1 按需实时组装（AI 摘要 + 最近意向 + 已抽取字段）；
 * V2 可落库 transfer_package 表做快照留痕。
 */
@Data
@Schema(description = "转人工交接包：坐席接手时看到的会话上下文（对话摘要/意向判定/缺失字段/推荐回复/下一步动作）")
public class TransferPackage {

    /** 会话 ID */
    @Schema(description = "会话 ID", example = "100")
    private Long conversationId;

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;

    /** 线索 ID */
    @Schema(description = "线索 ID", example = "50")
    private Long leadId;

    /** 会话类型：dm/wecom_chat/whatsapp */
    @Schema(description = "会话类型：dm直接私信/wecom_chat企微聊天/whatsapp", example = "wecom_chat")
    private String conversationType;

    /** 接手坐席 ID */
    @Schema(description = "接手坐席 ID", example = "8")
    private Long operatorId;

    /** 对话摘要（AI summary，失败时降级提示） */
    @Schema(description = "对话摘要（AI summary，失败时降级提示）")
    private String summary;

    /** 摘要来源：ai/fallback */
    @Schema(description = "摘要来源：ai/fallback", example = "ai")
    private String summarySource;

    /** 意向：quote/sample/selection/other */
    @Schema(description = "意向：quote报价/sample样品/selection选型/other其他", example = "quote")
    private String intent;

    /** 意向置信度 */
    @Schema(description = "意向置信度", example = "0.82")
    private BigDecimal confidence;

    /** 判定依据片段 */
    @Schema(description = "判定依据片段")
    private String evidence;

    /** 缺失字段：scene/qty/budget/lead_time/model 中未收集到的 */
    @Schema(description = "缺失字段：scene场景/qty数量/budget预算/lead_time交期/model型号 中未收集到的", example = "[\"qty\",\"budget\"]")
    private List<String> missingFields;

    /** 推荐回复话术（按意向生成） */
    @Schema(description = "推荐回复话术（按意向生成）")
    private String recommendedReply;

    /** 下一步动作清单 */
    @Schema(description = "下一步动作清单")
    private List<String> nextSteps;

    /** 转人工时间 */
    @Schema(description = "转人工时间", example = "2026-08-03 14:30:00")
    private LocalDateTime transferredAt;
}
