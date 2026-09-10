-- Stage 4D: CRM-only rule-driven target scoring. Existing targets remain unscored unless they opt in.
CREATE TABLE crm_performance_score_rule (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    rule_no VARCHAR(32) NOT NULL,
    name VARCHAR(200) NOT NULL,
    metric VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_performance_score_rule_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_performance_score_rule_no UNIQUE (tenant_id, rule_no),
    CONSTRAINT ck_crm_performance_score_rule_metric CHECK (metric IN ('SIGNED_CONTRACT_AMOUNT', 'CONFIRMED_ORDER_AMOUNT')),
    CONSTRAINT ck_crm_performance_score_rule_status CHECK (status IN ('DRAFT', 'ACTIVE', 'RETIRED'))
);

CREATE TABLE crm_performance_score_band (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    rule_id BIGINT NOT NULL,
    band_no INTEGER NOT NULL,
    minimum_achievement_rate NUMERIC(18,4) NOT NULL,
    maximum_achievement_rate NUMERIC(18,4),
    score NUMERIC(18,2) NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_crm_performance_score_band_rule_no UNIQUE (tenant_id, rule_id, band_no),
    CONSTRAINT fk_crm_performance_score_band_rule FOREIGN KEY (tenant_id, rule_id) REFERENCES crm_performance_score_rule(tenant_id, id),
    CONSTRAINT ck_crm_performance_score_band_rate CHECK (minimum_achievement_rate >= 0 AND (maximum_achievement_rate IS NULL OR maximum_achievement_rate > minimum_achievement_rate)),
    CONSTRAINT ck_crm_performance_score_band_score CHECK (score >= 0)
);
CREATE OR REPLACE FUNCTION crm_reject_non_draft_performance_score_band_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM crm_performance_score_rule r WHERE r.tenant_id = OLD.tenant_id AND r.id = OLD.rule_id AND r.status <> 'DRAFT') THEN
        RAISE EXCEPTION 'active or retired performance score rule bands are immutable' USING ERRCODE = '55000';
    END IF;
    RETURN CASE WHEN TG_OP = 'DELETE' THEN OLD ELSE NEW END;
END $$;
CREATE TRIGGER trg_crm_performance_score_band_draft_only
BEFORE UPDATE OR DELETE ON crm_performance_score_band
FOR EACH ROW EXECUTE FUNCTION crm_reject_non_draft_performance_score_band_change();

ALTER TABLE crm_sales_target ADD COLUMN score_rule_id BIGINT;
ALTER TABLE crm_sales_target ADD CONSTRAINT fk_crm_sales_target_score_rule
    FOREIGN KEY (tenant_id, score_rule_id) REFERENCES crm_performance_score_rule(tenant_id, id);
ALTER TABLE crm_sales_target_result ADD CONSTRAINT uk_crm_sales_target_result_tenant_id UNIQUE (tenant_id, id);

CREATE TABLE crm_sales_target_score (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    target_id BIGINT NOT NULL,
    target_result_id BIGINT NOT NULL,
    score_rule_id BIGINT NOT NULL,
    achievement_rate NUMERIC(18,4) NOT NULL,
    score NUMERIC(18,2) NOT NULL,
    calculation_snapshot JSONB NOT NULL,
    confirmed_by BIGINT NOT NULL,
    confirmed_at TIMESTAMPTZ NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_crm_sales_target_score_target UNIQUE (tenant_id, target_id),
    CONSTRAINT uk_crm_sales_target_score_result UNIQUE (tenant_id, target_result_id),
    CONSTRAINT fk_crm_sales_target_score_target FOREIGN KEY (tenant_id, target_id) REFERENCES crm_sales_target(tenant_id, id),
    CONSTRAINT fk_crm_sales_target_score_result FOREIGN KEY (tenant_id, target_result_id) REFERENCES crm_sales_target_result(tenant_id, id),
    CONSTRAINT fk_crm_sales_target_score_rule FOREIGN KEY (tenant_id, score_rule_id) REFERENCES crm_performance_score_rule(tenant_id, id),
    CONSTRAINT fk_crm_sales_target_score_confirmer FOREIGN KEY (tenant_id, confirmed_by) REFERENCES crm_user(tenant_id, id),
    CONSTRAINT ck_crm_sales_target_score_values CHECK (achievement_rate >= 0 AND score >= 0)
);
CREATE TRIGGER trg_crm_sales_target_score_append_only
BEFORE UPDATE OR DELETE ON crm_sales_target_score
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, 'PERFORMANCE_RULE', p.action FROM crm_tenant t
CROSS JOIN (VALUES
    ('performance:rule:read', '查看绩效评分规则', 'READ'),
    ('performance:rule:manage', '维护绩效评分规则', 'MANAGE')
) p(code, name, action) ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_performance_rule_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action) VALUES
        (p_tenant_id, 'performance:rule:read', '查看绩效评分规则', 'PERFORMANCE_RULE', 'READ'),
        (p_tenant_id, 'performance:rule:manage', '维护绩效评分规则', 'PERFORMANCE_RULE', 'MANAGE')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
CREATE OR REPLACE FUNCTION crm_seed_performance_rule_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN PERFORM crm_seed_performance_rule_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_performance_rule_permissions
AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_performance_rule_permissions_after_tenant_insert();
