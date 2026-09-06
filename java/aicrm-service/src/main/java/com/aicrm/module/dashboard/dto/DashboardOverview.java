package com.aicrm.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 看板总览（M1 实时统计）
 */
@Data
@Schema(description = "看板总览：线索量/会话量/转人工数/人工响应时效/有效对话率/人工意向分布")
public class DashboardOverview {

    /** 租户 ID */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /** 统计起始时间 */
    @Schema(description = "统计起始时间")
    private LocalDateTime from;

    /** 统计截止时间 */
    @Schema(description = "统计截止时间")
    private LocalDateTime to;

    /** 线索量 */
    @Schema(description = "线索量")
    private Long leadCount;

    /** 新增会话数 */
    @Schema(description = "新增会话数")
    private Long conversationCount;

    /** 转人工数 */
    @Schema(description = "转人工数")
    private Long transferCount;

    /** 平均响应时效（秒）：客户首条消息到人工首条回复 */
    @Schema(description = "平均响应时效（秒）：客户首条消息到人工首条回复")
    private Double avgResponseSec;

    /** 有效对话率（0-1）：≥3 条消息的会话占比 */
    @Schema(description = "有效对话率（0-1）：≥3 条消息的会话占比")
    private Double effectiveRate;

    /** 人工维护的线索意向分布 */
    @Schema(description = "人工维护的线索意向分布")
    private List<IntentCount> intentDistribution;

    /** 意向分布明细 */
    @Data
    @Schema(description = "意向分布明细")
    public static class IntentCount {
        /** 意向：quote/sample/selection/other/unknown */
        @Schema(description = "意向：quote报价/sample样品/selection选型/other其他/unknown未知", example = "quote")
        private String intent;
        /** 线索数 */
        @Schema(description = "线索数")
        private Long count;

        public IntentCount(String intent, Long count) {
            this.intent = intent;
            this.count = count;
        }
    }
}
