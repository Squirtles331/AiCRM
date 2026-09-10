package com.aicrm.analytics.report.application;

import com.aicrm.analytics.report.domain.SalesFunnelSnapshot;
import com.aicrm.analytics.report.domain.SalesPerformanceReport;
import com.aicrm.analytics.report.domain.SalesReportRepository;
import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class SalesReportQueryService {
    private final SalesReportRepository repository;
    private final Clock clock;

    @Autowired
    public SalesReportQueryService(SalesReportRepository repository) {
        this(repository, Clock.systemUTC());
    }

    SalesReportQueryService(SalesReportRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public SalesFunnelSnapshot funnel(Actor actor) {
        requireAnalyticsRead(actor);
        return repository.funnel(actor, Instant.now(clock));
    }

    public SalesPerformanceReport performance(Actor actor, LocalDate requestedFrom, LocalDate requestedTo) {
        requireAnalyticsRead(actor);
        LocalDate defaultFrom = LocalDate.now(clock.withZone(ZoneOffset.UTC)).withDayOfMonth(1);
        LocalDate from = requestedFrom == null ? defaultFrom : requestedFrom;
        LocalDate to = requestedTo == null ? (requestedFrom == null ? defaultFrom.plusMonths(1) : from.plusMonths(1)) : requestedTo;
        if (!to.isAfter(from)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "统计结束日期必须晚于开始日期");
        }
        if (from.plusDays(366).isBefore(to)) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "统计时间范围不能超过 366 天");
        }
        return repository.performance(actor, from, to, from.atStartOfDay().toInstant(ZoneOffset.UTC), to.atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    private void requireAnalyticsRead(Actor actor) {
        if (!actor.hasPermission("analytics:read")) {
            throw new DomainException(ErrorCode.FORBIDDEN, "缺少权限：analytics:read");
        }
    }
}
