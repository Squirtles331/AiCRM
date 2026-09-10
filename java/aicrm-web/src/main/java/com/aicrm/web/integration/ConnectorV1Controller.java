package com.aicrm.web.integration;

import com.aicrm.integration.application.ConnectorCommands;
import com.aicrm.integration.application.ConnectorService;
import com.aicrm.integration.domain.Connector;
import com.aicrm.integration.domain.ConnectorEventReceipt;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.web.api.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/connectors")
@Tag(name = "CRM V1 - 组织与营销连接器")
public class ConnectorV1Controller {
    private final ConnectorService connectors;

    public ConnectorV1Controller(ConnectorService connectors) {
        this.connectors = connectors;
    }

    @PostMapping
    @Operation(summary = "创建 CRM 入站连接器")
    public ResponseEntity<ApiResponse<ConnectorApiDtos.View>> create(
            @Valid @RequestBody ConnectorApiDtos.CreateRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Connector connector = connectors.create(ActorContext.require(), new ConnectorCommands.Create(request.name(), request.type(),
                request.operatorUserId(), request.publicPoolId(), request.sharedSecret()), idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(ConnectorApiDtos.view(connector)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询连接器配置")
    public ApiResponse<ConnectorApiDtos.View> detail(@PathVariable long id) {
        return success(ConnectorApiDtos.view(connectors.get(ActorContext.require(), id)));
    }

    @PostMapping("/{id}/actions/activate")
    @Operation(summary = "启用连接器")
    public ApiResponse<ConnectorApiDtos.View> activate(@PathVariable long id,
                                                        @Valid @RequestBody ConnectorApiDtos.VersionRequest request) {
        return success(ConnectorApiDtos.view(connectors.activate(ActorContext.require(), id,
                new ConnectorCommands.StatusChange(request.version()))));
    }

    @PostMapping("/{id}/actions/disable")
    @Operation(summary = "停用连接器")
    public ApiResponse<ConnectorApiDtos.View> disable(@PathVariable long id,
                                                       @Valid @RequestBody ConnectorApiDtos.VersionRequest request) {
        return success(ConnectorApiDtos.view(connectors.disable(ActorContext.require(), id,
                new ConnectorCommands.StatusChange(request.version()))));
    }

    @PostMapping("/{id}/events")
    @Operation(summary = "接收已认证的组织或营销 Webhook 事件")
    public ResponseEntity<ApiResponse<ConnectorApiDtos.EventReceiptView>> receive(@PathVariable long id,
            @RequestHeader("X-Connector-Secret") String sharedSecret, @RequestBody JsonNode payload) {
        ConnectorEventReceipt receipt = connectors.receive(id, sharedSecret, payload);
        HttpStatus status = receipt.replayed() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(success(ConnectorApiDtos.view(receipt)));
    }

    private <T> ApiResponse<T> success(T value) {
        return ApiResponse.success(value, TraceContext.get());
    }
}
