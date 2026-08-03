package com.aicrm.module.dashboard.service;

import com.aicrm.module.dashboard.dto.ChannelConversionStat;
import com.aicrm.module.dashboard.dto.DashboardOverview;
import com.aicrm.module.dashboard.dto.DashboardTrend;
import com.aicrm.module.dashboard.dto.SalesWorkloadStat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础看板服务
 */
public interface DashboardService {

    /**
     * 实时统计看板总览
     *
     * @param tenantId 租户 ID
     * @param from     统计起始时间（可空，默认近 7 天）
     * @param to       统计截止时间（可空，默认当前）
     * @return 看板总览
     */
    DashboardOverview getOverview(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * 每日趋势：线索量/会话量/AI 解决量按日聚合（缺失日期补 0）
     */
    List<DashboardTrend> getTrend(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * 渠道转化数据：按渠道账号聚合事件数、线索数、转化率
     */
    List<ChannelConversionStat> getChannelConversion(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * 销售工作量统计：按销售聚合分配线索、跟进、成交、人工会话量
     */
    List<SalesWorkloadStat> getSalesWorkload(Long tenantId, LocalDateTime from, LocalDateTime to);
}
