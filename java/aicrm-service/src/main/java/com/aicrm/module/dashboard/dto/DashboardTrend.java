package com.aicrm.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 看板每日趋势（3.4.5）：线索量/会话量/AI 解决量按日聚合
 */
@Data
@Schema(description = "看板每日趋势：线索量/会话量/AI 解决量按日聚合")
public class DashboardTrend {

    /** 统计日期 */
    @Schema(description = "统计日期", example = "2026-08-03")
    private LocalDate date;

    /** 新增线索数 */
    @Schema(description = "新增线索数")
    private Long leadCount;

    /** 新增会话数 */
    @Schema(description = "新增会话数")
    private Long conversationCount;

    /** AI 解决会话数（当日会话 - 转人工会话） */
    @Schema(description = "AI 解决会话数（当日会话减去转人工会话）")
    private Long aiResolvedCount;
}
