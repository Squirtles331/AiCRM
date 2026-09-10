package com.aicrm.analytics.performance.application;

import com.aicrm.analytics.performance.domain.PerformanceScoreBand;
import com.aicrm.analytics.performance.domain.PerformanceScoreRule;
import com.aicrm.analytics.performance.domain.PerformanceScoreRuleRepository;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceScoreRuleReadService {
    private final PerformanceScoreRuleRepository repository;
    public PerformanceScoreRuleReadService(PerformanceScoreRuleRepository repository) { this.repository = repository; }
    public Detail rule(Actor actor, long id) {
        if (!actor.hasPermission("performance:rule:read") && !actor.hasPermission("performance:rule:manage")) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：performance:rule:read");
        PerformanceScoreRule rule = repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "绩效评分规则不存在"));
        return new Detail(rule, repository.bands(actor.tenantId(), id));
    }
    public record Detail(PerformanceScoreRule rule, List<PerformanceScoreBand> bands) { }
}
