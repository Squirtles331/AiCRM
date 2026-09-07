ALTER TABLE crm_lead
    ADD CONSTRAINT ck_crm_lead_status CHECK (status IN ('NEW', 'FOLLOWING', 'CONVERTED', 'INVALID')),
    ADD CONSTRAINT ck_crm_lead_terminal_data CHECK (
        (status = 'CONVERTED' AND customer_id IS NOT NULL AND invalid_reason IS NULL)
        OR (status = 'INVALID' AND customer_id IS NULL AND NULLIF(btrim(invalid_reason), '') IS NOT NULL)
        OR (status IN ('NEW', 'FOLLOWING') AND customer_id IS NULL AND invalid_reason IS NULL)
    );

ALTER TABLE crm_customer
    ADD COLUMN merged_into_customer_id BIGINT,
    ADD COLUMN merged_at TIMESTAMPTZ,
    ADD CONSTRAINT ck_crm_customer_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    ADD CONSTRAINT ck_crm_customer_merge_shape CHECK (
        (merged_into_customer_id IS NULL AND merged_at IS NULL)
        OR (merged_into_customer_id IS NOT NULL AND merged_at IS NOT NULL AND deleted_at IS NOT NULL)
    );

ALTER TABLE crm_contact
    ADD COLUMN source_customer_id BIGINT;

ALTER TABLE crm_follow_up
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT,
    ADD COLUMN extension JSONB NOT NULL DEFAULT '{}'::jsonb;
UPDATE crm_follow_up SET created_by = actor_user_id WHERE created_by IS NULL;

ALTER TABLE crm_ownership_history
    ADD COLUMN operation_id VARCHAR(64),
    ADD COLUMN source VARCHAR(32) NOT NULL DEFAULT 'API',
    ADD COLUMN before_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN after_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN batch_no VARCHAR(64),
    ADD COLUMN trace_id VARCHAR(64);
ALTER TABLE crm_ownership_history DROP CONSTRAINT crm_ownership_history_action_check;
ALTER TABLE crm_ownership_history
    ADD CONSTRAINT ck_crm_ownership_history_action CHECK (action IN (
        'CREATE', 'CLAIM', 'RELEASE', 'ASSIGN', 'TRANSFER', 'RECYCLE',
        'CONVERT', 'MERGE', 'HANDOVER', 'INVALIDATE', 'STATUS_CHANGE'
    )),
    ADD CONSTRAINT ck_crm_ownership_history_source
        CHECK (source IN ('API', 'IMPORT', 'SYSTEM', 'SCHEDULER', 'CALLBACK'));

ALTER TABLE crm_resource_handover
    ADD COLUMN operation_id VARCHAR(64),
    ADD COLUMN source VARCHAR(32) NOT NULL DEFAULT 'API',
    ADD COLUMN reason VARCHAR(500),
    ADD COLUMN before_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN after_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    ADD COLUMN batch_no VARCHAR(64),
    ADD COLUMN trace_id VARCHAR(64),
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN completed_at TIMESTAMPTZ,
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT;
UPDATE crm_resource_handover SET completed_at = created_at WHERE status = 'COMPLETED';
ALTER TABLE crm_resource_handover
    ADD CONSTRAINT ck_crm_handover_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    ADD CONSTRAINT ck_crm_handover_source
        CHECK (source IN ('API', 'IMPORT', 'SYSTEM', 'SCHEDULER')),
    ADD CONSTRAINT ck_crm_handover_users_differ CHECK (from_user_id <> to_user_id);

CREATE INDEX idx_crm_lead_scope_follow_up
    ON crm_lead(tenant_id, ownership_type, owner_dept_id, owner_user_id, next_follow_up_at, id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_lead_pool_follow_up
    ON crm_lead(tenant_id, ownership_type, public_pool_id, next_follow_up_at, id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_customer_scope_follow_up
    ON crm_customer(tenant_id, ownership_type, owner_dept_id, owner_user_id, next_follow_up_at, id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_customer_pool_follow_up
    ON crm_customer(tenant_id, ownership_type, public_pool_id, next_follow_up_at, id)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_ownership_operation
    ON crm_ownership_history(tenant_id, operation_id) WHERE operation_id IS NOT NULL;
CREATE INDEX idx_crm_ownership_batch
    ON crm_ownership_history(tenant_id, batch_no, created_at) WHERE batch_no IS NOT NULL;
CREATE INDEX idx_crm_handover_batch
    ON crm_resource_handover(tenant_id, batch_no, created_at) WHERE batch_no IS NOT NULL;
