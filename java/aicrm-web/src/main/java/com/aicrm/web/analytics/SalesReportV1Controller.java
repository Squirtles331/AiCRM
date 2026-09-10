package com.aicrm.web.analytics;

import com.aicrm.analytics.report.application.SalesReportQueryService;
import com.aicrm.analytics.report.domain.SalesFunnelSnapshot;
import com.aicrm.analytics.report.domain.SalesPerformanceReport;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "CRM V1 - 销售报表")
public class SalesReportV1Controller {
    private final SalesReportQueryService queries;

    public SalesReportV1Controller(SalesReportQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/sales-funnel")
    @Operation(summary = "查询当前 CRM 销售漏斗")
    public ApiResponse<SalesFunnelSnapshot> funnel() {
        return ApiResponse.success(queries.funnel(ActorContext.require()), TraceContext.get());
    }

    @GetMapping("/sales-performance")
    @Operation(summary = "查询区间 CRM 销售业绩")
    public ApiResponse<SalesPerformanceReport> performance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(queries.performance(ActorContext.require(), from, to), TraceContext.get());
    }
}
