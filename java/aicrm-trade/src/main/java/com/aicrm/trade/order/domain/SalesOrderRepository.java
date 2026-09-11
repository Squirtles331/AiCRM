package com.aicrm.trade.order.domain;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository {
    SalesOrder insert(SalesOrder order, long actorId);
    void insertLine(SalesOrderLine line, long actorId);
    Optional<SalesOrder> find(long tenantId, long orderId);
    PageResult<SalesOrder> page(Actor actor, long page, long size);
    List<SalesOrderLine> findLines(long tenantId, long orderId);
    boolean existsForContract(long tenantId, long contractId);
    boolean transition(long tenantId, long orderId, SalesOrder.Status from, SalesOrder.Status to, long expectedVersion, long actorId);
    boolean close(long tenantId, long orderId, long expectedVersion, String reason, long actorId);
}
