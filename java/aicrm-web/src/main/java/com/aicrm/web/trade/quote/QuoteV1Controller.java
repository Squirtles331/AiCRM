package com.aicrm.web.trade.quote;

import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.trade.quote.application.QuoteCommandService;
import com.aicrm.trade.quote.application.QuoteCommands;
import com.aicrm.trade.quote.application.QuoteReadService;
import com.aicrm.trade.quote.domain.Quote;
import com.aicrm.trade.quote.domain.QuoteLine;
import com.aicrm.trade.quote.domain.QuoteVersion;
import com.aicrm.web.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/quotes")
@Tag(name = "CRM V1 - 报价管理")
public class QuoteV1Controller {
    private final QuoteCommandService commands;
    private final QuoteReadService reads;

    public QuoteV1Controller(QuoteCommandService commands, QuoteReadService reads) { this.commands = commands; this.reads = reads; }

    @PostMapping
    @Operation(summary = "创建报价")
    public ResponseEntity<ApiResponse<QuoteApiDtos.QuoteView>> create(@Valid @RequestBody QuoteApiDtos.CreateRequest request,
            @RequestHeader("Idempotency-Key") String key) {
        QuoteCommands.Create command = new QuoteCommands.Create(request.opportunityId(), request.priceListId(), request.validUntil(),
                request.lines().stream().map(line -> new QuoteCommands.Line(line.productId(), line.priceItemId(), line.quantity(), line.unitPrice(), line.discountRate())).toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(commands.create(actor(), command, key))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询报价")
    public ApiResponse<QuoteApiDtos.QuoteView> detail(@PathVariable long id) { return success(view(reads.quote(actor(), id))); }

    @GetMapping("/{id}/versions")
    @Operation(summary = "查询报价版本")
    public ApiResponse<List<QuoteApiDtos.VersionView>> versions(@PathVariable long id) {
        return success(reads.versions(actor(), id).stream().map(QuoteV1Controller::view).toList());
    }

    @GetMapping("/{id}/versions/{versionNo}/lines")
    @Operation(summary = "查询报价版本明细")
    public ApiResponse<List<QuoteApiDtos.LineView>> lines(@PathVariable long id, @PathVariable @Min(1) int versionNo) {
        return success(reads.lines(actor(), id, versionNo).stream().map(QuoteV1Controller::view).toList());
    }

    @PostMapping("/{id}/actions/submit")
    @Operation(summary = "提交报价")
    public ApiResponse<QuoteApiDtos.QuoteView> submit(@PathVariable long id, @Valid @RequestBody QuoteApiDtos.VersionRequest request) {
        return success(view(commands.submit(actor(), id, request.rootVersion(), request.version())));
    }

    @PostMapping("/{id}/actions/reject")
    @Operation(summary = "拒绝报价")
    public ApiResponse<QuoteApiDtos.QuoteView> reject(@PathVariable long id, @Valid @RequestBody QuoteApiDtos.RejectRequest request) {
        return success(view(commands.reject(actor(), id, request.rootVersion(), request.version(), request.reason())));
    }

    @PostMapping("/{id}/actions/expire")
    @Operation(summary = "标记报价过期")
    public ApiResponse<QuoteApiDtos.QuoteView> expire(@PathVariable long id, @Valid @RequestBody QuoteApiDtos.VersionRequest request) {
        return success(view(commands.expire(actor(), id, request.rootVersion(), request.version())));
    }

    private static QuoteApiDtos.QuoteView view(Quote q) { return new QuoteApiDtos.QuoteView(id(q.id()), q.quoteNo(), id(q.opportunityId()), id(q.customerId()), id(q.priceListId()), q.currency(), q.status().name(), q.currentVersionNo(), q.validUntil(), q.version(), q.createdAt(), q.updatedAt()); }
    private static QuoteApiDtos.VersionView view(QuoteVersion v) { return new QuoteApiDtos.VersionView(id(v.id()), id(v.quoteId()), v.versionNo(), v.status().name(), v.subtotal(), v.discountAmount(), v.taxAmount(), v.totalAmount(), v.discountRate(), v.rejectionReason(), v.submittedAt(), v.rejectedAt(), v.expiredAt(), v.version()); }
    private static QuoteApiDtos.LineView view(QuoteLine l) { return new QuoteApiDtos.LineView(id(l.id()), id(l.quoteVersionId()), l.lineNo(), id(l.productId()), id(l.priceItemId()), l.productNo(), l.sku(), l.productName(), l.unit(), l.quantity(), l.listPrice(), l.minimumPrice(), l.unitPrice(), l.discountRate(), l.taxRate(), l.lineAmount()); }
    private static String id(long value) { return String.valueOf(value); }
    private Actor actor() { return ActorContext.require(); }
    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
