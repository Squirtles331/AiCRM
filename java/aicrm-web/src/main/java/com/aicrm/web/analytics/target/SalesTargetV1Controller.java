package com.aicrm.web.analytics.target;

import com.aicrm.analytics.target.application.SalesTargetCommandService;
import com.aicrm.analytics.target.application.SalesTargetCommands;
import com.aicrm.analytics.target.application.SalesTargetReadService;
import com.aicrm.analytics.target.domain.SalesTarget;
import com.aicrm.analytics.target.domain.SalesTargetResult;
import com.aicrm.analytics.performance.domain.SalesTargetScore;
import com.aicrm.kernel.security.ActorContext;
import com.aicrm.kernel.security.TraceContext;
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

@RestController
@RequestMapping("/api/v1/sales-targets")
@Tag(name = "CRM V1 - 销售目标")
public class SalesTargetV1Controller {
    private final SalesTargetCommandService commands; private final SalesTargetReadService reads;
    public SalesTargetV1Controller(SalesTargetCommandService commands, SalesTargetReadService reads) { this.commands = commands; this.reads = reads; }
    @PostMapping @Operation(summary = "创建销售目标")
    public ResponseEntity<ApiResponse<SalesTargetApiDtos.View>> create(@Valid @RequestBody SalesTargetApiDtos.CreateRequest request, @RequestHeader("Idempotency-Key") String key) {
        SalesTarget target = commands.create(ActorContext.require(), new SalesTargetCommands.Create(request.name(), request.targetUserId(), request.metric(), request.periodFrom(), request.periodTo(), request.targetValue(), request.scoreRuleId()), key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(view(new SalesTargetReadService.TargetDetail(target, null, null))));
    }
    @GetMapping("/{id}") @Operation(summary = "查询销售目标") public ApiResponse<SalesTargetApiDtos.View> detail(@PathVariable long id) { return success(view(reads.target(ActorContext.require(), id))); }
    @PostMapping("/{id}/actions/activate") @Operation(summary = "启用销售目标") public ApiResponse<SalesTargetApiDtos.View> activate(@PathVariable long id, @Valid @RequestBody SalesTargetApiDtos.VersionRequest request) { return success(view(new SalesTargetReadService.TargetDetail(commands.activate(ActorContext.require(), id, new SalesTargetCommands.Versioned(request.version())), null, null))); }
    @PostMapping("/{id}/actions/confirm-result") @Operation(summary = "确认 CRM 销售目标结果") public ApiResponse<SalesTargetApiDtos.View> confirm(@PathVariable long id, @Valid @RequestBody SalesTargetApiDtos.VersionRequest request) { return success(view(commands.confirmResult(ActorContext.require(), id, new SalesTargetCommands.Versioned(request.version())))); }
    private static SalesTargetApiDtos.View view(SalesTargetReadService.TargetDetail detail) { SalesTarget t=detail.target(); SalesTargetResult r=detail.result(); SalesTargetScore score=detail.score(); SalesTargetApiDtos.ScoreView scoreView=score==null?null:new SalesTargetApiDtos.ScoreView(String.valueOf(score.id()),String.valueOf(score.scoreRuleId()),score.score(),score.achievementRate(),score.calculationSnapshot(),String.valueOf(score.confirmedBy()),score.confirmedAt()); return new SalesTargetApiDtos.View(String.valueOf(t.id()),t.targetNo(),t.name(),String.valueOf(t.targetUserId()),t.metric().name(),t.periodFrom(),t.periodTo(),t.targetValue(),t.scoreRuleId()==null?null:String.valueOf(t.scoreRuleId()),t.status().name(),t.version(),t.createdAt(),t.updatedAt(),r==null?null:new SalesTargetApiDtos.ResultView(String.valueOf(r.id()),r.actualValue(),r.achievementRate(),r.calculationSnapshot(),String.valueOf(r.confirmedBy()),r.calculatedAt(),r.confirmedAt(),scoreView)); }
    private <T> ApiResponse<T> success(T data) { return ApiResponse.success(data, TraceContext.get()); }
}
