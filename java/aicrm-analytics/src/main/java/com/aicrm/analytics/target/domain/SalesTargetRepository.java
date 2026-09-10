package com.aicrm.analytics.target.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface SalesTargetRepository {
    SalesTarget insert(SalesTarget target, long actorId);
    Optional<SalesTarget> find(long tenantId, long targetId);
    Optional<SalesTargetResult> findResult(long tenantId, long targetId);
    boolean userExists(long tenantId, long userId);
    boolean activate(long tenantId, long targetId, long version, long actorId);
    boolean confirm(long tenantId, long targetId, long version, long actorId);
    SalesTargetResult insertResult(SalesTargetResult result, long tenantId, long actorId);
    BigDecimal actualValue(SalesTarget target);
}
