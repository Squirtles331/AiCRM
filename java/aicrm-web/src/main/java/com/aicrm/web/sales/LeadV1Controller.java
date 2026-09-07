package com.aicrm.web.sales;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.sales.application.SalesReadService;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.web.api.ApiResponse;
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
@RequestMapping("/api/v1/leads")
public class LeadV1Controller {
    private final SalesReadService readService;
    private final SalesCommandService commandService;

    public LeadV1Controller(SalesReadService readService, SalesCommandService commandService) {
        this.readService = readService;
        this.commandService = commandService;
    }

    @GetMapping("/private")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.LeadView>> privateLeads(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        return success(page(readService.privateLeads(actor(), page, size)));
    }

    @GetMapping("/public")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.LeadView>> publicLeads(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        return success(page(readService.publicLeads(actor(), page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<SalesApiDtos.LeadView> detail(@PathVariable long id) {
        return success(view(readService.lead(actor(), id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalesApiDtos.LeadView>> create(@Valid @RequestBody SalesApiDtos.CreateLeadRequest request,
                                                                       @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Lead lead = commandService.createLead(actor(), new SalesCommands.CreateLead(request.name(), request.mobile(),
                request.email(), request.companyName(), request.sourceType(), request.sourceRef(), request.intent(), request.publicPoolId()),
                idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(lead)));
    }

    @PostMapping("/{id}/actions/claim")
    public ApiResponse<SalesApiDtos.LeadView> claim(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.claimLead(actor(), id, ownership(id, request))));
    }

    @PostMapping("/{id}/actions/release")
    public ApiResponse<SalesApiDtos.LeadView> release(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.releaseLead(actor(), id, ownership(id, request))));
    }

    @PostMapping("/{id}/actions/assign")
    public ApiResponse<SalesApiDtos.LeadView> assign(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.transferLead(actor(), id, ownership(id, request), true)));
    }

    @PostMapping("/{id}/actions/transfer")
    public ApiResponse<SalesApiDtos.LeadView> transfer(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.transferLead(actor(), id, ownership(id, request), false)));
    }

    @PostMapping("/{id}/actions/invalidate")
    public ApiResponse<SalesApiDtos.LeadView> invalidate(@PathVariable long id, @Valid @RequestBody SalesApiDtos.InvalidateRequest request) {
        return success(view(commandService.invalidateLead(actor(), id, request.version(), request.reason())));
    }

    @PostMapping("/{id}/actions/convert")
    public ApiResponse<SalesApiDtos.CustomerView> convert(@PathVariable long id, @Valid @RequestBody SalesApiDtos.ConvertRequest request,
                                                           @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return success(CustomerV1Controller.view(commandService.convertLead(actor(), new SalesCommands.ConvertLead(id,
                request.version(), request.existingCustomerId(), request.customerName(), request.industry(), request.region()), idempotencyKey)));
    }

    @PostMapping("/{id}/follow-ups")
    public ApiResponse<SalesApiDtos.LeadView> followUp(@PathVariable long id, @Valid @RequestBody SalesApiDtos.FollowUpRequest request) {
        return success(view(commandService.addLeadFollowUp(actor(), id, new SalesCommands.AddFollowUp(request.version(),
                request.channel(), request.content(), request.nextFollowUpAt()))));
    }

    private SalesCommands.OwnershipChange ownership(long id, SalesApiDtos.OwnershipRequest request) {
        return new SalesCommands.OwnershipChange(id, request.version(), request.targetUserId(), request.publicPoolId(), request.reason());
    }

    private SalesApiDtos.PageView<SalesApiDtos.LeadView> page(PageResult<Lead> source) {
        List<SalesApiDtos.LeadView> items = source.records().stream().map(LeadV1Controller::view).toList();
        return new SalesApiDtos.PageView<>(items, source.page(), source.size(), source.total());
    }

    static SalesApiDtos.LeadView view(Lead lead) {
        return new SalesApiDtos.LeadView(id(lead.id()), lead.leadNo(), lead.name(), lead.mobile(), lead.email(), lead.companyName(),
                lead.sourceType(), lead.sourceRef(), lead.intent(), lead.status().name(), lead.ownershipType().name(),
                id(lead.ownerUserId()), id(lead.ownerDeptId()), id(lead.publicPoolId()), id(lead.customerId()), lead.poolEnteredAt(),
                lead.lastFollowUpAt(), lead.nextFollowUpAt(), lead.version(), lead.createdAt(), lead.updatedAt());
    }

    static String id(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    static String id(long value) {
        return String.valueOf(value);
    }

    private Actor actor() {
        return ActorContext.require();
    }

    private <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, TraceContext.get());
    }
}
