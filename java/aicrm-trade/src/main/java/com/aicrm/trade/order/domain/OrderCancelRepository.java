package com.aicrm.trade.order.domain;

import java.util.Optional;
import java.util.List;

public interface OrderCancelRepository {
    OrderCancel insert(OrderCancel cancellation, long actorId);
    Optional<OrderCancel> find(long tenantId, long cancellationId);
    List<OrderCancel> findByOrder(long tenantId, long orderId);
    boolean transition(long tenantId, long cancellationId, OrderCancel.Status from, OrderCancel.Status to,
                       long expectedVersion, String rejectionReason, long actorId);
}
