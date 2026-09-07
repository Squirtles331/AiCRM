ALTER TABLE crm_idempotency_record DROP CONSTRAINT crm_idempotency_record_status_check;
ALTER TABLE crm_idempotency_record
    ADD COLUMN request_hash VARCHAR(64),
    ADD COLUMN http_status INT,
    ADD COLUMN error_code VARCHAR(100),
    ADD COLUMN error_message VARCHAR(1000),
    ADD COLUMN attempt_count INT NOT NULL DEFAULT 1,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN failed_at TIMESTAMPTZ,
    ADD COLUMN expires_at TIMESTAMPTZ,
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT,
    ADD CONSTRAINT ck_crm_idempotency_status
        CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    ADD CONSTRAINT ck_crm_idempotency_attempt_count CHECK (attempt_count > 0),
    ADD CONSTRAINT ck_crm_idempotency_result CHECK (
        (status = 'PROCESSING' AND completed_at IS NULL AND failed_at IS NULL)
        OR (status = 'COMPLETED' AND completed_at IS NOT NULL AND failed_at IS NULL)
        OR (status = 'FAILED' AND failed_at IS NOT NULL AND completed_at IS NULL)
    );
CREATE INDEX idx_crm_idempotency_expiry
    ON crm_idempotency_record(expires_at) WHERE expires_at IS NOT NULL;

ALTER TABLE crm_audit_log
    ADD COLUMN operation_id VARCHAR(64),
    ADD COLUMN source VARCHAR(32) NOT NULL DEFAULT 'API',
    ADD COLUMN batch_no VARCHAR(64),
    ADD COLUMN client_ip INET,
    ADD COLUMN user_agent VARCHAR(500),
    ADD COLUMN result VARCHAR(16) NOT NULL DEFAULT 'SUCCESS',
    ADD COLUMN error_code VARCHAR(100),
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT,
    ADD CONSTRAINT ck_crm_audit_source
        CHECK (source IN ('API', 'IMPORT', 'SYSTEM', 'SCHEDULER', 'CALLBACK')),
    ADD CONSTRAINT ck_crm_audit_result CHECK (result IN ('SUCCESS', 'FAILURE'));
CREATE INDEX idx_crm_audit_operation
    ON crm_audit_log(tenant_id, operation_id) WHERE operation_id IS NOT NULL;
CREATE INDEX idx_crm_audit_trace
    ON crm_audit_log(tenant_id, trace_id) WHERE trace_id IS NOT NULL;

ALTER TABLE crm_outbox_event
    ADD COLUMN event_version INT NOT NULL DEFAULT 1,
    ADD COLUMN operation_id VARCHAR(64),
    ADD COLUMN trace_id VARCHAR(64),
    ADD COLUMN status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN available_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN next_retry_at TIMESTAMPTZ,
    ADD COLUMN locked_at TIMESTAMPTZ,
    ADD COLUMN locked_by VARCHAR(100),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN dead_lettered_at TIMESTAMPTZ,
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT,
    ADD CONSTRAINT ck_crm_outbox_status CHECK (status IN ('PENDING', 'PUBLISHING', 'PUBLISHED', 'FAILED', 'DEAD')),
    ADD CONSTRAINT ck_crm_outbox_retry_count CHECK (retry_count >= 0),
    ADD CONSTRAINT ck_crm_outbox_version CHECK (event_version > 0),
    ADD CONSTRAINT ck_crm_outbox_publish_shape CHECK (
        (status = 'PUBLISHED' AND published_at IS NOT NULL)
        OR (status <> 'PUBLISHED')
    );
UPDATE crm_outbox_event SET status = 'PUBLISHED' WHERE published_at IS NOT NULL;
DROP INDEX idx_crm_outbox_unpublished;
CREATE INDEX idx_crm_outbox_dispatch
    ON crm_outbox_event(available_at, created_at, id)
    WHERE status IN ('PENDING', 'FAILED');
CREATE INDEX idx_crm_outbox_reconcile
    ON crm_outbox_event(tenant_id, status, occurred_at, id);
CREATE UNIQUE INDEX uk_crm_outbox_operation_event
    ON crm_outbox_event(tenant_id, operation_id, event_type, aggregate_type, aggregate_id)
    WHERE operation_id IS NOT NULL;

CREATE TABLE crm_inbox_record (
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    consumer VARCHAR(100) NOT NULL,
    message_id VARCHAR(128) NOT NULL,
    business_key VARCHAR(200),
    status VARCHAR(16) NOT NULL DEFAULT 'PROCESSING',
    payload_hash VARCHAR(64),
    attempt_count INT NOT NULL DEFAULT 1,
    first_received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ,
    failed_at TIMESTAMPTZ,
    last_error VARCHAR(1000),
    created_by BIGINT,
    updated_by BIGINT,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    PRIMARY KEY (tenant_id, consumer, message_id),
    CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
    CHECK (attempt_count > 0)
);
CREATE UNIQUE INDEX uk_crm_inbox_business_key
    ON crm_inbox_record(tenant_id, consumer, business_key)
    WHERE business_key IS NOT NULL;
