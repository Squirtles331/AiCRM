package com.aicrm.module.lead.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.module.lead.entity.Lead;
import com.aicrm.module.lead.service.LeadService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 线索管理接口
 */
@Tag(name = "线索管理")
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @Operation(summary = "分页查询线索",
            description = "权限：lead:list。按租户/状态/意向维度分页查询线索；status 取值：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失；intent 取值：quote报价/sample样品/selection选型/other其他。")
    @GetMapping
    public Result<PageResult<Lead>> page(
            @Parameter(description = "租户 ID（平台管理员可指定，为空默认当前租户）") @RequestParam(required = false) Long tenantId,
            @Parameter(description = "线索状态：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失") @RequestParam(required = false) String status,
            @Parameter(description = "客户意向：quote报价/sample样品/selection选型/other其他") @RequestParam(required = false) String intent,
            @Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(leadService.pageLeads(tenantId, status, intent, page, size));
    }

    @Operation(summary = "查询线索详情",
            description = "权限：lead:list。按 ID 查询线索详情，含抽取关键字段（extra）。")
    @GetMapping("/{id}")
    public Result<Lead> detail(@Parameter(description = "线索 ID", required = true) @PathVariable Long id) {
        return Result.ok(leadService.getById(id));
    }

    @Operation(summary = "创建线索",
            description = "权限：lead:list。创建线索（渠道接入/坐席手动录入）；intent 取值：quote报价/sample样品/selection选型/other其他，status 默认 new新线索。")
    @ApiResponse(responseCode = "400", description = "tenantId 不能为空")
    @OperLog(module = "线索管理", operation = "创建线索")
    @PostMapping
    public Result<Lead> create(@Parameter(description = "线索信息", required = true) @Valid @RequestBody Lead lead) {
        return Result.ok(leadService.createLead(lead));
    }
}
