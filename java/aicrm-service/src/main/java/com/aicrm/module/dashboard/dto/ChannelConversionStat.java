package com.aicrm.module.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 看板渠道转化数据（3.4.5）：按渠道账号聚合事件数、线索数、转化率
 */
@Data
@Schema(description = "看板渠道转化数据：按渠道账号聚合事件数/线索数/转化率")
public class ChannelConversionStat {

    /** 渠道账号 ID */
    @Schema(description = "渠道账号 ID")
    private Long channelAccountId;

    /** 渠道账号名称 */
    @Schema(description = "渠道账号名称")
    private String accountName;

    /** 渠道事件数 */
    @Schema(description = "渠道事件数")
    private Long eventCount;

    /** 映射线索数 */
    @Schema(description = "映射线索数")
    private Long leadCount;

    /** 转化率（线索数 / 事件数，0-1） */
    @Schema(description = "转化率（线索数/事件数，0-1）", example = "0.35")
    private Double conversionRate;
}
