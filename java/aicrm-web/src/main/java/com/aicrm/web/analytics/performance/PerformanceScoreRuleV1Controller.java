package com.aicrm.web.analytics.performance;

import com.aicrm.analytics.performance.application.PerformanceScoreRuleCommandService;
import com.aicrm.analytics.performance.application.PerformanceScoreRuleCommands;
import com.aicrm.analytics.performance.application.PerformanceScoreRuleReadService;
import com.aicrm.analytics.performance.domain.PerformanceScoreRule;
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
@RequestMapping("/api/v1/performance-score-rules")
@Tag(name = "CRM V1 - 绩效评分规则")
public class PerformanceScoreRuleV1Controller {
    private final PerformanceScoreRuleCommandService commands; private final PerformanceScoreRuleReadService reads;
    public PerformanceScoreRuleV1Controller(PerformanceScoreRuleCommandService commands, PerformanceScoreRuleReadService reads) { this.commands=commands; this.reads=reads; }
    @PostMapping @Operation(summary = "创建绩效评分规则")
    public ResponseEntity<ApiResponse<PerformanceScoreRuleApiDtos.View>> create(@Valid @RequestBody PerformanceScoreRuleApiDtos.CreateRequest request,@RequestHeader("Idempotency-Key") String key) {
        PerformanceScoreRule rule=commands.create(ActorContext.require(),new PerformanceScoreRuleCommands.Create(request.name(),request.metric(),request.bands().stream().map(band->new PerformanceScoreRuleCommands.Band(band.minimumAchievementRate(),band.maximumAchievementRate(),band.score())).toList()),key);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(PerformanceScoreRuleApiDtos.view(rule,reads.rule(ActorContext.require(),rule.id()).bands())));
    }
    @GetMapping("/{id}") @Operation(summary = "查询绩效评分规则") public ApiResponse<PerformanceScoreRuleApiDtos.View> detail(@PathVariable long id) { PerformanceScoreRuleReadService.Detail detail=reads.rule(ActorContext.require(),id); return success(PerformanceScoreRuleApiDtos.view(detail.rule(),detail.bands())); }
    @PostMapping("/{id}/actions/activate") @Operation(summary = "启用绩效评分规则") public ApiResponse<PerformanceScoreRuleApiDtos.View> activate(@PathVariable long id,@Valid @RequestBody PerformanceScoreRuleApiDtos.VersionRequest request) { PerformanceScoreRule rule=commands.activate(ActorContext.require(),id,new PerformanceScoreRuleCommands.Versioned(request.version())); return view(rule.id()); }
    @PostMapping("/{id}/actions/retire") @Operation(summary = "退役绩效评分规则") public ApiResponse<PerformanceScoreRuleApiDtos.View> retire(@PathVariable long id,@Valid @RequestBody PerformanceScoreRuleApiDtos.VersionRequest request) { PerformanceScoreRule rule=commands.retire(ActorContext.require(),id,new PerformanceScoreRuleCommands.Versioned(request.version())); return view(rule.id()); }
    private ApiResponse<PerformanceScoreRuleApiDtos.View> view(long id) { PerformanceScoreRuleReadService.Detail detail=reads.rule(ActorContext.require(),id); return success(PerformanceScoreRuleApiDtos.view(detail.rule(),detail.bands())); }
    private <T> ApiResponse<T> success(T value) { return ApiResponse.success(value, TraceContext.get()); }
}
