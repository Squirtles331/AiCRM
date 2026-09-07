package com.aicrm.web.sales;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.FieldPermissionService;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.sales.application.SalesReadService;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.web.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "CRM V1 - 线索管理")
public class LeadV1Controller {
    private final SalesReadService readService;
    private final SalesCommandService commandService;
    private final FieldPermissionService fieldPermissionService;

    public LeadV1Controller(SalesReadService readService, SalesCommandService commandService,
                            FieldPermissionService fieldPermissionService) {
        this.readService = readService;
        this.commandService = commandService;
        this.fieldPermissionService = fieldPermissionService;
    }

    @GetMapping("/private")
    @Operation(summary = "查询线索私海")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.LeadView>> privateLeads(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        return success(page(readService.privateLeads(actor(), page, size)));
    }

    @GetMapping("/public")
    @Operation(summary = "查询线索公海")
    public ApiResponse<SalesApiDtos.PageView<SalesApiDtos.LeadView>> publicLeads(
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) {
        return success(page(readService.publicLeads(actor(), page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询线索详情")
    public ApiResponse<SalesApiDtos.LeadView> detail(@PathVariable long id) {
        return success(view(readService.lead(actor(), id), true));
    }

    @PostMapping
    @Operation(summary = "创建线索")
    public ResponseEntity<ApiResponse<SalesApiDtos.LeadView>> create(@Valid @RequestBody SalesApiDtos.CreateLeadRequest request,
                                                                       @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Lead lead = commandService.createLead(actor(), new SalesCommands.CreateLead(request.name(), request.mobile(),
                request.email(), request.companyName(), request.sourceType(), request.sourceRef(), request.intent(), request.publicPoolId()),
                idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(lead, true)));
    }

    @PostMapping("/{id}/actions/claim")
    @Operation(summary = "认领公海线索")
    public ApiResponse<SalesApiDtos.LeadView> claim(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.claimLead(actor(), id, ownership(id, request)), true));
    }

    @PostMapping("/{id}/actions/release")
    @Operation(summary = "释放线索至公海")
    public ApiResponse<SalesApiDtos.LeadView> release(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.releaseLead(actor(), id, ownership(id, request)), true));
    }

    @PostMapping("/{id}/actions/assign")
    @Operation(summary = "从公海分配线索")
    public ApiResponse<SalesApiDtos.LeadView> assign(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.transferLead(actor(), id, ownership(id, request), true), true));
    }

    @PostMapping("/{id}/actions/transfer")
    @Operation(summary = "转移私海线索")
    public ApiResponse<SalesApiDtos.LeadView> transfer(@PathVariable long id, @Valid @RequestBody SalesApiDtos.OwnershipRequest request) {
        return success(view(commandService.transferLead(actor(), id, ownership(id, request), false), true));
    }

    @PostMapping("/{id}/actions/invalidate")
    @Operation(summary = "将线索标记为无效")
    public ApiResponse<SalesApiDtos.LeadView> invalidate(@PathVariable long id, @Valid @RequestBody SalesApiDtos.InvalidateRequest request) {
        return success(view(commandService.invalidateLead(actor(), id, request.version(), request.reason()), true));
    }

    @PostMapping("/{id}/actions/convert")
    @Operation(summary = "将线索转换为客户")
    public ApiResponse<SalesApiDtos.CustomerView> convert(@PathVariable long id, @Valid @RequestBody SalesApiDtos.ConvertRequest request,
                                                           @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return success(CustomerV1Controller.view(commandService.convertLead(actor(), new SalesCommands.ConvertLead(id,
                request.version(), request.existingCustomerId(), request.customerName(), request.industry(), request.region()), idempotencyKey)));
    }

    @PostMapping("/{id}/follow-ups")
    @Operation(summary = "新增线索跟进记录")
    public ApiResponse<SalesApiDtos.LeadView> followUp(@PathVariable long id, @Valid @RequestBody SalesApiDtos.FollowUpRequest request) {
        return success(view(commandService.addLeadFollowUp(actor(), id, new SalesCommands.AddFollowUp(request.version(),
                request.channel(), request.content(), request.nextFollowUpAt())), true));
    }

    private SalesCommands.OwnershipChange ownership(long id, SalesApiDtos.OwnershipRequest request) {
        return new SalesCommands.OwnershipChange(id, request.version(), request.targetUserId(), request.publicPoolId(), request.reason());
    }

    private SalesApiDtos.PageView<SalesApiDtos.LeadView> page(PageResult<Lead> source) {
        List<SalesApiDtos.LeadView> items = source.records().stream().map(lead -> view(lead, false)).toList();
        return new SalesApiDtos.PageView<>(items, source.page(), source.size(), source.total());
    }

    static SalesApiDtos.LeadView view(Lead lead) {
        return new SalesApiDtos.LeadView(id(lead.id()), lead.leadNo(), lead.name(), lead.mobile(), lead.email(), lead.companyName(),
                lead.sourceType(), lead.sourceRef(), lead.intent(), lead.status().name(), lead.ownershipType().name(),
                id(lead.ownerUserId()), id(lead.ownerDeptId()), id(lead.publicPoolId()), id(lead.customerId()), lead.poolEnteredAt(),
                lead.lastFollowUpAt(), lead.nextFollowUpAt(), lead.version(), lead.createdAt(), lead.updatedAt());
    }

    private SalesApiDtos.LeadView view(Lead lead, boolean detail) {
        SalesApiDtos.LeadView source = view(lead);
        boolean mobileVisible = detail && fieldPermissionService.canView(actor(), "LEAD", "mobile");
        boolean emailVisible = detail && fieldPermissionService.canView(actor(), "LEAD", "email");
        return new SalesApiDtos.LeadView(source.id(), source.leadNo(), source.name(),
                mobileVisible ? source.mobile() : SensitiveFieldMasker.mobile(source.mobile()),
                emailVisible ? source.email() : SensitiveFieldMasker.email(source.email()), source.companyName(),
                source.sourceType(), source.sourceRef(), source.intent(), source.status(), source.ownershipType(),
                source.ownerUserId(), source.ownerDeptId(), source.publicPoolId(), source.customerId(),
                source.poolEnteredAt(), source.lastFollowUpAt(), source.nextFollowUpAt(), source.version(),
                source.createdAt(), source.updatedAt());
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
