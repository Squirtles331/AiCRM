package com.aicrm.web.sales;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.application.SalesCommands;
import com.aicrm.web.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/handovers")
public class HandoverV1Controller {
    private final SalesCommandService commandService;

    public HandoverV1Controller(SalesCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
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
}
