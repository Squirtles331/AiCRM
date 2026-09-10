package com.aicrm.web.analytics;

import com.aicrm.analytics.application.WorkbenchQueryService;
import com.aicrm.analytics.domain.WorkbenchSummary;
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
@RequestMapping("/api/v1/workbench")
@Tag(name = "CRM V1 - 工作台")
public class WorkbenchV1Controller {
    private final WorkbenchQueryService queries;

    public WorkbenchV1Controller(WorkbenchQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/summary")
    @Operation(summary = "查询 CRM 销售工作台汇总")
    public ApiResponse<WorkbenchSummary> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(queries.summary(ActorContext.require(), from, to), TraceContext.get());
    }
}
