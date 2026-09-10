package com.aicrm.sales.domain.opportunity;

import java.time.Instant;

/** Immutable audit of pipeline state changes, separate from the general platform audit log. */
public record OpportunityStageHistory(long id, long opportunityId, Action action, Opportunity.Stage fromStage,
                                      Opportunity.Stage toStage, Opportunity.Status fromStatus,
                                      Opportunity.Status toStatus, Short fromProbability, short toProbability,
                                      String reason, long operatorUserId, Instant createdAt) {
    public enum Action { CREATE, STAGE_CHANGED, WON, LOST, RESTARTED }
}
