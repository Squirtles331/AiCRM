package com.aicrm.web.trade.order;

import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.trade.order.application.SalesOrderCommandService;
import com.aicrm.trade.order.application.SalesOrderCommands;
import com.aicrm.trade.order.application.SalesOrderReadService;
import com.aicrm.trade.order.domain.SalesOrder;
import com.aicrm.trade.order.domain.SalesOrderLine;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Validated @RestController @RequestMapping("/api/v1/orders") @Tag(name = "CRM V1 - 销售订单")
public class SalesOrderV1Controller {
    private final SalesOrderCommandService commands; private final SalesOrderReadService reads;
    public SalesOrderV1Controller(SalesOrderCommandService commands, SalesOrderReadService reads) { this.commands = commands; this.reads = reads; }
    @PostMapping @Operation(summary = "从已签合同创建销售订单草稿")
    public ResponseEntity<ApiResponse<SalesOrderApiDtos.OrderView>> create(@Valid @RequestBody SalesOrderApiDtos.CreateRequest request, @RequestHeader("Idempotency-Key") String key) { return ResponseEntity.status(HttpStatus.CREATED).body(success(view(commands.create(actor(), new SalesOrderCommands.Create(request.contractId(), request.expectedDeliveryAt()), key)))); }
    @GetMapping @Operation(summary = "分页查询销售订单") public ApiResponse<SalesOrderApiDtos.PageView<SalesOrderApiDtos.OrderView>> page(@RequestParam(defaultValue = "1") @Min(1) long page, @RequestParam(defaultValue = "20") @Min(1) @Max(200) long size) { var result = reads.page(actor(), page, size); return success(new SalesOrderApiDtos.PageView<>(result.records().stream().map(SalesOrderV1Controller::view).toList(), result.page(), result.size(), result.total())); }
    @PostMapping("/{id}/actions/confirm") @Operation(summary = "确认销售订单")
    public ApiResponse<SalesOrderApiDtos.OrderView> confirm(@PathVariable long id, @Valid @RequestBody SalesOrderApiDtos.VersionRequest request) { return success(view(commands.confirm(actor(), id, new SalesOrderCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/actions/request-cancel") @Operation(summary = "申请取消销售订单")
    public ResponseEntity<ApiResponse<SalesOrderApiDtos.CancellationView>> requestCancel(@PathVariable long id, @Valid @RequestBody SalesOrderApiDtos.ReasonRequest request, @RequestHeader("Idempotency-Key") String key) { return ResponseEntity.status(HttpStatus.CREATED).body(success(cancellationView(commands.requestCancel(actor(), id, new SalesOrderCommands.Reasoned(request.version(), request.reason()), key)))); }
    @PostMapping("/{id}/cancellations/{cancellationId}/actions/approve") @Operation(summary = "批准订单取消")
    public ApiResponse<SalesOrderApiDtos.OrderView> approveCancel(@PathVariable long id, @PathVariable long cancellationId, @Valid @RequestBody SalesOrderApiDtos.CancelDecisionRequest request) { return success(view(commands.approveCancel(actor(), id, cancellationId, new SalesOrderCommands.CancelDecision(request.orderVersion(), request.cancellationVersion(), request.reason())))); }
    @PostMapping("/{id}/cancellations/{cancellationId}/actions/reject") @Operation(summary = "驳回订单取消")
    public ApiResponse<SalesOrderApiDtos.OrderView> rejectCancel(@PathVariable long id, @PathVariable long cancellationId, @Valid @RequestBody SalesOrderApiDtos.CancelDecisionRequest request) { return success(view(commands.rejectCancel(actor(), id, cancellationId, new SalesOrderCommands.CancelDecision(request.orderVersion(), request.cancellationVersion(), request.reason())))); }
    @PostMapping("/{id}/actions/close") @Operation(summary = "关闭销售订单")
    public ApiResponse<SalesOrderApiDtos.OrderView> close(@PathVariable long id, @Valid @RequestBody SalesOrderApiDtos.ReasonRequest request) { return success(view(commands.close(actor(), id, new SalesOrderCommands.Reasoned(request.version(), request.reason())))); }
    @GetMapping("/{id}") @Operation(summary = "查询销售订单") public ApiResponse<SalesOrderApiDtos.OrderView> detail(@PathVariable long id) { return success(view(reads.order(actor(), id))); }
    @GetMapping("/{id}/lines") @Operation(summary = "查询销售订单快照明细") public ApiResponse<List<SalesOrderApiDtos.LineView>> lines(@PathVariable long id) { return success(reads.lines(actor(), id).stream().map(SalesOrderV1Controller::view).toList()); }
    @GetMapping("/{id}/cancellations") @Operation(summary = "查询订单取消申请") public ApiResponse<List<SalesOrderApiDtos.CancellationView>> cancellations(@PathVariable long id) { return success(reads.cancellations(actor(), id).stream().map(SalesOrderV1Controller::cancellationView).toList()); }
    private static SalesOrderApiDtos.OrderView view(SalesOrder o) { return new SalesOrderApiDtos.OrderView(id(o.id()), o.orderNo(), id(o.contractId()), id(o.customerId()), o.currency(), o.totalAmount(), o.status().name(), o.expectedDeliveryAt(), o.confirmedAt(), o.cancelledAt(), o.closedAt(), o.closeReason(), o.version()); }
    private static SalesOrderApiDtos.CancellationView cancellationView(com.aicrm.trade.order.domain.OrderCancel c) { return new SalesOrderApiDtos.CancellationView(id(c.id()), c.cancelNo(), id(c.orderId()), c.status().name(), c.reason(), c.requestSnapshot(), c.rejectionReason(), c.requestedAt(), c.completedAt(), c.version()); }
    private static SalesOrderApiDtos.LineView view(SalesOrderLine l) { return new SalesOrderApiDtos.LineView(id(l.id()), l.lineNo(), id(l.contractLineId()), id(l.productId()), l.productNo(), l.sku(), l.productName(), l.unit(), l.quantity(), l.unitPrice(), l.taxRate(), l.lineAmount()); }
    private static String id(long id) { return String.valueOf(id); } private Actor actor() { return ActorContext.require(); } private <T> ApiResponse<T> success(T value) { return ApiResponse.success(value, TraceContext.get()); }
}
