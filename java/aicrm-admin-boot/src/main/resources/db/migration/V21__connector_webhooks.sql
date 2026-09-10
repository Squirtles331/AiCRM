-- Stage 4E: CRM-owned connector registration and idempotent inbound webhook receipts.
CREATE TABLE crm_connector (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    connector_no VARCHAR(32) NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    operator_user_id BIGINT NOT NULL,
    public_pool_id BIGINT,
    secret_hash VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_connector_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_connector_no UNIQUE (tenant_id, connector_no),
    CONSTRAINT ck_crm_connector_type CHECK (type IN ('MARKETING_WEBHOOK', 'ORGANIZATION_WEBHOOK')),
    CONSTRAINT ck_crm_connector_status CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT ck_crm_connector_pool CHECK (
        (type = 'MARKETING_WEBHOOK' AND public_pool_id IS NOT NULL)
        OR (type = 'ORGANIZATION_WEBHOOK' AND public_pool_id IS NULL)
    ),
    CONSTRAINT fk_crm_connector_operator FOREIGN KEY (tenant_id, operator_user_id) REFERENCES crm_user(tenant_id, id),
    CONSTRAINT fk_crm_connector_pool FOREIGN KEY (tenant_id, public_pool_id) REFERENCES crm_public_pool(tenant_id, id)
);
CREATE INDEX idx_crm_connector_tenant_status ON crm_connector(tenant_id, status) WHERE deleted_at IS NULL;

CREATE TABLE crm_connector_event (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    connector_id BIGINT NOT NULL,
    external_event_id VARCHAR(128) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    payload JSONB NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    outcome VARCHAR(32) NOT NULL,
    lead_id BIGINT,
    received_at TIMESTAMPTZ NOT NULL,
    created_by BIGINT NOT NULL,
    CONSTRAINT uk_crm_connector_event_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_connector_event_external UNIQUE (tenant_id, connector_id, external_event_id),
    CONSTRAINT ck_crm_connector_event_outcome CHECK (outcome IN ('LEAD_CREATED', 'ACCEPTED')),
    CONSTRAINT ck_crm_connector_event_lead CHECK (
        (outcome = 'LEAD_CREATED' AND lead_id IS NOT NULL) OR (outcome = 'ACCEPTED' AND lead_id IS NULL)
    ),
    CONSTRAINT fk_crm_connector_event_connector FOREIGN KEY (tenant_id, connector_id) REFERENCES crm_connector(tenant_id, id),
    CONSTRAINT fk_crm_connector_event_lead FOREIGN KEY (tenant_id, lead_id) REFERENCES crm_lead(tenant_id, id),
    CONSTRAINT fk_crm_connector_event_creator FOREIGN KEY (tenant_id, created_by) REFERENCES crm_user(tenant_id, id)
);
CREATE INDEX idx_crm_connector_event_received ON crm_connector_event(tenant_id, connector_id, received_at DESC);
CREATE TRIGGER trg_crm_connector_event_append_only
BEFORE UPDATE OR DELETE ON crm_connector_event
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

CREATE OR REPLACE FUNCTION crm_validate_connector_references()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM crm_user u
        WHERE u.tenant_id = NEW.tenant_id AND u.id = NEW.operator_user_id
          AND u.status = 1 AND u.deleted_at IS NULL
    ) THEN
        RAISE EXCEPTION 'connector operator must be an active tenant user' USING ERRCODE = '23514';
    END IF;
    IF NEW.type = 'MARKETING_WEBHOOK' AND NOT EXISTS (
        SELECT 1 FROM crm_public_pool p
        WHERE p.tenant_id = NEW.tenant_id AND p.id = NEW.public_pool_id
          AND p.resource_type = 'LEAD' AND p.status = 1 AND p.deleted_at IS NULL
    ) THEN
        RAISE EXCEPTION 'marketing connector requires an active lead public pool' USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_connector_references
BEFORE INSERT OR UPDATE OF type, operator_user_id, public_pool_id ON crm_connector
FOR EACH ROW EXECUTE FUNCTION crm_validate_connector_references();

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, 'CONNECTOR', p.action FROM crm_tenant t
CROSS JOIN (VALUES
    ('connector:read', '查看连接器配置', 'READ'),
    ('connector:manage', '维护连接器配置', 'MANAGE')
) p(code, name, action) ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_connector_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action) VALUES
        (p_tenant_id, 'connector:read', '查看连接器配置', 'CONNECTOR', 'READ'),
        (p_tenant_id, 'connector:manage', '维护连接器配置', 'CONNECTOR', 'MANAGE')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
CREATE OR REPLACE FUNCTION crm_seed_connector_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN PERFORM crm_seed_connector_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_connector_permissions
AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_connector_permissions_after_tenant_insert();
