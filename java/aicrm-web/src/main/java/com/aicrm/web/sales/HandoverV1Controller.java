package com.aicrm.web.sales;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.BatchHandoverResult;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.web.api.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/handovers")
@Tag(name = "CRM V1 - 资源交接")
public class HandoverV1Controller {
    private final SalesCommandService commandService;

    public HandoverV1Controller(SalesCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    @Operation(summary = "交接指定线索或客户")
    public ApiResponse<?> handover(@Valid @RequestBody SalesApiDtos.HandoverRequest request,
                                    @RequestHeader("Idempotency-Key") String idempotencyKey) {
        SalesCommands.Handover command = new SalesCommands.Handover(request.resourceId(), request.version(),
                request.fromUserId(), request.toUserId(), request.reason());
        return switch (request.resourceType().trim().toUpperCase()) {
            case "LEAD" -> ApiResponse.success(LeadV1Controller.view(commandService.handoverLead(ActorContext.require(), command, idempotencyKey)),
                    TraceContext.get());
            case "CUSTOMER" -> ApiResponse.success(CustomerV1Controller.view(commandService.handoverCustomer(ActorContext.require(), command,
                    idempotencyKey)), TraceContext.get());
            default -> throw new DomainException(ErrorCode.VALIDATION_ERROR, "resourceType 仅支持 LEAD 或 CUSTOMER");
        };
    }

    @PostMapping("/batch")
    @Operation(summary = "批量离职交接")
    public ApiResponse<BatchHandoverResult> batchHandover(@Valid @RequestBody SalesApiDtos.BatchHandoverRequest request,
                                                            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        BatchHandoverResult result = commandService.handoverAll(ActorContext.require(),
                new SalesCommands.BatchHandover(request.batchNo(), request.fromUserId(), request.toUserId(),
                        request.pageSize(), request.reason()), idempotencyKey);
        return ApiResponse.success(result, TraceContext.get());
    }
}
