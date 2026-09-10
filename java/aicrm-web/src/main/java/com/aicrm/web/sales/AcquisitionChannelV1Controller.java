package com.aicrm.web.sales;

import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.sales.application.AcquisitionChannelCommands;
import com.aicrm.sales.application.AcquisitionChannelService;
import com.aicrm.web.api.ApiResponse;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/acquisition-channels")
@Tag(name = "CRM V1 - 获客渠道")
public class AcquisitionChannelV1Controller {
    private final AcquisitionChannelService service;
    public AcquisitionChannelV1Controller(AcquisitionChannelService service) { this.service = service; }
    @PostMapping @Operation(summary = "创建获客渠道")
    public ResponseEntity<ApiResponse<AcquisitionChannelApiDtos.View>> create(@Valid @RequestBody AcquisitionChannelApiDtos.CreateRequest request, @RequestHeader("Idempotency-Key") String key) {
        return ResponseEntity.status(HttpStatus.CREATED).body(success(AcquisitionChannelApiDtos.view(service.create(ActorContext.require(), new AcquisitionChannelCommands.Create(request.code(), request.name(), request.sourceType()), key))));
    }
    @GetMapping("/{id}") @Operation(summary = "查询获客渠道")
    public ApiResponse<AcquisitionChannelApiDtos.View> detail(@PathVariable long id) { return success(AcquisitionChannelApiDtos.view(service.get(ActorContext.require(), id))); }
    @GetMapping @Operation(summary = "查询获客渠道列表")
    public ApiResponse<List<AcquisitionChannelApiDtos.View>> list() { return success(service.list(ActorContext.require()).stream().map(AcquisitionChannelApiDtos::view).toList()); }
    @PostMapping("/{id}/actions/activate") @Operation(summary = "启用获客渠道")
    public ApiResponse<AcquisitionChannelApiDtos.View> activate(@PathVariable long id, @Valid @RequestBody AcquisitionChannelApiDtos.VersionRequest request) { return success(AcquisitionChannelApiDtos.view(service.activate(ActorContext.require(), id, new AcquisitionChannelCommands.Versioned(request.version())))); }
    @PostMapping("/{id}/actions/disable") @Operation(summary = "停用获客渠道")
    public ApiResponse<AcquisitionChannelApiDtos.View> disable(@PathVariable long id, @Valid @RequestBody AcquisitionChannelApiDtos.VersionRequest request) { return success(AcquisitionChannelApiDtos.view(service.disable(ActorContext.require(), id, new AcquisitionChannelCommands.Versioned(request.version())))); }
    private <T> ApiResponse<T> success(T value) { return ApiResponse.success(value, TraceContext.get()); }
}
