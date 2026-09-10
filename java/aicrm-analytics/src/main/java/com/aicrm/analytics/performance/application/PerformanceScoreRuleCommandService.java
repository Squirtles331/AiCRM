package com.aicrm.analytics.performance.application;

import com.aicrm.analytics.performance.domain.PerformanceScoreBand;
import com.aicrm.analytics.performance.domain.PerformanceScoreRule;
import com.aicrm.analytics.performance.domain.PerformanceScoreRuleRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PerformanceScoreRuleCommandService {
    private final PerformanceScoreRuleRepository repository; private final IdGenerator ids; private final IdempotencyService idempotency;
    private final AuditLogService audit; private final OutboxService outbox; private final ObjectMapper mapper;
    public PerformanceScoreRuleCommandService(PerformanceScoreRuleRepository repository, IdGenerator ids, IdempotencyService idempotency, AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.repository=repository; this.ids=ids; this.idempotency=idempotency; this.audit=audit; this.outbox=outbox; this.mapper=mapper;
    }
    @Transactional
    public PerformanceScoreRule create(Actor actor, PerformanceScoreRuleCommands.Create command, String key) {
        require(actor); List<PerformanceScoreBand> bands = validatedBands(command);
        return idempotency.execute(actor, "performance-score-rule:create", key, command, PerformanceScoreRule.class, () -> {
            Instant now = Instant.now();
            PerformanceScoreRule rule = new PerformanceScoreRule(ids.nextId(), actor.tenantId(), "PSR-" + ids.nextId(), command.name().trim(), command.metric(), PerformanceScoreRule.Status.DRAFT, 0, now, now);
            PerformanceScoreRule created = repository.insert(rule, bands, actor.userId()); journal(actor, "CREATE", created, "PerformanceScoreRuleCreated"); return created;
        });
    }
    @Transactional
    public PerformanceScoreRule activate(Actor actor, long id, PerformanceScoreRuleCommands.Versioned command) {
        require(actor); existing(actor,id);
        if (!repository.activate(actor.tenantId(),id,command.version(),actor.userId())) throw conflict("仅草稿且版本匹配的绩效评分规则可以启用");
        PerformanceScoreRule rule=existing(actor,id); journal(actor,"ACTIVATE",rule,"PerformanceScoreRuleActivated"); return rule;
    }
    @Transactional
    public PerformanceScoreRule retire(Actor actor, long id, PerformanceScoreRuleCommands.Versioned command) {
        require(actor); existing(actor,id);
        if (!repository.retire(actor.tenantId(),id,command.version(),actor.userId())) throw conflict("仅启用且版本匹配的绩效评分规则可以退役");
        PerformanceScoreRule rule=existing(actor,id); journal(actor,"RETIRE",rule,"PerformanceScoreRuleRetired"); return rule;
    }
    private List<PerformanceScoreBand> validatedBands(PerformanceScoreRuleCommands.Create command) {
        if (command.name()==null || command.name().isBlank() || command.name().length()>200 || command.metric()==null || command.bands()==null || command.bands().isEmpty()) throw new DomainException(ErrorCode.VALIDATION_ERROR,"绩效评分规则字段不合法");
        List<PerformanceScoreRuleCommands.Band> input = new ArrayList<>(command.bands()); input.sort(Comparator.comparing(PerformanceScoreRuleCommands.Band::minimumAchievementRate, Comparator.nullsFirst(Comparator.naturalOrder())));
        List<PerformanceScoreBand> bands = new ArrayList<>(); BigDecimal expected = BigDecimal.ZERO.setScale(4);
        for (int index=0; index<input.size(); index++) {
            PerformanceScoreRuleCommands.Band band=input.get(index); boolean finalBand=index==input.size()-1;
            if (band.minimumAchievementRate()==null || band.score()==null || band.minimumAchievementRate().signum()<0 || band.score().signum()<0 || band.minimumAchievementRate().compareTo(expected)!=0) throw new DomainException(ErrorCode.VALIDATION_ERROR,"评分区间必须从 0 连续覆盖，且分值不能为负数");
            if (finalBand ? band.maximumAchievementRate()!=null : band.maximumAchievementRate()==null || band.maximumAchievementRate().compareTo(band.minimumAchievementRate())<=0) throw new DomainException(ErrorCode.VALIDATION_ERROR,"仅最后一个评分区间可以没有上限，且上限必须大于下限");
            BigDecimal minimum=band.minimumAchievementRate().setScale(4,RoundingMode.HALF_UP); BigDecimal maximum=band.maximumAchievementRate()==null?null:band.maximumAchievementRate().setScale(4,RoundingMode.HALF_UP);
            bands.add(new PerformanceScoreBand(ids.nextId(),index+1,minimum,maximum,band.score().setScale(2,RoundingMode.HALF_UP)));
            if (!finalBand) expected=maximum;
        }
        return List.copyOf(bands);
    }
    private PerformanceScoreRule existing(Actor actor,long id) { return repository.find(actor.tenantId(),id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND,"绩效评分规则不存在")); }
    private void require(Actor actor) { if (!actor.hasPermission("performance:rule:manage")) throw new DomainException(ErrorCode.FORBIDDEN,"缺少权限：performance:rule:manage"); }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT,message); }
    private void journal(Actor actor,String action,PerformanceScoreRule rule,String event) { String operation="performance-rule:"+action+":"+rule.id()+":"+ids.nextId(); String data=json(rule); audit.record(actor,action,"PERFORMANCE_SCORE_RULE",rule.id(),operation,"API",null,"{}",data); outbox.append(new DomainEvent(event,"PERFORMANCE_SCORE_RULE",rule.id(),actor.tenantId(),data,Instant.now()),operation,actor.userId()); }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException exception) { throw new IllegalStateException("无法序列化绩效评分规则",exception); } }
}
