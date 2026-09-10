package com.aicrm.analytics.performance.infrastructure;

import com.aicrm.analytics.performance.domain.PerformanceScoreBand;
import com.aicrm.analytics.performance.domain.PerformanceScoreRule;
import com.aicrm.analytics.performance.domain.PerformanceScoreRuleRepository;
import com.aicrm.analytics.performance.domain.SalesTargetScore;
import com.aicrm.analytics.target.domain.SalesTarget;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPerformanceScoreRuleRepository implements PerformanceScoreRuleRepository {
    private final JdbcTemplate jdbc;
    public JdbcPerformanceScoreRuleRepository(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public PerformanceScoreRule insert(PerformanceScoreRule rule,List<PerformanceScoreBand> bands,long actor) {
        jdbc.update("insert into crm_performance_score_rule (id,tenant_id,rule_no,name,metric,status,version,created_by,updated_by,created_at,updated_at) values (?,?,?,?,?,?,?,?,?,?,?)",rule.id(),rule.tenantId(),rule.ruleNo(),rule.name(),rule.metric().name(),rule.status().name(),rule.version(),actor,actor,ts(rule.createdAt()),ts(rule.updatedAt()));
        for (PerformanceScoreBand band:bands) jdbc.update("insert into crm_performance_score_band (id,tenant_id,rule_id,band_no,minimum_achievement_rate,maximum_achievement_rate,score,created_by,updated_by) values (?,?,?,?,?,?,?,?,?)",band.id(),rule.tenantId(),rule.id(),band.bandNo(),band.minimumAchievementRate(),band.maximumAchievementRate(),band.score(),actor,actor);
        return find(rule.tenantId(),rule.id()).orElseThrow();
    }
    public Optional<PerformanceScoreRule> find(long tenant,long id) { return jdbc.query("select * from crm_performance_score_rule where tenant_id=? and id=? and deleted_at is null",(rs,row)->rule(rs),tenant,id).stream().findFirst(); }
    public List<PerformanceScoreBand> bands(long tenant,long rule) { return jdbc.query("select * from crm_performance_score_band where tenant_id=? and rule_id=? order by band_no",(rs,row)->new PerformanceScoreBand(rs.getLong("id"),rs.getInt("band_no"),rs.getBigDecimal("minimum_achievement_rate"),rs.getBigDecimal("maximum_achievement_rate"),rs.getBigDecimal("score")),tenant,rule); }
    public boolean activate(long tenant,long id,long version,long actor) { return jdbc.update("update crm_performance_score_rule set status='ACTIVE',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='DRAFT' and version=? and deleted_at is null",actor,tenant,id,version)==1; }
    public boolean retire(long tenant,long id,long version,long actor) { return jdbc.update("update crm_performance_score_rule set status='RETIRED',version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='ACTIVE' and version=? and deleted_at is null",actor,tenant,id,version)==1; }
    public Optional<PerformanceScoreRule> scoringRule(long tenant,long id,SalesTarget.Metric metric) { return jdbc.query("select * from crm_performance_score_rule where tenant_id=? and id=? and metric=? and status in ('ACTIVE','RETIRED') and deleted_at is null",(rs,row)->rule(rs),tenant,id,metric.name()).stream().findFirst(); }
    public Optional<SalesTargetScore> findTargetScore(long tenant,long target) { return jdbc.query("select * from crm_sales_target_score where tenant_id=? and target_id=?",(rs,row)->new SalesTargetScore(rs.getLong("id"),rs.getLong("target_id"),rs.getLong("target_result_id"),rs.getLong("score_rule_id"),rs.getBigDecimal("achievement_rate"),rs.getBigDecimal("score"),rs.getString("calculation_snapshot"),rs.getLong("confirmed_by"),instant(rs,"confirmed_at")),tenant,target).stream().findFirst(); }
    public SalesTargetScore insertTargetScore(SalesTargetScore score,long tenant,long actor) { jdbc.update("insert into crm_sales_target_score (id,tenant_id,target_id,target_result_id,score_rule_id,achievement_rate,score,calculation_snapshot,confirmed_by,confirmed_at,created_by,updated_by) values (?,?,?,?,?,?,?,?::jsonb,?,?,?,?)",score.id(),tenant,score.targetId(),score.targetResultId(),score.scoreRuleId(),score.achievementRate(),score.score(),score.calculationSnapshot(),score.confirmedBy(),ts(score.confirmedAt()),actor,actor); return score; }
    private PerformanceScoreRule rule(java.sql.ResultSet rs) throws java.sql.SQLException { return new PerformanceScoreRule(rs.getLong("id"),rs.getLong("tenant_id"),rs.getString("rule_no"),rs.getString("name"),SalesTarget.Metric.valueOf(rs.getString("metric")),PerformanceScoreRule.Status.valueOf(rs.getString("status")),rs.getLong("version"),instant(rs,"created_at"),instant(rs,"updated_at")); }
    private Instant instant(java.sql.ResultSet rs,String column) throws java.sql.SQLException { Timestamp value=rs.getTimestamp(column); return value==null?null:value.toInstant(); }
    private Timestamp ts(Instant value) { return value==null?null:Timestamp.from(value); }
}
