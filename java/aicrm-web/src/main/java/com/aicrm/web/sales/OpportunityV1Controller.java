package com.aicrm.web.sales;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.sales.application.OpportunityCommandService;
import com.aicrm.sales.application.OpportunityCommands;
import com.aicrm.sales.application.OpportunityReadService;
import com.aicrm.sales.domain.opportunity.Opportunity;
import com.aicrm.sales.domain.opportunity.OpportunityStageHistory;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/opportunities")
@Tag(name = "CRM V1 - 商机管理")
public class OpportunityV1Controller {
    private final OpportunityReadService reads;
    private final OpportunityCommandService commands;

    public OpportunityV1Controller(OpportunityReadService reads, OpportunityCommandService commands) {
        this.reads = reads;
        this.commands = commands;
    }

    @GetMapping
    @Operation(summary = "查询商机")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.OpportunityView>> opportunities(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        PageResult<Opportunity> result = reads.opportunities(actor(), page, size);
        return success(new SalesApiDtos.PageView<>(result.records().stream().map(OpportunityV1Controller::view).toList(),
                result.page(), result.size(), result.total()));
    }

    @PostMapping
    @Operation(summary = "创建商机")
    public ResponseEntity<ApiResponse<SalesApiDtos.OpportunityView>> create(
            @Valid @RequestBody SalesApiDtos.CreateOpportunityRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Opportunity created = commands.create(actor(), new OpportunityCommands.Create(request.customerId(), request.contactId(),
                request.sourceLeadId(), request.name(), request.expectedAmount(), request.currency(), request.probability(),
                request.expectedCloseDate()), idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(created)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商机详情")
    public ApiResponse<SalesApiDtos.OpportunityView> detail(@PathVariable long id) {
        return success(view(reads.opportunity(actor(), id)));
    }

    @GetMapping("/{id}/stage-history")
    @Operation(summary = "查询商机阶段历史")
    public ApiResponse<List<SalesApiDtos.OpportunityStageHistoryView>> histories(@PathVariable long id) {
        return success(reads.histories(actor(), id).stream().map(OpportunityV1Controller::view).toList());
    }

    @PostMapping("/{id}/actions/stage")
    @Operation(summary = "推进商机阶段")
    public ApiResponse<SalesApiDtos.OpportunityView> changeStage(@PathVariable long id,
            @Valid @RequestBody SalesApiDtos.ChangeOpportunityStageRequest request) {
        return success(view(commands.changeStage(actor(), id, new OpportunityCommands.ChangeStage(request.version(), request.stage(),
                request.probability()))));
    }

    @PostMapping("/{id}/actions/win")
    @Operation(summary = "商机赢单")
    public ApiResponse<SalesApiDtos.OpportunityView> win(@PathVariable long id,
            @Valid @RequestBody SalesApiDtos.OpportunityVersionRequest request) {
        return success(view(commands.win(actor(), id, request.version())));
    }

    @PostMapping("/{id}/actions/lose")
    @Operation(summary = "商机输单")
    public ApiResponse<SalesApiDtos.OpportunityView> lose(@PathVariable long id,
            @Valid @RequestBody SalesApiDtos.LoseOpportunityRequest request) {
        return success(view(commands.lose(actor(), id, request.version(), request.reason())));
    }

    @PostMapping("/{id}/actions/restart")
    @Operation(summary = "重启输单商机")
    public ApiResponse<SalesApiDtos.OpportunityView> restart(@PathVariable long id,
            @Valid @RequestBody SalesApiDtos.OpportunityVersionRequest request) {
        return success(view(commands.restart(actor(), id, request.version())));
    }

    private static SalesApiDtos.OpportunityView view(Opportunity value) {
        return new SalesApiDtos.OpportunityView(LeadV1Controller.id(value.id()), value.opportunityNo(), value.name(),
                LeadV1Controller.id(value.customerId()), LeadV1Controller.id(value.contactId()), LeadV1Controller.id(value.sourceLeadId()),
                value.stage().name(), value.status().name(), value.expectedAmount(), value.currency(), value.probability(),
                value.expectedCloseDate(), LeadV1Controller.id(value.ownerUserId()), LeadV1Controller.id(value.ownerDeptId()),
                value.lostReason(), value.lostAt(), value.wonAt(), value.version(), value.createdAt(), value.updatedAt());
    }

    private static SalesApiDtos.OpportunityStageHistoryView view(OpportunityStageHistory value) {
        return new SalesApiDtos.OpportunityStageHistoryView(LeadV1Controller.id(value.id()), LeadV1Controller.id(value.opportunityId()),
                value.action().name(), value.fromStage() == null ? null : value.fromStage().name(), value.toStage().name(),
                value.fromStatus() == null ? null : value.fromStatus().name(), value.toStatus().name(), value.fromProbability(),
                value.toProbability(), value.reason(), LeadV1Controller.id(value.operatorUserId()), value.createdAt());
    }

    private Actor actor() { return ActorContext.require(); }
    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
