package com.aicrm.analytics.performance.domain;

import com.aicrm.analytics.target.domain.SalesTarget;

import java.util.List;
import java.util.Optional;

public interface PerformanceScoreRuleRepository {
    PerformanceScoreRule insert(PerformanceScoreRule rule, List<PerformanceScoreBand> bands, long actorId);
    Optional<PerformanceScoreRule> find(long tenantId, long ruleId);
    List<PerformanceScoreBand> bands(long tenantId, long ruleId);
    boolean activate(long tenantId, long ruleId, long version, long actorId);
    boolean retire(long tenantId, long ruleId, long version, long actorId);
    Optional<PerformanceScoreRule> scoringRule(long tenantId, long ruleId, SalesTarget.Metric metric);
    Optional<SalesTargetScore> findTargetScore(long tenantId, long targetId);
    SalesTargetScore insertTargetScore(SalesTargetScore score, long tenantId, long actorId);
}
