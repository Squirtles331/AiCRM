package com.aicrm.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 看板销售工作量统计（3.4.5）：按销售聚合分配线索、跟进、成交、会话量
 */
@Data
@Schema(description = "看板销售工作量统计：按销售聚合分配线索/跟进/成交/会话量")
public class SalesWorkloadStat {

    /** 销售用户 ID */
    @Schema(description = "销售用户 ID")
    private Long userId;

    /** 销售姓名 */
    @Schema(description = "销售姓名")
    private String userName;

    /** 分配线索数 */
    @Schema(description = "分配线索数")
    private Long assignedLeadCount;

    /** 跟进记录数 */
    @Schema(description = "跟进记录数")
    private Long followUpCount;

    /** 成交线索数（status=won） */
    @Schema(description = "成交线索数（status=won）")
    private Long wonCount;

    /** 人工接待会话数 */
    @Schema(description = "人工接待会话数")
    private Long conversationCount;
}
