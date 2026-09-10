package com.aicrm.integration.infrastructure;

import com.aicrm.integration.domain.Connector;
import com.aicrm.integration.domain.ConnectorEvent;
import com.aicrm.integration.domain.ConnectorMonitoring;
import com.aicrm.integration.domain.ConnectorRepository;
import com.aicrm.integration.domain.ConnectorStatus;
import com.aicrm.integration.domain.ConnectorType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class JdbcConnectorRepository implements ConnectorRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcConnectorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Connector> find(long tenantId, long connectorId) {
        return queryConnector("where tenant_id = ? and id = ? and deleted_at is null", tenantId, connectorId);
    }

    @Override
    public Optional<Connector> findAnyTenant(long connectorId) {
        return queryConnector("where id = ? and deleted_at is null", connectorId);
    }

    @Override
    public Connector insert(Connector connector, long actorUserId) {
        jdbcTemplate.update("insert into crm_connector (id, tenant_id, connector_no, name, type, status, operator_user_id, "
                        + "public_pool_id, secret_hash, version, created_by, updated_by, created_at, updated_at) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                connector.id(), connector.tenantId(), connector.connectorNo(), connector.name(), connector.type().name(),
                connector.status().name(), connector.operatorUserId(), connector.publicPoolId(), connector.secretHash(),
                connector.version(), actorUserId, actorUserId, Timestamp.from(connector.createdAt()), Timestamp.from(connector.updatedAt()));
        return connector;
    }

    @Override
    public boolean updateStatus(long tenantId, long connectorId, long version, ConnectorStatus status, long actorUserId) {
        return jdbcTemplate.update("update crm_connector set status = ?, version = version + 1, updated_by = ?, updated_at = now() "
                        + "where tenant_id = ? and id = ? and version = ? and deleted_at is null",
                status.name(), actorUserId, tenantId, connectorId, version) == 1;
    }

    @Override
    public Optional<ConnectorEvent> findEvent(long tenantId, long connectorId, String externalEventId) {
        var events = jdbcTemplate.query("select id, tenant_id, connector_id, external_event_id, event_type, payload::text, "
                        + "payload_hash, outcome, lead_id, received_at from crm_connector_event "
                        + "where tenant_id = ? and connector_id = ? and external_event_id = ?",
                (rs, rowNum) -> new ConnectorEvent(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), ConnectorEvent.Outcome.valueOf(rs.getString(8)),
                        rs.getObject(9, Long.class), instant(rs.getTimestamp(10))),
                tenantId, connectorId, externalEventId);
        return events.stream().findFirst();
    }

    @Override
    public boolean insertEvent(ConnectorEvent event, long actorUserId) {
        return jdbcTemplate.update("insert into crm_connector_event (id, tenant_id, connector_id, external_event_id, event_type, payload, "
                        + "payload_hash, outcome, lead_id, received_at, created_by) values (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?) "
                        + "on conflict (tenant_id, connector_id, external_event_id) do nothing",
                event.id(), event.tenantId(), event.connectorId(), event.externalEventId(), event.eventType(), event.payload(),
                event.payloadHash(), event.outcome().name(), event.leadId(), Timestamp.from(event.receivedAt()), actorUserId) == 1;
    }

    @Override
    public ConnectorMonitoring monitoring(long tenantId, long connectorId, Instant from, Instant to) {
        ConnectorMonitoring summary = jdbcTemplate.query("""
                select count(*) as received,
                       count(*) filter (where e.outcome='LEAD_CREATED') as leads_created,
                       count(*) filter (where e.outcome='ACCEPTED') as accepted,
                       count(*) filter (where o.status in ('PENDING','PUBLISHING','FAILED')) as pending_publications,
                       count(*) filter (where o.status='PUBLISHED') as published_publications,
                       count(*) filter (where o.status='DEAD') as dead_publications
                from crm_connector_event e
                left join crm_outbox_event o on o.tenant_id=e.tenant_id and o.aggregate_type='CONNECTOR_EVENT'
                    and o.aggregate_id=e.id and o.deleted_at is null
                where e.tenant_id=? and e.connector_id=? and e.received_at>=? and e.received_at<?
                """, rs -> rs.next() ? new ConnectorMonitoring(rs.getLong(1), rs.getLong(2), rs.getLong(3),
                        rs.getLong(4), rs.getLong(5), rs.getLong(6), null) : new ConnectorMonitoring(0, 0, 0, 0, 0, 0, null),
                tenantId, connectorId, Timestamp.from(from), Timestamp.from(to));
        var issues = jdbcTemplate.query("""
                select o.id, o.status, o.retry_count, o.last_error, o.updated_at
                from crm_outbox_event o join crm_connector_event e
                  on e.tenant_id=o.tenant_id and e.id=o.aggregate_id
                where o.tenant_id=? and e.connector_id=? and o.aggregate_type='CONNECTOR_EVENT'
                  and o.status in ('FAILED','DEAD') and o.deleted_at is null
                  and e.received_at>=? and e.received_at<?
                order by o.updated_at desc, o.id desc limit 1
                """, (rs, rowNum) -> new ConnectorMonitoring.DeliveryIssue(rs.getLong(1), rs.getString(2),
                        rs.getInt(3), rs.getString(4), instant(rs.getTimestamp(5))),
                tenantId, connectorId, Timestamp.from(from), Timestamp.from(to));
        return new ConnectorMonitoring(summary.receivedEvents(), summary.leadsCreated(), summary.acceptedEvents(),
                summary.pendingPublications(), summary.publishedPublications(), summary.deadPublications(),
                issues.stream().findFirst().orElse(null));
    }

    @Override
    public boolean ownsConnectorEventOutbox(long tenantId, long connectorId, long outboxEventId) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*) from crm_outbox_event o join crm_connector_event e
                  on e.tenant_id=o.tenant_id and e.id=o.aggregate_id
                where o.tenant_id=? and o.id=? and e.connector_id=? and o.aggregate_type='CONNECTOR_EVENT'
                  and o.deleted_at is null
                """, Integer.class, tenantId, outboxEventId, connectorId);
        return count != null && count == 1;
    }

    private Optional<Connector> queryConnector(String where, Object... arguments) {
        var connectors = jdbcTemplate.query("select id, tenant_id, connector_no, name, type, status, operator_user_id, public_pool_id, "
                        + "secret_hash, version, created_at, updated_at from crm_connector " + where,
                (rs, rowNum) -> new Connector(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
                        ConnectorType.valueOf(rs.getString(5)), ConnectorStatus.valueOf(rs.getString(6)), rs.getLong(7),
                        rs.getObject(8, Long.class), rs.getString(9), rs.getLong(10), instant(rs.getTimestamp(11)),
                        instant(rs.getTimestamp(12))), arguments);
        return connectors.stream().findFirst();
    }

    private static Instant instant(Timestamp value) {
        return value.toInstant();
    }
}
