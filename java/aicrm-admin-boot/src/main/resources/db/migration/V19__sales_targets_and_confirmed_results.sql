-- Stage 4B: CRM-only sales targets and immutable confirmed result snapshots.
CREATE TABLE crm_sales_target (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    target_no VARCHAR(32) NOT NULL,
    name VARCHAR(200) NOT NULL,
    target_user_id BIGINT NOT NULL,
    metric VARCHAR(32) NOT NULL,
    period_from DATE NOT NULL,
    period_to DATE NOT NULL,
    target_value NUMERIC(18,2) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_sales_target_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_sales_target_no UNIQUE (tenant_id, target_no),
    CONSTRAINT ck_crm_sales_target_metric CHECK (metric IN ('SIGNED_CONTRACT_AMOUNT', 'CONFIRMED_ORDER_AMOUNT')),
    CONSTRAINT ck_crm_sales_target_status CHECK (status IN ('DRAFT', 'ACTIVE', 'RESULT_CONFIRMED')),
    CONSTRAINT ck_crm_sales_target_period CHECK (period_to > period_from),
    CONSTRAINT ck_crm_sales_target_value CHECK (target_value > 0),
    CONSTRAINT fk_crm_sales_target_user FOREIGN KEY (tenant_id, target_user_id) REFERENCES crm_user(tenant_id, id)
);
CREATE INDEX idx_crm_sales_target_user_period ON crm_sales_target(tenant_id, target_user_id, period_from, period_to) WHERE deleted_at IS NULL;

CREATE TABLE crm_sales_target_result (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    target_id BIGINT NOT NULL,
    actual_value NUMERIC(18,2) NOT NULL,
    achievement_rate NUMERIC(18,4) NOT NULL,
    calculation_snapshot JSONB NOT NULL,
    confirmed_by BIGINT NOT NULL,
    calculated_at TIMESTAMPTZ NOT NULL,
    confirmed_at TIMESTAMPTZ NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_crm_sales_target_result_target UNIQUE (tenant_id, target_id),
    CONSTRAINT ck_crm_sales_target_result_value CHECK (actual_value >= 0 AND achievement_rate >= 0),
    CONSTRAINT fk_crm_sales_target_result_target FOREIGN KEY (tenant_id, target_id) REFERENCES crm_sales_target(tenant_id, id),
    CONSTRAINT fk_crm_sales_target_result_confirmer FOREIGN KEY (tenant_id, confirmed_by) REFERENCES crm_user(tenant_id, id)
);
CREATE TRIGGER trg_crm_sales_target_result_append_only
BEFORE UPDATE OR DELETE ON crm_sales_target_result
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, 'SALES_TARGET', p.action FROM crm_tenant t
CROSS JOIN (VALUES
    ('target:read:own', '查看本人销售目标', 'READ_OWN'),
    ('target:read:any', '查看全部销售目标', 'READ_ANY'),
    ('target:manage', '维护销售目标', 'MANAGE'),
    ('target:confirm', '确认销售目标结果', 'CONFIRM')
) p(code, name, action) ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_sales_target_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action) VALUES
        (p_tenant_id, 'target:read:own', '查看本人销售目标', 'SALES_TARGET', 'READ_OWN'),
        (p_tenant_id, 'target:read:any', '查看全部销售目标', 'SALES_TARGET', 'READ_ANY'),
        (p_tenant_id, 'target:manage', '维护销售目标', 'SALES_TARGET', 'MANAGE'),
        (p_tenant_id, 'target:confirm', '确认销售目标结果', 'SALES_TARGET', 'CONFIRM')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
CREATE OR REPLACE FUNCTION crm_seed_sales_target_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN PERFORM crm_seed_sales_target_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_sales_target_permissions
AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_sales_target_permissions_after_tenant_insert();
