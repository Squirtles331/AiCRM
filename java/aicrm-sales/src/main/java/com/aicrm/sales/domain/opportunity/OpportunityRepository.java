package com.aicrm.sales.domain.opportunity;

import com.aicrm.kernel.page.PageResult;
import com.aicrm.kernel.security.Actor;

import java.util.List;
import java.util.Optional;

/** Persistence port for the opportunity aggregate and its append-only stage history. */
public interface OpportunityRepository {
    Opportunity insert(Opportunity opportunity, long actorId);
    Optional<Opportunity> find(long tenantId, long opportunityId);
    PageResult<Opportunity> page(Actor actor, long page, long size);
    boolean changeStage(long tenantId, long opportunityId, long expectedVersion, Opportunity.Stage stage,
                        short probability, long actorId);
    boolean win(long tenantId, long opportunityId, long expectedVersion, long actorId);
    boolean lose(long tenantId, long opportunityId, long expectedVersion, String reason, long actorId);
    boolean restart(long tenantId, long opportunityId, long expectedVersion, long actorId);
    void appendHistory(long id, long tenantId, long opportunityId, OpportunityStageHistory.Action action,
                       Opportunity before, Opportunity after, String reason, long actorId);
    List<OpportunityStageHistory> histories(long tenantId, long opportunityId);
}
