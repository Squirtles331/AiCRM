package com.aicrm.web.trade.contract;

import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.trade.contract.application.ContractCommandService;
import com.aicrm.trade.contract.application.ContractCommands;
import com.aicrm.trade.contract.application.ContractReadService;
import com.aicrm.trade.contract.domain.Contract;
import com.aicrm.trade.contract.domain.ContractLine;
import com.aicrm.trade.contract.domain.ContractChange;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "CRM V1 - 合同管理")
public class ContractV1Controller {
    private final ContractCommandService commands;
    private final ContractReadService reads;
    public ContractV1Controller(ContractCommandService commands, ContractReadService reads) { this.commands = commands; this.reads = reads; }

    @PostMapping
    @Operation(summary = "从已批准报价创建合同草稿")
    public ResponseEntity<ApiResponse<ContractApiDtos.ContractView>> create(@Valid @RequestBody ContractApiDtos.CreateRequest request,
            @RequestHeader("Idempotency-Key") String key) {
        Contract contract = commands.create(actor(), new ContractCommands.Create(request.quoteId(), request.name(), request.effectiveFrom(), request.effectiveTo()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(contract)));
    }
    @GetMapping("/{id}") @Operation(summary = "查询合同")
    public ApiResponse<ContractApiDtos.ContractView> detail(@PathVariable long id) { return success(view(reads.contract(actor(), id))); }
    @GetMapping("/{id}/lines") @Operation(summary = "查询合同快照明细")
    public ApiResponse<List<ContractApiDtos.LineView>> lines(@PathVariable long id) { return success(reads.lines(actor(), id).stream().map(ContractV1Controller::view).toList()); }
    @PostMapping("/{id}/actions/submit-signature") @Operation(summary = "提交合同签署")
    public ApiResponse<ContractApiDtos.ContractView> submitSignature(@PathVariable long id, @Valid @RequestBody ContractApiDtos.VersionRequest request) { return success(view(commands.submitSignature(actor(), id, new ContractCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/actions/sign") @Operation(summary = "确认合同签署")
    public ApiResponse<ContractApiDtos.ContractView> sign(@PathVariable long id, @Valid @RequestBody ContractApiDtos.VersionRequest request) { return success(view(commands.sign(actor(), id, new ContractCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/actions/withdraw-signature") @Operation(summary = "撤回合同签署")
    public ApiResponse<ContractApiDtos.ContractView> withdrawSignature(@PathVariable long id, @Valid @RequestBody ContractApiDtos.VersionRequest request) { return success(view(commands.withdrawSignature(actor(), id, new ContractCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/actions/void") @Operation(summary = "作废合同")
    public ApiResponse<ContractApiDtos.ContractView> voidContract(@PathVariable long id, @Valid @RequestBody ContractApiDtos.ReasonRequest request) { return success(view(commands.voidContract(actor(), id, new ContractCommands.Reasoned(request.version(), request.reason())))); }
    @PostMapping("/{id}/changes") @Operation(summary = "创建合同变更申请")
    public ResponseEntity<ApiResponse<ContractApiDtos.ChangeView>> createChange(@PathVariable long id, @Valid @RequestBody ContractApiDtos.CreateChangeRequest request,
                                                                                  @RequestHeader("Idempotency-Key") String key) {
        ContractChange change = commands.createChange(actor(), id, new ContractCommands.CreateChange(request.contractVersion(), request.reason(), request.proposedName(), request.proposedEffectiveFrom(), request.proposedEffectiveTo()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(changeView(change)));
    }
    @GetMapping("/{id}/changes/{changeId}") @Operation(summary = "查询合同变更申请")
    public ApiResponse<ContractApiDtos.ChangeView> change(@PathVariable long id, @PathVariable long changeId) { return success(changeView(reads.change(actor(), id, changeId))); }
    @PostMapping("/{id}/changes/{changeId}/actions/submit") @Operation(summary = "提交合同变更")
    public ApiResponse<ContractApiDtos.ChangeView> submitChange(@PathVariable long id, @PathVariable long changeId, @Valid @RequestBody ContractApiDtos.VersionRequest request) { return success(changeView(commands.submitChange(actor(), id, changeId, new ContractCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/changes/{changeId}/actions/cancel") @Operation(summary = "取消合同变更")
    public ApiResponse<ContractApiDtos.ChangeView> cancelChange(@PathVariable long id, @PathVariable long changeId, @Valid @RequestBody ContractApiDtos.VersionRequest request) { return success(changeView(commands.cancelChange(actor(), id, changeId, new ContractCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/changes/{changeId}/actions/approve") @Operation(summary = "批准合同变更")
    public ApiResponse<ContractApiDtos.ChangeView> approveChange(@PathVariable long id, @PathVariable long changeId, @Valid @RequestBody ContractApiDtos.VersionRequest request) { return success(changeView(commands.approveChange(actor(), id, changeId, new ContractCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/changes/{changeId}/actions/reject") @Operation(summary = "驳回合同变更")
    public ApiResponse<ContractApiDtos.ChangeView> rejectChange(@PathVariable long id, @PathVariable long changeId, @Valid @RequestBody ContractApiDtos.RejectChangeRequest request) { return success(changeView(commands.rejectChange(actor(), id, changeId, new ContractCommands.RejectChange(request.version(), request.reason())))); }

    private static ContractApiDtos.ContractView view(Contract c) { return new ContractApiDtos.ContractView(id(c.id()), c.contractNo(), c.name(), id(c.quoteId()), id(c.quoteVersionId()), id(c.customerId()), c.currency(), c.subtotal(), c.discountAmount(), c.taxAmount(), c.totalAmount(), c.status().name(), c.effectiveFrom(), c.effectiveTo(), c.submittedAt(), c.signedAt(), c.version()); }
    private static ContractApiDtos.LineView view(ContractLine l) { return new ContractApiDtos.LineView(id(l.id()), l.lineNo(), id(l.quoteLineId()), id(l.productId()), l.productNo(), l.sku(), l.productName(), l.unit(), l.quantity(), l.listPrice(), l.unitPrice(), l.discountRate(), l.taxRate(), l.lineAmount()); }
    private static ContractApiDtos.ChangeView changeView(ContractChange c) { return new ContractApiDtos.ChangeView(id(c.id()), c.changeNo(), id(c.contractId()), c.status().name(), c.reason(), c.beforeSnapshot(), c.afterSnapshot(), c.rejectionReason(), c.version(), c.submittedAt(), c.approvedAt(), c.rejectedAt(), c.cancelledAt()); }
    private static String id(long value) { return String.valueOf(value); }
    private Actor actor() { return ActorContext.require(); }
    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
