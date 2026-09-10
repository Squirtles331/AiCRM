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
    private final ContractReadService contracts;
    private final IdGenerator ids;
    private final IdempotencyService idempotency;
    private final AuditLogService audit;
    private final OutboxService outbox;
    private final ObjectMapper mapper;
    public SalesOrderCommandService(SalesOrderRepository repository, ContractReadService contracts, IdGenerator ids, IdempotencyService idempotency,
                                    AuditLogService audit, OutboxService outbox, ObjectMapper mapper) {
        this.repository = repository; this.contracts = contracts; this.ids = ids; this.idempotency = idempotency; this.audit = audit; this.outbox = outbox; this.mapper = mapper;
    }
    @Transactional
    public SalesOrder create(Actor actor, SalesOrderCommands.Create command, String key) {
        require(actor, "order:create");
        return idempotency.execute(actor, "order:create", key, command, SalesOrder.class, () -> {
            ContractReadService.SignedOrderSource source = contracts.signedOrderSource(actor, command.contractId());
            if (repository.existsForContract(actor.tenantId(), source.contractId())) throw conflict("该合同已生成销售订单");
            Instant now = Instant.now();
            SalesOrder order = new SalesOrder(ids.nextId(), actor.tenantId(), "ORD-" + ids.nextId(), source.contractId(), source.customerId(), null,
                    source.currency(), source.totalAmount(), SalesOrder.Status.DRAFT, command.expectedDeliveryAt(), null, null, null, 0, now, now);
            try {
                SalesOrder created = repository.insert(order, actor.userId());
                for (ContractReadService.OrderSourceLine line : source.lines()) repository.insertLine(line(actor, created.id(), line, now), actor.userId());
                journal(actor, created); return created;
            } catch (DuplicateKeyException exception) { throw conflict("该合同已生成销售订单"); }
        });
    }
    private SalesOrderLine line(Actor actor, long orderId, ContractReadService.OrderSourceLine source, Instant now) { return new SalesOrderLine(ids.nextId(), actor.tenantId(), orderId, source.lineNo(), source.contractLineId(), source.productId(), source.productNo(), source.sku(), source.productName(), source.unit(), source.quantity(), source.unitPrice(), source.taxRate(), source.lineAmount(), now, now); }
    private void require(Actor actor, String permission) { if (!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission); }
    private DomainException conflict(String message) { return new DomainException(ErrorCode.CONFLICT, message); }
    private void journal(Actor actor, SalesOrder order) { String operation = "order:CREATE:" + order.id() + ":" + ids.nextId(); String data = json(order); audit.record(actor, "CREATE", "ORDER", order.id(), operation, "API", null, "{}", data); outbox.append(new DomainEvent("OrderCreated", "ORDER", order.id(), actor.tenantId(), data, Instant.now()), operation, actor.userId()); }
    private String json(Object value) { try { return mapper.writeValueAsString(value); } catch (JsonProcessingException exception) { throw new IllegalStateException("无法序列化销售订单", exception); } }
}
