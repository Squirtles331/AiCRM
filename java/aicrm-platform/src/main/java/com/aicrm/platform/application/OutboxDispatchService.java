package com.aicrm.platform.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/** Claims outbox rows using a database lease and publishes them with at-least-once delivery. */
@Service
public class OutboxDispatchService {
    private static final List<Duration> RETRY_DELAYS = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofHours(2), Duration.ofHours(12));

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final OutboxEventPublisher eventPublisher;

    public OutboxDispatchService(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate,
                                 OutboxEventPublisher eventPublisher) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
    }

    public DispatchResult dispatchBatch(String workerId, int batchSize, Duration leaseDuration) {
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("workerId must not be blank");
        }
        if (batchSize < 1 || batchSize > 500) {
            throw new IllegalArgumentException("batchSize must be between 1 and 500");
        }
        List<OutboxMessage> claimed = transactionTemplate.execute(status -> claim(workerId, batchSize, leaseDuration));
        int published = 0;
        int failed = 0;
        int dead = 0;
        for (OutboxMessage message : claimed == null ? List.<OutboxMessage>of() : claimed) {
            try {
                eventPublisher.publish(message);
                transactionTemplate.executeWithoutResult(status -> markPublished(message.id(), workerId));
                published++;
            } catch (RuntimeException exception) {
                boolean deadLettered = transactionTemplate.execute(status -> markFailed(message, workerId, exception));
                if (deadLettered) {
                    dead++;
                } else {
                    failed++;
                }
            }
        }
        return new DispatchResult(claimed == null ? 0 : claimed.size(), published, failed, dead);
    }

    private List<OutboxMessage> claim(String workerId, int batchSize, Duration leaseDuration) {
        Instant staleBefore = Instant.now().minus(leaseDuration == null ? Duration.ofMinutes(5) : leaseDuration);
        return jdbcTemplate.query("""
                with candidates as (
                    select id from crm_outbox_event
                    where deleted_at is null and (
                        (status = 'PENDING' and available_at <= now())
                        or (status = 'FAILED' and coalesce(next_retry_at, available_at) <= now())
                        or (status = 'PUBLISHING' and locked_at < ?)
                    )
                    order by available_at, created_at, id
                    for update skip locked limit ?
                )
                update crm_outbox_event event
                set status = 'PUBLISHING', locked_by = ?, locked_at = now(), updated_at = now()
                from candidates where event.id = candidates.id
                returning event.*
                """, this::mapMessage, Timestamp.from(staleBefore), batchSize, workerId);
    }

    private void markPublished(long eventId, String workerId) {
        jdbcTemplate.update("update crm_outbox_event set status='PUBLISHED', published_at=now(), next_retry_at=null, "
                        + "locked_by=null, locked_at=null, last_error=null, updated_at=now() "
                        + "where id=? and status='PUBLISHING' and locked_by=?",
                eventId, workerId);
    }

    private boolean markFailed(OutboxMessage message, String workerId, RuntimeException exception) {
        int attempt = message.retryCount() + 1;
        boolean dead = attempt > RETRY_DELAYS.size();
        Instant nextRetryAt = dead ? null : Instant.now().plus(RETRY_DELAYS.get(attempt - 1));
        String error = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        if (error.length() > 1000) {
            error = error.substring(0, 1000);
        }
        jdbcTemplate.update("update crm_outbox_event set status=?, retry_count=?, last_error=?, next_retry_at=?, "
                        + "dead_lettered_at=?, locked_by=null, locked_at=null, updated_at=now() "
                        + "where id=? and status='PUBLISHING' and locked_by=?",
                dead ? "DEAD" : "FAILED", attempt, error,
                nextRetryAt == null ? null : Timestamp.from(nextRetryAt), dead ? Timestamp.from(Instant.now()) : null,
                message.id(), workerId);
        return dead;
    }

    private OutboxMessage mapMessage(ResultSet rs, int rowNum) throws SQLException {
        return new OutboxMessage(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("aggregate_type"),
                rs.getLong("aggregate_id"), rs.getString("event_type"), rs.getInt("event_version"),
                rs.getString("operation_id"), rs.getString("trace_id"), rs.getString("payload"),
                rs.getTimestamp("occurred_at").toInstant(), rs.getInt("retry_count"));
    }

    public record DispatchResult(int claimed, int published, int failed, int dead) {
    }
}
