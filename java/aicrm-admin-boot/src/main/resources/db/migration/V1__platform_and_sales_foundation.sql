CREATE TABLE crm_tenant (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);

CREATE TABLE crm_department (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    parent_id BIGINT REFERENCES crm_department(id),
    name VARCHAR(100) NOT NULL,
    path VARCHAR(1000) NOT NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);
CREATE INDEX idx_crm_department_tenant_parent ON crm_department(tenant_id, parent_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_user (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    department_id BIGINT REFERENCES crm_department(id),
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(32),
    email VARCHAR(200),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);
CREATE INDEX idx_crm_user_tenant_department ON crm_user(tenant_id, department_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_role (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    code VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);
CREATE UNIQUE INDEX uk_crm_role_code_active ON crm_role(tenant_id, code) WHERE deleted_at IS NULL;

CREATE TABLE crm_permission (
    code VARCHAR(100) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    resource_type VARCHAR(64) NOT NULL,
    action VARCHAR(64) NOT NULL
);

CREATE TABLE crm_user_role (
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    user_id BIGINT NOT NULL REFERENCES crm_user(id),
    role_id BIGINT NOT NULL REFERENCES crm_role(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (tenant_id, user_id, role_id)
);

CREATE TABLE crm_role_permission (
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    role_id BIGINT NOT NULL REFERENCES crm_role(id),
    permission_code VARCHAR(100) NOT NULL REFERENCES crm_permission(code),
    PRIMARY KEY (tenant_id, role_id, permission_code)
);

CREATE TABLE crm_idempotency_record (
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    operation VARCHAR(100) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PROCESSING', 'COMPLETED')),
    response_body JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ,
    PRIMARY KEY (tenant_id, operation, idempotency_key)
);

CREATE TABLE crm_audit_log (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    actor_user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(64) NOT NULL,
    resource_id BIGINT,
    before_data JSONB,
    after_data JSONB,
    trace_id VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_crm_audit_tenant_resource ON crm_audit_log(tenant_id, resource_type, resource_id, created_at DESC);

CREATE TABLE crm_outbox_event (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ,
    retry_count INT NOT NULL DEFAULT 0,
    last_error VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_crm_outbox_unpublished ON crm_outbox_event(created_at) WHERE published_at IS NULL;

CREATE TABLE crm_public_pool (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    resource_type VARCHAR(20) NOT NULL CHECK (resource_type IN ('LEAD', 'CUSTOMER')),
    code VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);
CREATE UNIQUE INDEX uk_crm_pool_active ON crm_public_pool(tenant_id, resource_type, code) WHERE deleted_at IS NULL;

CREATE TABLE crm_customer (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    customer_no VARCHAR(32) NOT NULL,
    name VARCHAR(200) NOT NULL,
    industry VARCHAR(100),
    region VARCHAR(100),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    ownership_type VARCHAR(16) NOT NULL CHECK (ownership_type IN ('PRIVATE', 'PUBLIC')),
    owner_user_id BIGINT REFERENCES crm_user(id),
    owner_dept_id BIGINT REFERENCES crm_department(id),
    public_pool_id BIGINT REFERENCES crm_public_pool(id),
    pool_entered_at TIMESTAMPTZ,
    last_follow_up_at TIMESTAMPTZ,
    next_follow_up_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    extension JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CHECK ((ownership_type = 'PRIVATE' AND owner_user_id IS NOT NULL AND public_pool_id IS NULL)
        OR (ownership_type = 'PUBLIC' AND owner_user_id IS NULL AND public_pool_id IS NOT NULL))
);
CREATE UNIQUE INDEX uk_crm_customer_no_active ON crm_customer(tenant_id, customer_no) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_crm_customer_name_active ON crm_customer(tenant_id, lower(name)) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_customer_private ON crm_customer(tenant_id, owner_user_id, updated_at DESC) WHERE deleted_at IS NULL AND ownership_type = 'PRIVATE';
CREATE INDEX idx_crm_customer_public ON crm_customer(tenant_id, public_pool_id, pool_entered_at DESC) WHERE deleted_at IS NULL AND ownership_type = 'PUBLIC';

CREATE TABLE crm_contact (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    customer_id BIGINT NOT NULL REFERENCES crm_customer(id),
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(32),
    email VARCHAR(200),
    department VARCHAR(100),
    title VARCHAR(100),
    is_decision_maker BOOLEAN NOT NULL DEFAULT FALSE,
    extension JSONB NOT NULL DEFAULT '{}'::jsonb,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);
CREATE INDEX idx_crm_contact_customer ON crm_contact(tenant_id, customer_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_lead (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    lead_no VARCHAR(32) NOT NULL,
    name VARCHAR(100),
    mobile VARCHAR(32),
    email VARCHAR(200),
    company_name VARCHAR(200),
    source_type VARCHAR(64) NOT NULL,
    source_ref VARCHAR(200),
    intent VARCHAR(64),
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    invalid_reason VARCHAR(500),
    customer_id BIGINT REFERENCES crm_customer(id),
    ownership_type VARCHAR(16) NOT NULL CHECK (ownership_type IN ('PRIVATE', 'PUBLIC')),
    owner_user_id BIGINT REFERENCES crm_user(id),
    owner_dept_id BIGINT REFERENCES crm_department(id),
    public_pool_id BIGINT REFERENCES crm_public_pool(id),
    pool_entered_at TIMESTAMPTZ,
    last_follow_up_at TIMESTAMPTZ,
    next_follow_up_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    extension JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CHECK ((ownership_type = 'PRIVATE' AND owner_user_id IS NOT NULL AND public_pool_id IS NULL)
        OR (ownership_type = 'PUBLIC' AND owner_user_id IS NULL AND public_pool_id IS NOT NULL))
);
CREATE UNIQUE INDEX uk_crm_lead_no_active ON crm_lead(tenant_id, lead_no) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_lead_private ON crm_lead(tenant_id, owner_user_id, updated_at DESC) WHERE deleted_at IS NULL AND ownership_type = 'PRIVATE';
CREATE INDEX idx_crm_lead_public ON crm_lead(tenant_id, public_pool_id, pool_entered_at DESC) WHERE deleted_at IS NULL AND ownership_type = 'PUBLIC';
CREATE INDEX idx_crm_lead_follow_up ON crm_lead(tenant_id, next_follow_up_at) WHERE deleted_at IS NULL AND ownership_type = 'PRIVATE';

CREATE TABLE crm_follow_up (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    lead_id BIGINT REFERENCES crm_lead(id),
    customer_id BIGINT REFERENCES crm_customer(id),
    actor_user_id BIGINT NOT NULL REFERENCES crm_user(id),
    channel VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    next_follow_up_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK ((lead_id IS NOT NULL AND customer_id IS NULL) OR (lead_id IS NULL AND customer_id IS NOT NULL))
);
CREATE INDEX idx_crm_follow_up_lead ON crm_follow_up(tenant_id, lead_id, created_at DESC) WHERE lead_id IS NOT NULL;
CREATE INDEX idx_crm_follow_up_customer ON crm_follow_up(tenant_id, customer_id, created_at DESC) WHERE customer_id IS NOT NULL;

CREATE TABLE crm_ownership_history (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    resource_type VARCHAR(20) NOT NULL CHECK (resource_type IN ('LEAD', 'CUSTOMER')),
    resource_id BIGINT NOT NULL,
    action VARCHAR(32) NOT NULL CHECK (action IN ('CREATE', 'CLAIM', 'RELEASE', 'ASSIGN', 'TRANSFER', 'RECYCLE', 'CONVERT', 'MERGE', 'HANDOVER', 'INVALIDATE')),
    from_owner_user_id BIGINT,
    to_owner_user_id BIGINT,
    from_pool_id BIGINT,
    to_pool_id BIGINT,
    reason VARCHAR(500),
    operator_user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_crm_ownership_history_resource ON crm_ownership_history(tenant_id, resource_type, resource_id, created_at DESC);

CREATE TABLE crm_resource_handover (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    from_user_id BIGINT NOT NULL REFERENCES crm_user(id),
    to_user_id BIGINT NOT NULL REFERENCES crm_user(id),
    resource_type VARCHAR(20) NOT NULL CHECK (resource_type IN ('LEAD', 'CUSTOMER')),
    resource_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_crm_handover_tenant_from ON crm_resource_handover(tenant_id, from_user_id, created_at DESC);
