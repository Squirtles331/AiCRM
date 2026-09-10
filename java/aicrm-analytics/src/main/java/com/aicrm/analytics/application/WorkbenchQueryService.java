package com.aicrm.analytics.application;

import com.aicrm.analytics.domain.WorkbenchRepository;
import com.aicrm.analytics.domain.WorkbenchSummary;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/** Application service for CRM-only workbench reads. */
@Service
public class WorkbenchQueryService {
    private final WorkbenchRepository repository;
    private final Clock clock;

    public WorkbenchQueryService(WorkbenchRepository repository) {
        this(repository, Clock.systemUTC());
    }

    WorkbenchQueryService(WorkbenchRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public WorkbenchSummary summary(Actor actor, LocalDate requestedFrom, LocalDate requestedTo) {
        require(actor, "analytics:read");
        LocalDate defaultFrom = LocalDate.now(clock.withZone(ZoneOffset.UTC)).withDayOfMonth(1);
        LocalDate from = requestedFrom == null ? defaultFrom : requestedFrom;
        LocalDate to = requestedTo == null ? (requestedFrom == null ? defaultFrom.plusMonths(1) : from.plusMonths(1)) : requestedTo;
        if (!to.isAfter(from)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "统计结束日期必须晚于开始日期");
        }
        if (from.plusDays(366).isBefore(to)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "统计时间范围不能超过 366 天");
        }
        Instant start = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = to.atStartOfDay().toInstant(ZoneOffset.UTC);
        return repository.summary(actor, from, to, start, end);
    }

    private void require(Actor actor, String permission) {
        if (!actor.hasPermission(permission)) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：" + permission);
        }
    }
}
