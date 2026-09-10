package com.aicrm.analytics.target.application;

import com.aicrm.analytics.target.domain.SalesTarget;
import com.aicrm.analytics.target.domain.SalesTargetRepository;
import com.aicrm.analytics.target.domain.SalesTargetResult;
import com.aicrm.analytics.performance.domain.PerformanceScoreBand;
import com.aicrm.analytics.performance.domain.PerformanceScoreRule;
import com.aicrm.analytics.performance.domain.PerformanceScoreRuleRepository;
import com.aicrm.analytics.performance.domain.SalesTargetScore;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class SalesTargetCommandService {
    private final SalesTargetRepository repository; private final IdGenerator ids; private final IdempotencyService idempotency;
    private final AuditLogService audit; private final OutboxService outbox; private final ObjectMapper mapper; private final PerformanceScoreRuleRepository scoreRules;
    public SalesTargetCommandService(SalesTargetRepository repository, IdGenerator ids, IdempotencyService idempotency,
                                     AuditLogService audit, OutboxService outbox, ObjectMapper mapper, PerformanceScoreRuleRepository scoreRules) {
        this.repository = repository; this.ids = ids; this.idempotency = idempotency; this.audit = audit; this.outbox = outbox; this.mapper = mapper; this.scoreRules = scoreRules;
    }
    @Transactional
    public SalesTarget create(Actor actor, SalesTargetCommands.Create command, String key) {
        require(actor, "target:manage"); validate(command);
        return idempotency.execute(actor, "sales-target:create", key, command, SalesTarget.class, () -> {
            if (!repository.userExists(actor.tenantId(), command.targetUserId())) throw new DomainException(ErrorCode.VALIDATION_ERROR, "目标销售人员不存在、已停用或不属于当前租户");
            if (command.scoreRuleId()!=null && scoreRules.scoringRule(actor.tenantId(), command.scoreRuleId(), command.metric()).filter(rule -> rule.status()==PerformanceScoreRule.Status.ACTIVE).isEmpty()) throw new DomainException(ErrorCode.VALIDATION_ERROR, "评分规则不存在、未启用或指标不匹配");
            Instant now = Instant.now();
            SalesTarget target = new SalesTarget(ids.nextId(), actor.tenantId(), "TGT-" + ids.nextId(), command.name().trim(), command.targetUserId(), command.metric(),
                    command.periodFrom(), command.periodTo(), command.targetValue().setScale(2, RoundingMode.HALF_UP), command.scoreRuleId(), SalesTarget.Status.DRAFT, 0, now, now);
            SalesTarget created = repository.insert(target, actor.userId()); journal(actor, "CREATE", created, "SalesTargetCreated"); return created;
        });
    }
    @Transactional
    public SalesTarget activate(Actor actor, long id, SalesTargetCommands.Versioned command) {
        require(actor, "target:manage"); existing(actor, id);
        if (!repository.activate(actor.tenantId(), id, command.version(), actor.userId())) throw conflict("仅草稿且版本匹配的销售目标可以启用");
        SalesTarget target = existing(actor, id); journal(actor, "ACTIVATE", target, "SalesTargetActivated"); return target;
    }
    @Transactional
    public SalesTargetReadService.TargetDetail confirmResult(Actor actor, long id, SalesTargetCommands.Versioned command) {
        require(actor, "target:confirm"); SalesTarget target = existing(actor, id);
        if (repository.findResult(actor.tenantId(), id).isPresent()) throw conflict("销售目标结果已确认");
        if (target.status() != SalesTarget.Status.ACTIVE || !target.periodTo().isBefore(LocalDate.now(ZoneOffset.UTC))) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "仅已结束的启用销售目标可以确认结果");
        }
        BigDecimal actual = repository.actualValue(target).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rate = actual.divide(target.targetValue(), 4, RoundingMode.HALF_UP);
        Instant now = Instant.now();
        SalesTargetResult result = new SalesTargetResult(ids.nextId(), id, actual, rate, json(new CalculationSnapshot(target, actual, rate)), actor.userId(), now, now);
        repository.insertResult(result, actor.tenantId(), actor.userId());
        SalesTargetScore score = target.scoreRuleId()==null ? null : calculateScore(actor, target, result, now);
        if (!repository.confirm(actor.tenantId(), id, command.version(), actor.userId())) throw conflict("销售目标已被其他操作修改");
        SalesTarget confirmed = existing(actor, id); journal(actor, "CONFIRM_RESULT", confirmed, "SalesTargetResultConfirmed");
        return new SalesTargetReadService.TargetDetail(confirmed, result, score);
    }
    private SalesTarget existing(Actor actor, long id) { return repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "销售目标不存在")); }
    private void validate(SalesTargetCommands.Create c) {
        if (c.name() == null || c.name().isBlank() || c.name().length() > 200 || c.metric() == null || c.periodFrom() == null || c.periodTo() == null || c.targetValue() == null || c.targetValue().signum() <= 0) throw new DomainException(ErrorCode.VALIDATION_ERROR, "销售目标字段不合法");
        if (!c.periodTo().isAfter(c.periodFrom())) throw new DomainException(ErrorCode.VALIDATION_ERROR, "目标结束日期必须晚于开始日期");
    }
    private void require(Actor actor, String permission) { if (!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission); }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT, message); }
    private SalesTargetScore calculateScore(Actor actor, SalesTarget target, SalesTargetResult result, Instant now) {
        PerformanceScoreRule rule = scoreRules.scoringRule(actor.tenantId(),target.scoreRuleId(),target.metric()).orElseThrow(() -> new DomainException(ErrorCode.CONFLICT,"目标绑定的评分规则不可用于计算"));
        PerformanceScoreBand band = scoreRules.bands(actor.tenantId(),rule.id()).stream().filter(candidate -> result.achievementRate().compareTo(candidate.minimumAchievementRate())>=0 && (candidate.maximumAchievementRate()==null || result.achievementRate().compareTo(candidate.maximumAchievementRate())<0)).findFirst().orElseThrow(() -> new DomainException(ErrorCode.CONFLICT,"评分规则未覆盖目标达成率"));
        SalesTargetScore score = new SalesTargetScore(ids.nextId(),target.id(),result.id(),rule.id(),result.achievementRate(),band.score(),json(new ScoreSnapshot(rule,band,result)),actor.userId(),now);
        scoreRules.insertTargetScore(score,actor.tenantId(),actor.userId());
        String operation="target-score:CONFIRM:"+target.id()+":"+ids.nextId(); String data=json(score); audit.record(actor,"CONFIRM","SALES_TARGET_SCORE",score.id(),operation,"API",null,"{}",data); outbox.append(new DomainEvent("SalesTargetScoreConfirmed","SALES_TARGET_SCORE",score.id(),actor.tenantId(),data,Instant.now()),operation,actor.userId());
        return score;
    }
    private void journal(Actor actor, String action, SalesTarget target, String event) { String operation = "target:" + action + ":" + target.id() + ":" + ids.nextId(); String data = json(target); audit.record(actor, action, "SALES_TARGET", target.id(), operation, "API", null, "{}", data); outbox.append(new DomainEvent(event, "SALES_TARGET", target.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId()); }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException e) { throw new IllegalStateException("无法序列化销售目标", e); } }
    private record CalculationSnapshot(String metric, long targetUserId, LocalDate periodFrom, LocalDate periodTo, BigDecimal targetValue, BigDecimal actualValue, BigDecimal achievementRate) { CalculationSnapshot(SalesTarget target, BigDecimal actual, BigDecimal rate) { this(target.metric().name(), target.targetUserId(), target.periodFrom(), target.periodTo(), target.targetValue(), actual, rate); } }
    private record ScoreSnapshot(String ruleNo, String metric, int bandNo, BigDecimal minimumAchievementRate, BigDecimal maximumAchievementRate, BigDecimal achievementRate, BigDecimal score) { ScoreSnapshot(PerformanceScoreRule rule, PerformanceScoreBand band, SalesTargetResult result) { this(rule.ruleNo(),rule.metric().name(),band.bandNo(),band.minimumAchievementRate(),band.maximumAchievementRate(),result.achievementRate(),band.score()); } }
}
