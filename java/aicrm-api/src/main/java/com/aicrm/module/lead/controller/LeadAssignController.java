package com.aicrm.module.lead.controller;

import com.aicrm.common.PageResult;
import com.aicrm.common.Result;
import com.aicrm.common.auth.RequirePermission;
import com.aicrm.module.lead.entity.LeadAssignRule;
import com.aicrm.module.lead.service.LeadAssignService;
import com.aicrm.module.log.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 线索分配引擎接口（3.2.4）：分配规则管理、手动分配、超时回收
 */
@Tag(name = "线索分配")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeadAssignController {

    private final LeadAssignService leadAssignService;

    @Operation(summary = "手动分配单条线索",
            description = "权限：lead:assign。按当前租户已启用的分配规则（product/region/round_robin）将线索自动分配给匹配销售；无匹配规则时保持未分配。")
    @ApiResponse(responseCode = "1201", description = "线索不存在")
    @OperLog(module = "线索管理", operation = "分配线索")
    @RequirePermission(perms = "lead:assign")
    @PostMapping("/leads/{id}/assign")
    public Result<Void> assign(@Parameter(description = "线索 ID", required = true) @PathVariable Long id) {
        leadAssignService.assignLead(id);
        return Result.ok();
    }

    @Operation(summary = "回收超时线索并重新分配",
            description = "权限：lead:assign。扫描超过响应 SLA（sla_deadline）仍未处理的线索，回收后按分配规则重新分配；返回本次回收并重新分配的线索数。")
    @OperLog(module = "线索管理", operation = "线索回收重分配")
    @RequirePermission(perms = "lead:assign")
    @PostMapping("/leads/reassign")
    public Result<Integer> reassign() {
        return Result.ok(leadAssignService.reassignExpiredLeads());
    }

    @Operation(summary = "分配规则分页查询",
            description = "权限：lead:assign。分页查询当前租户的线索分配规则。")
    @RequirePermission(perms = "lead:assign")
    @GetMapping("/lead-assign-rules")
    public Result<PageResult<LeadAssignRule>> pageRules(@Parameter(description = "页码，默认 1") @RequestParam(defaultValue = "1") long page,
                                                        @Parameter(description = "每页条数，默认 20") @RequestParam(defaultValue = "20") long size) {
        return Result.ok(leadAssignService.pageRules(page, size));
    }

    @Operation(summary = "创建分配规则",
            description = "权限：lead:assign。新建线索分配规则；rule_type 取值：product按产品线/region按地域/round_robin轮询组。")
    @ApiResponse(responseCode = "400", description = "规则名称/规则类型/轮询组/匹配值/指定销售不能为空")
    @OperLog(module = "线索管理", operation = "创建分配规则")
    @RequirePermission(perms = "lead:assign")
    @PostMapping("/lead-assign-rules")
    public Result<LeadAssignRule> createRule(@Parameter(description = "分配规则信息", required = true) @RequestBody LeadAssignRule rule) {
        return Result.ok(leadAssignService.createRule(rule));
    }

    @Operation(summary = "更新分配规则",
            description = "权限：lead:assign。按 ID 更新分配规则；rule_type 取值：product按产品线/region按地域/round_robin轮询组。")
    @ApiResponse(responseCode = "404", description = "分配规则不存在")
    @OperLog(module = "线索管理", operation = "更新分配规则")
    @RequirePermission(perms = "lead:assign")
    @PutMapping("/lead-assign-rules/{id}")
    public Result<LeadAssignRule> updateRule(@Parameter(description = "分配规则 ID", required = true) @PathVariable Long id,
                                             @Parameter(description = "分配规则信息", required = true) @RequestBody LeadAssignRule rule) {
        rule.setId(id);
        return Result.ok(leadAssignService.updateRule(rule));
    }

    @Operation(summary = "删除分配规则",
            description = "权限：lead:assign。按 ID 删除分配规则。")
    @ApiResponse(responseCode = "404", description = "分配规则不存在")
    @OperLog(module = "线索管理", operation = "删除分配规则")
    @RequirePermission(perms = "lead:assign")
    @DeleteMapping("/lead-assign-rules/{id}")
    public Result<Void> deleteRule(@Parameter(description = "分配规则 ID", required = true) @PathVariable Long id) {
        leadAssignService.deleteRule(id);
        return Result.ok();
    }

    @Operation(summary = "分配规则启停",
            description = "权限：lead:assign。启停分配规则；status 取值：1启用/0停用。")
    @ApiResponse(responseCode = "404", description = "分配规则不存在")
    @OperLog(module = "线索管理", operation = "分配规则启停")
    @RequirePermission(perms = "lead:assign")
    @PutMapping("/lead-assign-rules/{id}/status")
    public Result<Void> updateRuleStatus(@Parameter(description = "分配规则 ID", required = true) @PathVariable Long id,
                                         @Parameter(description = "状态：1启用/0停用", required = true) @RequestParam Integer status) {
        leadAssignService.updateRuleStatus(id, status);
        return Result.ok();
    }
}
