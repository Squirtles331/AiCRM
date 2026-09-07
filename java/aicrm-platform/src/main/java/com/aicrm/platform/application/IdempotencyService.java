package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.function.Supplier;

/** Executes a command once per tenant, operation and idempotency key. */
@Service
public class IdempotencyService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public IdempotencyService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public <T> T execute(Actor actor, String operation, String key, Object request,
                         Class<T> responseType, Supplier<T> command) {
        if (key == null || key.isBlank() || key.length() > 128) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "Idempotency-Key 必填且长度不能超过 128");
        }
        String requestHash = requestHash(request);
        int inserted = jdbcTemplate.update("insert into crm_idempotency_record "
                        + "(tenant_id, operation, idempotency_key, request_hash, status) values (?, ?, ?, ?, 'PROCESSING') "
                        + "on conflict (tenant_id, operation, idempotency_key) do nothing",
                actor.tenantId(), operation, key, requestHash);
        if (inserted == 0) {
            List<ExistingRequest> records = jdbcTemplate.query(
                    "select status,response_body::text,request_hash from crm_idempotency_record "
                            + "where tenant_id = ? and operation = ? and idempotency_key = ?",
                    (rs, rowNum) -> new ExistingRequest(rs.getString(1), rs.getString(2), rs.getString(3)),
                    actor.tenantId(), operation, key);
            if (records.isEmpty()) {
                throw new DomainException(ErrorCode.IDEMPOTENCY_IN_PROGRESS, "相同请求正在处理");
            }
            ExistingRequest existing = records.get(0);
            if (existing.requestHash() != null && !existing.requestHash().equals(requestHash)) {
                throw new DomainException(ErrorCode.IDEMPOTENCY_KEY_REUSED, "Idempotency-Key 已用于不同请求");
            }
            if (!"COMPLETED".equals(existing.status()) || existing.responseBody() == null) {
                throw new DomainException(ErrorCode.IDEMPOTENCY_IN_PROGRESS, "相同请求正在处理");
            }
            try {
                return objectMapper.readValue(existing.responseBody(), responseType);
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("无法读取已保存的幂等响应", exception);
            }
        }

        T result = command.get();
        try {
            jdbcTemplate.update("update crm_idempotency_record set status = 'COMPLETED', response_body = ?::jsonb, "
                    + "completed_at = now() where tenant_id = ? and operation = ? and idempotency_key = ?",
                    objectMapper.writeValueAsString(result), actor.tenantId(), operation, key);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("无法保存幂等响应", exception);
        }
        return result;
    }

    private String requestHash(Object request) {
        try {
            byte[] canonical = objectMapper.writeValueAsBytes(request);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(canonical));
        } catch (JsonProcessingException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("无法计算幂等请求哈希", exception);
        }
    }

    private record ExistingRequest(String status, String responseBody, String requestHash) {
    }
}
