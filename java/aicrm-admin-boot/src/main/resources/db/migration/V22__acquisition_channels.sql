-- Stage 5A: CRM-owned acquisition channels and immutable lead attribution.
CREATE TABLE crm_acquisition_channel (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    code VARCHAR(64) NOT NULL,
    name VARCHAR(200) NOT NULL,
    source_type VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_acquisition_channel_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_acquisition_channel_code UNIQUE (tenant_id, code),
    CONSTRAINT ck_crm_acquisition_channel_status CHECK (status IN ('DRAFT', 'ACTIVE', 'DISABLED')),
    CONSTRAINT fk_crm_acquisition_channel_creator FOREIGN KEY (tenant_id, created_by) REFERENCES crm_user(tenant_id, id),
    CONSTRAINT fk_crm_acquisition_channel_updater FOREIGN KEY (tenant_id, updated_by) REFERENCES crm_user(tenant_id, id)
);
CREATE INDEX idx_crm_acquisition_channel_status ON crm_acquisition_channel(tenant_id, status) WHERE deleted_at IS NULL;

ALTER TABLE crm_lead
    ADD COLUMN acquisition_channel_id BIGINT,
    ADD COLUMN acquisition_channel_code VARCHAR(64),
    ADD CONSTRAINT ck_crm_lead_acquisition_channel CHECK (
        (acquisition_channel_id IS NULL AND acquisition_channel_code IS NULL)
        OR (acquisition_channel_id IS NOT NULL AND acquisition_channel_code IS NOT NULL)
    ),
    ADD CONSTRAINT fk_crm_lead_acquisition_channel
        FOREIGN KEY (tenant_id, acquisition_channel_id) REFERENCES crm_acquisition_channel(tenant_id, id);
CREATE INDEX idx_crm_lead_acquisition_channel ON crm_lead(tenant_id, acquisition_channel_id)
    WHERE acquisition_channel_id IS NOT NULL AND deleted_at IS NULL;

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, 'ACQUISITION_CHANNEL', p.action FROM crm_tenant t
CROSS JOIN (VALUES
    ('channel:read', '查看获客渠道', 'READ'),
    ('channel:manage', '维护获客渠道', 'MANAGE')
) p(code, name, action) ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_acquisition_channel_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action) VALUES
        (p_tenant_id, 'channel:read', '查看获客渠道', 'ACQUISITION_CHANNEL', 'READ'),
        (p_tenant_id, 'channel:manage', '维护获客渠道', 'ACQUISITION_CHANNEL', 'MANAGE')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
CREATE OR REPLACE FUNCTION crm_seed_acquisition_channel_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN PERFORM crm_seed_acquisition_channel_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_acquisition_channel_permissions
AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_acquisition_channel_permissions_after_tenant_insert();
