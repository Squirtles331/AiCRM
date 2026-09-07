package com.aicrm.platform.application;

import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.TraceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/** Database outbox. A later integration worker publishes unpublished events to RabbitMQ. */
@Service
public class OutboxService {
    private final JdbcTemplate jdbcTemplate;
    private final IdGenerator idGenerator;

    public OutboxService(JdbcTemplate jdbcTemplate, IdGenerator idGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.idGenerator = idGenerator;
    }

    public void append(DomainEvent event, String operationId, long actorId) {
        jdbcTemplate.update("insert into crm_outbox_event (id, tenant_id, aggregate_type, aggregate_id, event_type, payload, "
                        + "operation_id, trace_id, occurred_at, created_by, updated_by) values (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?)",
                idGenerator.nextId(), event.tenantId(), event.aggregateType(), event.aggregateId(),
                event.eventType(), event.payload(), operationId, TraceContext.get(), event.occurredAt(), actorId, actorId);
    }
}
