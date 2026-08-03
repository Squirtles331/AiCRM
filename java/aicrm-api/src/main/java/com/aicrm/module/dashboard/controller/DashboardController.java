package com.aicrm.module.dashboard.controller;

import com.aicrm.common.Result;
import com.aicrm.module.dashboard.dto.ChannelConversionStat;
import com.aicrm.module.dashboard.dto.DashboardOverview;
import com.aicrm.module.dashboard.dto.DashboardTrend;
import com.aicrm.module.dashboard.dto.SalesWorkloadStat;
import com.aicrm.module.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础看板接口
 */
@Tag(name = "基础看板")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "看板总览（线索量/会话量/转人工数/响应时效/有效对话率/意向分布）", description = "按时间范围聚合看板核心指标，场景：管理后台首页数据总览")
    @GetMapping("/overview")
    public Result<DashboardOverview> overview(
            @Parameter(description = "租户 ID", required = true) @RequestParam Long tenantId,
            @Parameter(description = "开始时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @Parameter(description = "结束时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {
        return Result.ok(dashboardService.getOverview(tenantId, from, to));
    }

    @Operation(summary = "每日趋势（线索量/会话量/AI 解决量按日聚合）", description = "按日聚合线索/会话/AI 解决量趋势，场景：趋势分析与运营复盘")
    @GetMapping("/trend")
    public Result<List<DashboardTrend>> trend(
            @Parameter(description = "租户 ID", required = true) @RequestParam Long tenantId,
            @Parameter(description = "开始时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @Parameter(description = "结束时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {
        return Result.ok(dashboardService.getTrend(tenantId, from, to));
    }

    @Operation(summary = "渠道转化数据（按渠道账号聚合事件/线索/转化率）", description = "按渠道账号聚合事件/线索/转化率，场景：渠道效果分析与投放优化")
    @GetMapping("/channel-conversion")
    public Result<List<ChannelConversionStat>> channelConversion(
            @Parameter(description = "租户 ID", required = true) @RequestParam Long tenantId,
            @Parameter(description = "开始时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @Parameter(description = "结束时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {
        return Result.ok(dashboardService.getChannelConversion(tenantId, from, to));
    }

    @Operation(summary = "销售工作量统计（按销售聚合分配线索/跟进/成交/会话）", description = "按销售聚合分配线索/跟进/成交/会话，场景：销售绩效统计与工作量评估")
    @GetMapping("/sales-workload")
    public Result<List<SalesWorkloadStat>> salesWorkload(
            @Parameter(description = "租户 ID", required = true) @RequestParam Long tenantId,
            @Parameter(description = "开始时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @Parameter(description = "结束时间（yyyy-MM-dd HH:mm:ss），为空取全部", required = false) @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to) {
        return Result.ok(dashboardService.getSalesWorkload(tenantId, from, to));
    }
}
