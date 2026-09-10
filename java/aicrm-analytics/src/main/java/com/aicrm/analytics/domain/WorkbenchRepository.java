package com.aicrm.analytics.domain;

import com.aicrm.kernel.security.Actor;

import java.time.Instant;
import java.time.LocalDate;

/** Read-only port for CRM workbench statistics. */
public interface WorkbenchRepository {
    WorkbenchSummary summary(Actor actor, LocalDate from, LocalDate to, Instant startInclusive, Instant endExclusive);
}
