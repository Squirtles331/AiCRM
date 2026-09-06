package com.aicrm.platform.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.security.Actor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public <T> T execute(Actor actor, String operation, String key, Class<T> responseType, Supplier<T> command) {
        if (key == null || key.isBlank() || key.length() > 128) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "Idempotency-Key 必填且长度不能超过 128");
        }
        try {
            jdbcTemplate.update("insert into crm_idempotency_record (tenant_id, operation, idempotency_key, status) "
                    + "values (?, ?, ?, 'PROCESSING')", actor.tenantId(), operation, key);
        } catch (DuplicateKeyException duplicate) {
            String response = jdbcTemplate.query(
                    "select response_body::text from crm_idempotency_record "
                            + "where tenant_id = ? and operation = ? and idempotency_key = ? and status = 'COMPLETED'",
                    rs -> rs.next() ? rs.getString(1) : null, actor.tenantId(), operation, key);
            if (response == null) {
                throw new DomainException(ErrorCode.IDEMPOTENCY_IN_PROGRESS, "相同请求正在处理");
            }
            try {
                return objectMapper.readValue(response, responseType);
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
}
