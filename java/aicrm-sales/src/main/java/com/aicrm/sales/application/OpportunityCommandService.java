package com.aicrm.sales.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.DataScope;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.sales.domain.SalesRepository;
import com.aicrm.sales.domain.customer.Customer;
import com.aicrm.sales.domain.lead.Lead;
import com.aicrm.sales.domain.opportunity.Opportunity;
import com.aicrm.sales.domain.opportunity.OpportunityRepository;
import com.aicrm.sales.domain.opportunity.OpportunityStageHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

/** Transactional commands for the opportunity pipeline and its immutable stage journal. */
@Service
public class OpportunityCommandService {
    private final OpportunityRepository repository;
    private final SalesRepository salesRepository;
    private final IdGenerator idGenerator;
    private final IdempotencyService idempotencyService;
    private final AuditLogService auditLogService;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public OpportunityCommandService(OpportunityRepository repository, SalesRepository salesRepository,
                                     IdGenerator idGenerator, IdempotencyService idempotencyService,
                                     AuditLogService auditLogService, OutboxService outboxService,
                                     ObjectMapper objectMapper) {
        this.repository = repository;
        this.salesRepository = salesRepository;
        this.idGenerator = idGenerator;
        this.idempotencyService = idempotencyService;
        this.auditLogService = auditLogService;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Opportunity create(Actor actor, OpportunityCommands.Create command, String idempotencyKey) {
        require(actor, "opportunity:create");
        return idempotencyService.execute(actor, "opportunity:create", idempotencyKey, command, Opportunity.class, () -> {
            Customer customer = salesRepository.findCustomer(actor.tenantId(), command.customerId())
                    .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "客户不存在"));
            requireCustomerVisible(actor, customer);
            validateReferences(actor, command, customer);
            Instant now = Instant.now();
            Opportunity opportunity = new Opportunity(idGenerator.nextId(), actor.tenantId(), "OPP-" + idGenerator.nextId(),
                    required(command.name(), "商机名称"), customer.id(), command.contactId(), command.sourceLeadId(),
                    Opportunity.Stage.DISCOVERY, Opportunity.Status.OPEN, amount(command.expectedAmount()), currency(command.currency()),
                    probability(command.probability()), command.expectedCloseDate(), actor.userId(), actor.departmentId(),
                    null, null, null, 0, now, now);
            Opportunity created = repository.insert(opportunity, actor.userId());
            journal(actor, OpportunityStageHistory.Action.CREATE, null, created, null, "OpportunityCreated");
            return created;
        });
    }

    @Transactional
    public Opportunity changeStage(Actor actor, long opportunityId, OpportunityCommands.ChangeStage command) {
        require(actor, "opportunity:stage");
        Opportunity before = opportunity(actor, opportunityId);
        requireWrite(actor, before);
        requireOpen(before);
        Opportunity.Stage target = stage(command.stage());
        if (target == Opportunity.Stage.CLOSED_WON || target == Opportunity.Stage.CLOSED_LOST || target == before.stage()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "只能推进至不同的进行中阶段");
        }
        short probability = probability(command.probability());
        if (!repository.changeStage(actor.tenantId(), opportunityId, command.version(), target, probability, actor.userId())) {
            throw conflict("商机阶段已被其他操作修改");
        }
        Opportunity after = opportunity(actor, opportunityId);
        journal(actor, OpportunityStageHistory.Action.STAGE_CHANGED, before, after, null, "OpportunityStageChanged");
        return after;
    }

    @Transactional
    public Opportunity win(Actor actor, long opportunityId, long version) {
        require(actor, "opportunity:win");
        Opportunity before = opportunity(actor, opportunityId);
        requireWrite(actor, before);
        requireOpen(before);
        if (!repository.win(actor.tenantId(), opportunityId, version, actor.userId())) {
            throw conflict("赢单失败，商机已被其他操作修改");
        }
        Opportunity after = opportunity(actor, opportunityId);
        journal(actor, OpportunityStageHistory.Action.WON, before, after, null, "OpportunityWon");
        return after;
    }

    @Transactional
    public Opportunity lose(Actor actor, long opportunityId, long version, String reason) {
        require(actor, "opportunity:lose");
        Opportunity before = opportunity(actor, opportunityId);
        requireWrite(actor, before);
        requireOpen(before);
        String lostReason = required(reason, "输单原因");
        if (!repository.lose(actor.tenantId(), opportunityId, version, lostReason, actor.userId())) {
            throw conflict("输单失败，商机已被其他操作修改");
        }
        Opportunity after = opportunity(actor, opportunityId);
        journal(actor, OpportunityStageHistory.Action.LOST, before, after, lostReason, "OpportunityLost");
        return after;
    }

    @Transactional
    public Opportunity restart(Actor actor, long opportunityId, long version) {
        require(actor, "opportunity:restart");
        Opportunity before = opportunity(actor, opportunityId);
        requireWrite(actor, before);
        if (before.status() != Opportunity.Status.LOST) {
            throw new DomainException(ErrorCode.CONFLICT, "只有输单商机可以重启");
        }
        if (!repository.restart(actor.tenantId(), opportunityId, version, actor.userId())) {
            throw conflict("重启失败，商机已被其他操作修改");
        }
        Opportunity after = opportunity(actor, opportunityId);
        journal(actor, OpportunityStageHistory.Action.RESTARTED, before, after, null, "OpportunityRestarted");
        return after;
    }

    private void validateReferences(Actor actor, OpportunityCommands.Create command, Customer customer) {
        if (command.contactId() != null && salesRepository.findContacts(actor.tenantId(), customer.id()).stream()
                .noneMatch(contact -> contact.id() == command.contactId())) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "联系人不属于该客户");
        }
        if (command.sourceLeadId() != null) {
            Lead lead = salesRepository.findLead(actor.tenantId(), command.sourceLeadId())
                    .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "来源线索不存在"));
            if (lead.customerId() != null && lead.customerId() != customer.id()) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "来源线索已关联其他客户");
            }
        }
    }

    private Opportunity opportunity(Actor actor, long id) {
        return repository.find(actor.tenantId(), id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "商机不存在"));
    }

    private void requireCustomerVisible(Actor actor, Customer customer) {
        if (customer.ownerUserId() != null && customer.ownerUserId() == actor.userId() && actor.hasPermission("customer:read:own")) {
            return;
        }
        if (actor.hasPermission("customer:read:any") && customer.ownerDeptId() != null
                && salesRepository.isDepartmentInActorScope(actor, customer.ownerDeptId())) {
            return;
        }
        if (customer.ownerUserId() == null && actor.hasPermission("customer:read:own")) {
            return;
        }
        throw new DomainException(ErrorCode.FORBIDDEN, "无权使用该客户创建商机");
    }

    private void requireWrite(Actor actor, Opportunity opportunity) {
        if (opportunity.ownerUserId() == actor.userId() && actor.hasPermission("opportunity:write:own")) {
            return;
        }
        if (actor.hasPermission("opportunity:write:any") && salesRepository.isDepartmentInActorScope(actor, opportunity.ownerDeptId())) {
            return;
        }
        throw new DomainException(ErrorCode.FORBIDDEN, "无权维护该商机");
    }

    private void requireOpen(Opportunity opportunity) {
        if (opportunity.status() != Opportunity.Status.OPEN) {
            throw new DomainException(ErrorCode.CONFLICT, "商机已处于终态");
        }
    }

    private void journal(Actor actor, OpportunityStageHistory.Action action, Opportunity before, Opportunity after,
                         String reason, String eventType) {
        String operationId = "op-" + idGenerator.nextId();
        repository.appendHistory(idGenerator.nextId(), actor.tenantId(), after.id(), action, before, after, reason, actor.userId());
        String beforeJson = json(before);
        String afterJson = json(after);
        auditLogService.record(actor, action.name(), "OPPORTUNITY", after.id(), operationId, "API", null, beforeJson, afterJson);
        outboxService.append(new DomainEvent(eventType, "OPPORTUNITY", after.id(), actor.tenantId(),
                json(Map.of("opportunityId", String.valueOf(after.id()), "action", action.name())), Instant.now()), operationId, actor.userId());
    }

    private Opportunity.Stage stage(String value) {
        try {
            return Opportunity.Stage.valueOf(required(value, "商机阶段").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "商机阶段无效");
        }
    }

    private BigDecimal amount(BigDecimal value) {
        if (value == null || value.signum() < 0) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "预计金额不能小于零");
        }
        return value;
    }

    private short probability(short value) {
        if (value < 0 || value > 100) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "赢单概率必须在 0 到 100 之间");
        }
        return value;
    }

    private String currency(String value) {
        String normalized = required(value, "币种").toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{3}")) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "币种必须为 ISO 4217 三位代码");
        }
        return normalized;
    }

    private void require(Actor actor, String permission) {
        if (!actor.hasPermission(permission)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission);
        }
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, field + "不能为空");
        }
        return value.trim();
    }

    private DomainException conflict(String message) {
        return new DomainException(ErrorCode.CONFLICT, message);
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? Map.of() : value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法序列化商机审计快照", exception);
        }
    }
}
