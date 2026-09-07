package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

/** Runs a message handler once per tenant and consumer, persisting retries in the Inbox. */
@Service
public class InboxService {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public InboxService(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    public boolean consume(long tenantId, String consumer, String messageId, String businessKey,
                           String payload, Runnable handler) {
        validate(tenantId, consumer, messageId, handler);
        String payloadHash = sha256(payload == null ? "" : payload);
        Boolean claimed = transactionTemplate.execute(status -> claim(
                tenantId, consumer, messageId, businessKey, payloadHash));
        if (!Boolean.TRUE.equals(claimed)) {
            return false;
        }
        try {
            transactionTemplate.executeWithoutResult(status -> {
                handler.run();
                jdbcTemplate.update("update crm_inbox_record set status='COMPLETED', completed_at=now(), failed_at=null, "
                                + "last_error=null, updated_at=now() where tenant_id=? and consumer=? and message_id=?",
                        tenantId, consumer, messageId);
            });
            return true;
        } catch (RuntimeException exception) {
            transactionTemplate.executeWithoutResult(status -> jdbcTemplate.update(
                    "update crm_inbox_record set status='FAILED', failed_at=now(), completed_at=null, "
                            + "last_error=?, updated_at=now() where tenant_id=? and consumer=? and message_id=?",
                    abbreviate(exception), tenantId, consumer, messageId));
            throw exception;
        }
    }

    private boolean claim(long tenantId, String consumer, String messageId, String businessKey, String payloadHash) {
        int inserted = jdbcTemplate.update("insert into crm_inbox_record "
                        + "(tenant_id,consumer,message_id,business_key,payload_hash) values (?,?,?,?,?) "
                        + "on conflict do nothing",
                tenantId, consumer, messageId, businessKey, payloadHash);
        if (inserted == 1) {
            return true;
        }
        String select = "select message_id,status,payload_hash from crm_inbox_record "
                + "where tenant_id=? and consumer=? and ";
        List<InboxRecord> records;
        if (businessKey == null) {
            records = jdbcTemplate.query(select + "message_id=? for update", this::mapRecord,
                    tenantId, consumer, messageId);
        } else {
            records = jdbcTemplate.query(select + "(message_id=? or business_key=?) "
                            + "order by case when message_id=? then 0 else 1 end limit 1 for update",
                    this::mapRecord, tenantId, consumer, messageId, businessKey, messageId);
        }
        if (records.isEmpty()) {
            return false;
        }
        InboxRecord existing = records.get(0);
        if (existing.messageId().equals(messageId) && existing.payloadHash() != null
                && !existing.payloadHash().equals(payloadHash)) {
            throw new DomainException(ErrorCode.CONFLICT, "消息 ID 已用于不同载荷");
        }
        if (!existing.messageId().equals(messageId)) {
            return false;
        }
        return jdbcTemplate.update("update crm_inbox_record set status='PROCESSING', attempt_count=attempt_count+1, "
                        + "failed_at=null, last_error=null, updated_at=now() "
                        + "where tenant_id=? and consumer=? and message_id=? "
                        + "and (status='FAILED' or (status='PROCESSING' and updated_at < now()-interval '5 minutes'))",
                tenantId, consumer, messageId) == 1;
    }

    private void validate(long tenantId, String consumer, String messageId, Runnable handler) {
        if (tenantId <= 0 || consumer == null || consumer.isBlank() || consumer.length() > 100
                || messageId == null || messageId.isBlank() || messageId.length() > 128 || handler == null) {
            throw new IllegalArgumentException("invalid inbox arguments");
        }
    }

    private String sha256(String payload) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String abbreviate(RuntimeException exception) {
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        return message.length() <= 1000 ? message : message.substring(0, 1000);
    }

    private InboxRecord mapRecord(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new InboxRecord(rs.getString("message_id"), rs.getString("status"), rs.getString("payload_hash"));
    }

    private record InboxRecord(String messageId, String status, String payloadHash) {
    }
}
