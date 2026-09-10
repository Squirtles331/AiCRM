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
import com.aicrm.trade.contract.domain.ContractChange;
import com.aicrm.trade.contract.domain.ContractChangeRepository;
import com.aicrm.trade.contract.domain.ContractLine;
import com.aicrm.trade.contract.domain.ContractRepository;
import com.aicrm.trade.quote.application.QuoteReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ContractCommandService {
    private final ContractRepository repository;
    private final ContractChangeRepository changes;
    private final QuoteReadService quotes;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;

    public ContractCommandService(ContractRepository repository, ContractChangeRepository changes, QuoteReadService quotes, IdGenerator ids, IdempotencyService idempotency,
                                  AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.repository = repository; this.changes = changes; this.quotes = quotes; this.ids = ids; this.idempotency = idempotency;
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
        Contract contract = existing(actor, contractId);
        if (!repository.transition(actor.tenantId(), contractId, Contract.Status.DRAFT, Contract.Status.PENDING_SIGNATURE, command.version(), actor.userId())) {
            throw conflict("合同已被其他操作修改，或当前状态不可提交签署");
        }
        Contract after = existing(actor, contractId); journal(actor, "SUBMIT_SIGNATURE", after, "ContractSignatureSubmitted"); return after;
    }

    @Transactional
    public Contract sign(Actor actor, long contractId, ContractCommands.Versioned command) {
        require(actor, "contract:sign");
        Contract contract = existing(actor, contractId);
        if (!repository.transition(actor.tenantId(), contractId, Contract.Status.PENDING_SIGNATURE, Contract.Status.SIGNED, command.version(), actor.userId())) {
            throw conflict("合同已被其他操作修改，或当前状态不可签署");
        }
        Contract after = existing(actor, contractId); journal(actor, "SIGN", after, "ContractSigned"); return after;
    }

    @Transactional
    public Contract withdrawSignature(Actor actor, long contractId, ContractCommands.Versioned command) {
        require(actor, "contract:withdraw-signature");
        existing(actor, contractId);
        if (!repository.transition(actor.tenantId(), contractId, Contract.Status.PENDING_SIGNATURE, Contract.Status.DRAFT, command.version(), actor.userId())) {
            throw conflict("合同已被其他操作修改，或当前状态不可撤回签署");
        }
        Contract after = existing(actor, contractId); journal(actor, "WITHDRAW", after, "ContractSignatureWithdrawn"); return after;
    }

    @Transactional
    public Contract voidContract(Actor actor, long contractId, ContractCommands.Reasoned command) {
        require(actor, "contract:void");
        Contract contract = existing(actor, contractId);
        if (contract.status() == Contract.Status.VOIDED) throw conflict("合同已作废");
        if (contract.status() == Contract.Status.SIGNED && repository.hasNonCancelledOrder(actor.tenantId(), contractId)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "存在未取消销售订单的已签合同不可作废");
        }
        String reason = required(command.reason(), "作废原因");
        if (!repository.voidContract(actor.tenantId(), contractId, contract.status(), command.version(), reason, actor.userId())) {
            throw conflict("合同已被其他操作修改，或当前状态不可作废");
        }
        Contract after = existing(actor, contractId); journal(actor, "VOID", after, "ContractVoided"); return after;
    }

    @Transactional
    public ContractChange createChange(Actor actor, long contractId, ContractCommands.CreateChange command, String key) {
        require(actor, "contract:change");
        return idempotency.execute(actor, "contract:change:create", key, command, ContractChange.class, () -> {
            Contract contract = existing(actor, contractId);
            if (contract.status() != Contract.Status.SIGNED) throw new DomainException(ErrorCode.VALIDATION_ERROR, "仅已签署合同可以发起变更");
            if (contract.version() != command.contractVersion()) throw conflict("合同已被其他操作修改");
            String name = command.proposedName() == null ? contract.name() : required(command.proposedName(), "拟变更合同名称");
            java.time.LocalDate from = command.proposedEffectiveFrom() == null ? contract.effectiveFrom() : command.proposedEffectiveFrom();
            java.time.LocalDate to = command.proposedEffectiveTo() == null ? contract.effectiveTo() : command.proposedEffectiveTo();
            validatePeriod(from, to);
            if (name.equals(contract.name()) && java.util.Objects.equals(from, contract.effectiveFrom()) && java.util.Objects.equals(to, contract.effectiveTo())) {
                throw new DomainException(ErrorCode.VALIDATION_ERROR, "合同变更必须包含合同名称或有效期调整");
            }
            Instant now = Instant.now();
            ContractChange change = new ContractChange(ids.nextId(), actor.tenantId(), contractId, "CHG-" + ids.nextId(), ContractChange.Status.DRAFT,
                    required(command.reason(), "变更原因"), snapshot(contract, null, null, null), snapshot(contract, name, from, to), null, null, null,
                    null, null, 0, actor.userId(), now, now);
            try {
                ContractChange created = changes.insert(change, actor.userId());
                journalChange(actor, "CREATE", created, "ContractChangeCreated");
                return created;
            } catch (DuplicateKeyException exception) {
                throw conflict("该合同已有未结束的变更申请");
            }
        });
    }

    @Transactional
    public ContractChange submitChange(Actor actor, long contractId, long changeId, ContractCommands.Versioned command) {
        require(actor, "contract:change");
        ContractChange change = change(actor, contractId, changeId);
        if (change.createdBy() != actor.userId()) throw new DomainException(ErrorCode.FORBIDDEN, "仅申请人可以提交合同变更");
        if (!changes.transition(actor.tenantId(), changeId, ContractChange.Status.DRAFT, ContractChange.Status.SUBMITTED, command.version(), null, actor.userId())) {
            throw conflict("合同变更已被其他操作修改，或当前状态不可提交");
        }
        ContractChange after = change(actor, contractId, changeId); journalChange(actor, "SUBMIT", after, "ContractChangeSubmitted"); return after;
    }

    @Transactional
    public ContractChange cancelChange(Actor actor, long contractId, long changeId, ContractCommands.Versioned command) {
        require(actor, "contract:change");
        ContractChange change = change(actor, contractId, changeId);
        if (change.createdBy() != actor.userId()) throw new DomainException(ErrorCode.FORBIDDEN, "仅申请人可以取消合同变更");
        if (change.status() != ContractChange.Status.DRAFT && change.status() != ContractChange.Status.SUBMITTED) throw conflict("当前状态不可取消合同变更");
        if (!changes.transition(actor.tenantId(), changeId, change.status(), ContractChange.Status.CANCELLED, command.version(), null, actor.userId())) {
            throw conflict("合同变更已被其他操作修改");
        }
        ContractChange after = change(actor, contractId, changeId); journalChange(actor, "CANCEL", after, "ContractChangeCancelled"); return after;
    }

    @Transactional
    public ContractChange approveChange(Actor actor, long contractId, long changeId, ContractCommands.Versioned command) {
        require(actor, "contract:approve-change");
        ContractChange change = change(actor, contractId, changeId);
        Contract contract = existing(actor, contractId);
        if (contract.status() != Contract.Status.SIGNED) throw new DomainException(ErrorCode.VALIDATION_ERROR, "已作废合同不可批准变更");
        if (!changes.transition(actor.tenantId(), changeId, ContractChange.Status.SUBMITTED, ContractChange.Status.APPROVED, command.version(), null, actor.userId())) {
            throw conflict("合同变更已被其他操作修改，或当前状态不可批准");
        }
        ContractChange after = change(actor, contractId, changeId); journalChange(actor, "APPROVE", after, "ContractChangeApproved"); return after;
    }

    @Transactional
    public ContractChange rejectChange(Actor actor, long contractId, long changeId, ContractCommands.RejectChange command) {
        require(actor, "contract:approve-change");
        change(actor, contractId, changeId);
        String reason = required(command.reason(), "驳回原因");
        if (!changes.transition(actor.tenantId(), changeId, ContractChange.Status.SUBMITTED, ContractChange.Status.REJECTED, command.changeVersion(), reason, actor.userId())) {
            throw conflict("合同变更已被其他操作修改，或当前状态不可驳回");
        }
        ContractChange after = change(actor, contractId, changeId); journalChange(actor, "REJECT", after, "ContractChangeRejected"); return after;
    }

    private Contract existing(Actor actor, long id) {
        return repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "合同不存在"));
    }

    private ContractChange change(Actor actor, long contractId, long changeId) {
        ContractChange value = changes.find(actor.tenantId(), changeId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "合同变更不存在"));
        if (value.contractId() != contractId) throw new DomainException(ErrorCode.NOT_FOUND, "合同变更不存在");
        return value;
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
        String operationAction = "SUBMIT_SIGNATURE".equals(action) ? "SUBMIT" : action;
        String operation = "contract:" + operationAction + ":" + contract.id() + ":" + ids.nextId(); String data = json(contract);
        audit.record(actor, action, "CONTRACT", contract.id(), operation, "API", null, "{}", data);
        outbox.append(new DomainEvent(event, "CONTRACT", contract.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId());
    }
    private void journalChange(Actor actor, String action, ContractChange change, String event) {
        String operation = "chg:" + action + ":" + change.id() + ":" + ids.nextId(); String data = json(change);
        audit.record(actor, action, "CONTRACT_CHANGE", change.id(), operation, "API", null, "{}", data);
        outbox.append(new DomainEvent(event, "CONTRACT_CHANGE", change.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId());
    }
    private String snapshot(Contract contract, String proposedName, java.time.LocalDate proposedFrom, java.time.LocalDate proposedTo) {
        ObjectNode root = mapper.createObjectNode();
        root.put("contractId", contract.id()); root.put("contractVersion", contract.version());
        root.put("name", proposedName == null ? contract.name() : proposedName);
        if (proposedFrom == null ? contract.effectiveFrom() == null : false) root.putNull("effectiveFrom");
        else root.put("effectiveFrom", proposedFrom == null ? contract.effectiveFrom().toString() : proposedFrom.toString());
        if (proposedTo == null ? contract.effectiveTo() == null : false) root.putNull("effectiveTo");
        else root.put("effectiveTo", proposedTo == null ? contract.effectiveTo().toString() : proposedTo.toString());
        root.set("lines", mapper.valueToTree(repository.findLines(contract.tenantId(), contract.id())));
        return root.toString();
    }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException exception) { throw new IllegalStateException("无法序列化合同", exception); } }
}
