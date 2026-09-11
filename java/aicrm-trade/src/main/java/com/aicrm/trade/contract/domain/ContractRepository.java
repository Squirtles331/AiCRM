package com.aicrm.trade.contract.domain;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;
import java.util.List;
import java.util.Optional;

public interface ContractRepository {
    Contract insert(Contract contract, long actorId);
    void insertLine(ContractLine line, long actorId);
    Optional<Contract> find(long tenantId, long contractId);
    PageResult<Contract> page(Actor actor, long page, long size);
    List<ContractLine> findLines(long tenantId, long contractId);
    boolean existsForQuoteVersion(long tenantId, long quoteId, long quoteVersionId);
    boolean transition(long tenantId, long contractId, Contract.Status from, Contract.Status to,
                       long expectedVersion, long actorId);
    boolean voidContract(long tenantId, long contractId, Contract.Status from, long expectedVersion, String reason, long actorId);
    boolean hasNonCancelledOrder(long tenantId, long contractId);
}
