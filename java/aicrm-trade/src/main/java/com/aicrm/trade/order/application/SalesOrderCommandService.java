package com.aicrm.trade.order.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.trade.contract.application.ContractReadService;
import com.aicrm.trade.order.domain.SalesOrder;
import com.aicrm.trade.order.domain.SalesOrderLine;
import com.aicrm.trade.order.domain.OrderCancel;
import com.aicrm.trade.order.domain.OrderCancelRepository;
import com.aicrm.trade.order.domain.SalesOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SalesOrderCommandService {
    private final SalesOrderRepository repository;
    private final OrderCancelRepository cancellations;
    private final ContractReadService contracts;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;
    public SalesOrderCommandService(SalesOrderRepository repository, OrderCancelRepository cancellations, ContractReadService contracts, IdGenerator ids, IdempotencyService idempotency,
                                    AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.repository = repository; this.cancellations = cancellations; this.contracts = contracts; this.ids = ids; this.idempotency = idempotency; this.audit = audit; this.outbox = outbox; this.mapper = mapper;
    }
    @Transactional
    public SalesOrder create(Actor actor, SalesOrderCommands.Create command, String key) {
        require(actor, "order:create");
        return idempotency.execute(actor, "order:create", key, command, SalesOrder.class, () -> {
            ContractReadService.SignedOrderSource source = contracts.signedOrderSource(actor, command.contractId());
            if (repository.existsForContract(actor.tenantId(), source.contractId())) throw conflict("该合同已生成销售订单");
            Instant now = Instant.now();
            SalesOrder order = new SalesOrder(ids.nextId(), actor.tenantId(), "ORD-" + ids.nextId(), source.contractId(), source.customerId(), null,
                    source.currency(), source.totalAmount(), SalesOrder.Status.DRAFT, command.expectedDeliveryAt(), null, null, null, null, 0, now, now);
            try {
                SalesOrder created = repository.insert(order, actor.userId());
                for (ContractReadService.OrderSourceLine line : source.lines()) repository.insertLine(line(actor, created.id(), line, now), actor.userId());
                journal(actor, created); return created;
            } catch (DuplicateKeyException exception) { throw conflict("该合同已生成销售订单"); }
        });
    }

    @Transactional
    public SalesOrder confirm(Actor actor, long orderId, SalesOrderCommands.Versioned command) {
        require(actor, "order:confirm");
        existing(actor, orderId);
        if (!repository.transition(actor.tenantId(), orderId, SalesOrder.Status.DRAFT, SalesOrder.Status.CONFIRMED, command.version(), actor.userId())) {
            throw conflict("销售订单已被其他操作修改，或当前状态不可确认");
        }
        SalesOrder after = existing(actor, orderId); journal(actor, "CONFIRM", after, "OrderConfirmed"); return after;
    }

    @Transactional
    public OrderCancel requestCancel(Actor actor, long orderId, SalesOrderCommands.Reasoned command, String key) {
        require(actor, "order:cancel");
        return idempotency.execute(actor, "order:request-cancel", key, command, OrderCancel.class, () -> {
            SalesOrder order = existing(actor, orderId);
            if (order.status() != SalesOrder.Status.CONFIRMED || order.version() != command.version()) throw conflict("仅已确认且版本匹配的订单可以申请取消");
            String reason = required(command.reason(), "取消原因");
            Instant now = Instant.now();
            OrderCancel cancellation = new OrderCancel(ids.nextId(), actor.tenantId(), orderId, "CAN-" + ids.nextId(), OrderCancel.Status.PENDING,
                    reason, json(order), now, null, null, null, null, 0, actor.userId(), now, now);
            try {
                OrderCancel created = cancellations.insert(cancellation, actor.userId());
                if (!repository.transition(actor.tenantId(), orderId, SalesOrder.Status.CONFIRMED, SalesOrder.Status.CANCELLING, command.version(), actor.userId())) {
                    throw conflict("销售订单已被其他操作修改");
                }
                SalesOrder after = existing(actor, orderId);
                journal(actor, "REQUEST_CANCEL", after, "OrderCancellationRequested");
                return created;
            } catch (DuplicateKeyException exception) { throw conflict("该订单已有未结束的取消申请"); }
        });
    }

    @Transactional
    public SalesOrder approveCancel(Actor actor, long orderId, long cancellationId, SalesOrderCommands.CancelDecision command) {
        require(actor, "order:approve-cancel");
        OrderCancel cancellation = cancellation(actor, orderId, cancellationId);
        SalesOrder order = existing(actor, orderId);
        if (!repository.transition(actor.tenantId(), orderId, SalesOrder.Status.CANCELLING, SalesOrder.Status.CANCELLED, command.orderVersion(), actor.userId())) {
            throw conflict("销售订单已被其他操作修改，或当前状态不可取消");
        }
        if (!cancellations.transition(actor.tenantId(), cancellationId, OrderCancel.Status.PENDING, OrderCancel.Status.COMPLETED,
                command.cancellationVersion(), null, actor.userId())) throw conflict("取消申请已被其他操作修改");
        SalesOrder after = existing(actor, orderId); journal(actor, "CANCEL", after, "OrderCancelled"); return after;
    }

    @Transactional
    public SalesOrder rejectCancel(Actor actor, long orderId, long cancellationId, SalesOrderCommands.CancelDecision command) {
        require(actor, "order:approve-cancel");
        cancellation(actor, orderId, cancellationId);
        existing(actor, orderId);
        String reason = required(command.reason(), "驳回原因");
        if (!repository.transition(actor.tenantId(), orderId, SalesOrder.Status.CANCELLING, SalesOrder.Status.CONFIRMED, command.orderVersion(), actor.userId())) {
            throw conflict("销售订单已被其他操作修改，或当前状态不可驳回取消");
        }
        if (!cancellations.transition(actor.tenantId(), cancellationId, OrderCancel.Status.PENDING, OrderCancel.Status.REJECTED,
                command.cancellationVersion(), reason, actor.userId())) throw conflict("取消申请已被其他操作修改");
        SalesOrder after = existing(actor, orderId); journal(actor, "REJECT_CANCEL", after, "OrderCancellationRejected"); return after;
    }

    @Transactional
    public SalesOrder close(Actor actor, long orderId, SalesOrderCommands.Reasoned command) {
        require(actor, "order:close");
        existing(actor, orderId);
        String reason = required(command.reason(), "关闭原因");
        if (!repository.close(actor.tenantId(), orderId, command.version(), reason, actor.userId())) {
            throw conflict("仅已确认且版本匹配的订单可以关闭");
        }
        SalesOrder after = existing(actor, orderId); journal(actor, "CLOSE", after, "OrderClosed"); return after;
    }

    public OrderCancel cancellation(Actor actor, long orderId, long cancellationId) {
        OrderCancel cancellation = cancellations.find(actor.tenantId(), cancellationId).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "取消申请不存在"));
        if (cancellation.orderId() != orderId) throw new DomainException(ErrorCode.NOT_FOUND, "取消申请不存在");
        return cancellation;
    }

    private SalesOrder existing(Actor actor, long id) {
        return repository.find(actor.tenantId(), id).orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, "销售订单不存在"));
    }
    private SalesOrderLine line(Actor actor, long orderId, ContractReadService.OrderSourceLine source, Instant now) { return new SalesOrderLine(ids.nextId(), actor.tenantId(), orderId, source.lineNo(), source.contractLineId(), source.productId(), source.productNo(), source.sku(), source.productName(), source.unit(), source.quantity(), source.unitPrice(), source.taxRate(), source.lineAmount(), now, now); }
    private void require(Actor actor, String permission) { if (!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission); }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT, message); }
    private void journal(Actor actor, SalesOrder order) { String operation = "order:CREATE:" + order.id() + ":" + ids.nextId(); String data = json(order); audit.record(actor, "CREATE", "ORDER", order.id(), operation, "API", null, "{}", data); outbox.append(new DomainEvent("OrderCreated", "ORDER", order.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId()); }
    private void journal(Actor actor, String action, SalesOrder order, String event) { String operation = "order:" + action + ":" + order.id() + ":" + ids.nextId(); String data = json(order); audit.record(actor, action, "ORDER", order.id(), operation, "API", null, "{}", data); outbox.append(new DomainEvent(event, "ORDER", order.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId()); }
    private String required(String value, String name) { if (value == null || value.isBlank()) throw new DomainException(ErrorCode.VALIDATION_ERROR, name + "不能为空"); return value.trim(); }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException exception) { throw new IllegalStateException("无法序列化销售订单", exception); } }
}
