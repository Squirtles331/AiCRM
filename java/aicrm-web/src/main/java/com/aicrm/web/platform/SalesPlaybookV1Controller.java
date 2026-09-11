package com.aicrm.web.platform;

import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.SalesPlaybookService;
import com.aicrm.web.api.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-playbooks")
public class SalesPlaybookV1Controller {
    private final SalesPlaybookService service;

    public SalesPlaybookV1Controller(SalesPlaybookService service) {
        this.service = service;
    }

    record CreateRequest(@NotBlank String title, @NotBlank String salesStage, @NotBlank String scenario,
                         @NotBlank String content) { }
    record VersionRequest(@NotNull Long version) { }
    record View(String id, String title, String salesStage, String scenario, String content, String status,
                String ownerUserId, Instant publishedAt, Instant archivedAt, long version) { }

    @PostMapping
    public ResponseEntity<ApiResponse<View>> create(@RequestBody CreateRequest request,
                                                    @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(view(service.create(ActorContext.require(),
                new SalesPlaybookService.Create(request.title(), request.salesStage(), request.scenario(), request.content()),
                idempotencyKey))));
    }

    @GetMapping
    public ApiResponse<List<View>> list() {
        return ok(service.list(ActorContext.require()).stream().map(this::view).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<View> get(@PathVariable long id) {
        return ok(view(service.get(ActorContext.require(), id)));
    }

    @PostMapping("/{id}/actions/publish")
    public ApiResponse<View> publish(@PathVariable long id, @RequestBody VersionRequest request) {
        return ok(view(service.publish(ActorContext.require(), id, new SalesPlaybookService.Versioned(request.version()))));
    }

    @PostMapping("/{id}/actions/archive")
    public ApiResponse<View> archive(@PathVariable long id, @RequestBody VersionRequest request) {
        return ok(view(service.archive(ActorContext.require(), id, new SalesPlaybookService.Versioned(request.version()))));
    }

    private View view(SalesPlaybookService.Playbook playbook) {
        return new View(String.valueOf(playbook.id()), playbook.title(), playbook.salesStage(), playbook.scenario(),
                playbook.content(), playbook.status().name(), String.valueOf(playbook.ownerUserId()),
                playbook.publishedAt(), playbook.archivedAt(), playbook.version());
    }

    private <T> ApiResponse<T> ok(T data) {
        return ApiResponse.success(data, TraceContext.get());
    }
}
