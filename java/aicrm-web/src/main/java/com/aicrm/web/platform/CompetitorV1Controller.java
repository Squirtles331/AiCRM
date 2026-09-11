package com.aicrm.web.platform;

import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.CompetitorService;
import com.aicrm.web.api.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/competitors")
public class CompetitorV1Controller {
    private final CompetitorService service;
    public CompetitorV1Controller(CompetitorService service) { this.service = service; }
    record CreateRequest(@NotBlank String name, String positioning, String strengths, String weaknesses) { }
    record VersionRequest(@NotNull Long version) { }
    record View(String id, String name, String positioning, String strengths, String weaknesses, String status, String ownerUserId, long version) { }
    @PostMapping
    public ResponseEntity<ApiResponse<View>> create(@RequestBody CreateRequest request, @RequestHeader("Idempotency-Key") String key) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(view(service.create(ActorContext.require(), new CompetitorService.Create(request.name(), request.positioning(), request.strengths(), request.weaknesses()), key))));
    }
    @GetMapping public ApiResponse<List<View>> list() { return ok(service.list(ActorContext.require()).stream().map(this::view).toList()); }
    @GetMapping("/{id}") public ApiResponse<View> get(@PathVariable long id) { return ok(view(service.get(ActorContext.require(), id))); }
    @PostMapping("/{id}/actions/archive") public ApiResponse<View> archive(@PathVariable long id, @RequestBody VersionRequest request) { return ok(view(service.archive(ActorContext.require(), id, new CompetitorService.Versioned(request.version())))); }
    private View view(CompetitorService.Competitor competitor) { return new View(String.valueOf(competitor.id()), competitor.name(), competitor.positioning(), competitor.strengths(), competitor.weaknesses(), competitor.status().name(), String.valueOf(competitor.ownerUserId()), competitor.version()); }
    private <T> ApiResponse<T> ok(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
