package com.aicrm.trade.contract.domain;

import java.util.Optional;

public interface ContractChangeRepository {
    ContractChange insert(ContractChange change, long actorId);
    Optional<ContractChange> find(long tenantId, long changeId);
    boolean transition(long tenantId, long changeId, ContractChange.Status from, ContractChange.Status to,
                       long expectedVersion, String rejectionReason, long actorId);
}
