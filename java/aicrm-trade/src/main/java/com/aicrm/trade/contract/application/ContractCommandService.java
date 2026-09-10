package com.aicrm.trade.contract.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.trade.contract.domain.Contract;
import com.aicrm.trade.contract.domain.ContractLine;
import com.aicrm.trade.contract.domain.ContractRepository;
import com.aicrm.trade.quote.application.QuoteReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ContractCommandService {
    private final ContractRepository repository;
    private final QuoteReadService quotes;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;

    public ContractCommandService(ContractRepository repository, QuoteReadService quotes, IdGenerator ids, IdempotencyService idempotency,
                                  AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.repository = repository; this.quotes = quotes; this.ids = ids; this.idempotency = idempotency;
        this.audit = audit; this.outbox = outbox; this.mapper = mapper;
    }

    @Transactional
    public Contract create(Actor actor, ContractCommands.Create command, String key) {
        require(actor, "contract:create");
        validatePeriod(command.effectiveFrom(), command.effectiveTo());
        return idempotency.execute(actor, "contract:create", key, command, Contract.class, () -> {
            QuoteReadService.ApprovedContractSource source = quotes.approvedContractSource(actor, command.quoteId());
            if (repository.existsForQuoteVersion(actor.tenantId(), source.quoteId(), source.quoteVersionId())) throw conflict("该报价版本已生成合同");
            Instant now = Instant.now();
            Contract contract = new Contract(ids.nextId(), actor.tenantId(), "CON-" + ids.nextId(), required(command.name(), "合同名称"),
                    source.quoteId(), source.quoteVersionId(), source.customerId(), source.currency(), source.subtotal(), source.discountAmount(),
                    source.taxAmount(), source.totalAmount(), Contract.Status.DRAFT, command.effectiveFrom(), command.effectiveTo(),
                    null, null, null, null, 0, now, now);
            try {
                Contract created = repository.insert(contract, actor.userId());
                for (QuoteReadService.ContractSourceLine line : source.lines()) repository.insertLine(line(actor, created.id(), line, now), actor.userId());
                journal(actor, "CREATE", created, "ContractCreated");
                return created;
            } catch (DuplicateKeyException exception) {
                throw conflict("该报价版本已生成合同");
            }
        });
    }

    @Transactional
    public Contract submitSignature(Actor actor, long contractId, ContractCommands.Versioned command) {
        require(actor, "contract:submit-signature");
        Contract contract = writable(actor, contractId);
        if (!repository.transition(actor.tenantId(), contractId, Contract.Status.DRAFT, Contract.Status.PENDING_SIGNATURE, command.version(), actor.userId())) {
            throw conflict("合同已被其他操作修改，或当前状态不可提交签署");
        }
        Contract after = writable(actor, contractId); journal(actor, "SUBMIT_SIGNATURE", after, "ContractSignatureSubmitted"); return after;
    }

    @Transactional
    public Contract sign(Actor actor, long contractId, ContractCommands.Versioned command) {
        require(actor, "contract:sign");
        Contract contract = writable(actor, contractId);
        if (!repository.transition(actor.tenantId(), contractId, Contract.Status.PENDING_SIGNATURE, Contract.Status.SIGNED, command.version(), actor.userId())) {
            throw conflict("合同已被其他操作修改，或当前状态不可签署");
        }
        Contract after = writable(actor, contractId); journal(actor, "SIGN", after, "ContractSigned"); return after;
    }

    private Contract writable(Actor actor, long id) {
        if (!actor.hasPermission("contract:write:own") && !actor.hasPermission("contract:write:any")
                && !actor.hasPermission("contract:submit-signature") && !actor.hasPermission("contract:sign")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少合同维护权限");
        }
        Contract contract = repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "合同不存在"));
        quotes.quote(actor, contract.quoteId());
        return contract;
    }

    private ContractLine line(Actor actor, long contractId, QuoteReadService.ContractSourceLine source, Instant now) {
        return new ContractLine(ids.nextId(), actor.tenantId(), contractId, source.lineNo(), source.quoteLineId(), source.productId(), source.productNo(), source.sku(),
                source.productName(), source.unit(), source.quantity(), source.listPrice(), source.unitPrice(), source.discountRate(), source.taxRate(), source.lineAmount(), now, now);
    }
    private void validatePeriod(java.time.LocalDate from, java.time.LocalDate to) { if (from != null && to != null && to.isBefore(from)) throw new DomainException(ErrorCode.VALIDATION_ERROR, "合同截止日期不能早于生效日期"); }
    private void require(Actor actor, String permission) { if (!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission); }
    private String required(String value, String name) { if (value == null || value.isBlank()) throw new DomainException(ErrorCode.VALIDATION_ERROR, name + "不能为空"); return value.trim(); }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT, message); }
    private void journal(Actor actor, String action, Contract contract, String event) {
        String operation = "contract:" + action + ":" + contract.id() + ":" + ids.nextId(); String data = json(contract);
        audit.record(actor, action, "CONTRACT", contract.id(), operation, "API", null, "{}", data);
        outbox.append(new DomainEvent(event, "CONTRACT", contract.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId());
    }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException exception) { throw new IllegalStateException("无法序列化合同", exception); } }
}
