-- Stage 2C: quote root, immutable versions and product/price snapshots.
CREATE TABLE crm_quote (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    quote_no VARCHAR(32) NOT NULL,
    opportunity_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    price_list_id BIGINT NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    current_version_no INTEGER NOT NULL DEFAULT 1,
    valid_until DATE,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_quote_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_quote_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'EXPIRED', 'CANCELLED')),
    CONSTRAINT ck_crm_quote_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_crm_quote_version CHECK (current_version_no >= 1),
    CONSTRAINT fk_crm_quote_opportunity_tenant FOREIGN KEY (tenant_id, opportunity_id) REFERENCES crm_opportunity(tenant_id, id),
    CONSTRAINT fk_crm_quote_customer_tenant FOREIGN KEY (tenant_id, customer_id) REFERENCES crm_customer(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_quote_no_active ON crm_quote(tenant_id, quote_no) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_quote_opportunity ON crm_quote(tenant_id, opportunity_id, status, updated_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_quote_version (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    quote_id BIGINT NOT NULL,
    version_no INTEGER NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    subtotal NUMERIC(18,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    discount_rate NUMERIC(5,4) NOT NULL DEFAULT 0,
    rejection_reason VARCHAR(500),
    submitted_at TIMESTAMPTZ,
    approved_at TIMESTAMPTZ,
    rejected_at TIMESTAMPTZ,
    expired_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_quote_version_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_quote_version_no UNIQUE (tenant_id, quote_id, version_no),
    CONSTRAINT ck_crm_quote_version_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'EXPIRED')),
    CONSTRAINT ck_crm_quote_version_amount CHECK (subtotal >= 0 AND discount_amount >= 0 AND tax_amount >= 0 AND total_amount >= 0),
    CONSTRAINT ck_crm_quote_version_discount CHECK (discount_rate >= 0 AND discount_rate <= 1),
    CONSTRAINT fk_crm_quote_version_quote_tenant FOREIGN KEY (tenant_id, quote_id) REFERENCES crm_quote(tenant_id, id)
);
CREATE INDEX idx_crm_quote_version_timeline ON crm_quote_version(tenant_id, quote_id, version_no DESC);

CREATE TABLE crm_quote_line (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    quote_version_id BIGINT NOT NULL,
    line_no INTEGER NOT NULL,
    product_id BIGINT NOT NULL,
    price_item_id BIGINT NOT NULL,
    product_no_snapshot VARCHAR(32) NOT NULL,
    sku_snapshot VARCHAR(64) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    list_price NUMERIC(18,2) NOT NULL,
    minimum_price NUMERIC(18,2),
    unit_price NUMERIC(18,2) NOT NULL,
    discount_rate NUMERIC(5,4) NOT NULL DEFAULT 0,
    tax_rate NUMERIC(5,4) NOT NULL DEFAULT 0,
    line_amount NUMERIC(18,2) NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_quote_line_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_quote_line_no UNIQUE (tenant_id, quote_version_id, line_no),
    CONSTRAINT ck_crm_quote_line_quantity CHECK (quantity > 0),
    CONSTRAINT ck_crm_quote_line_prices CHECK (list_price >= 0 AND unit_price >= 0 AND (minimum_price IS NULL OR minimum_price >= 0) AND (minimum_price IS NULL OR unit_price >= minimum_price)),
    CONSTRAINT ck_crm_quote_line_rates CHECK (discount_rate >= 0 AND discount_rate <= 1 AND tax_rate >= 0 AND tax_rate <= 1),
    CONSTRAINT fk_crm_quote_line_version_tenant FOREIGN KEY (tenant_id, quote_version_id) REFERENCES crm_quote_version(tenant_id, id)
);
CREATE INDEX idx_crm_quote_line_product ON crm_quote_line(tenant_id, product_id) WHERE deleted_at IS NULL;
CREATE TRIGGER trg_crm_quote_version_append_only
BEFORE UPDATE OR DELETE ON crm_quote_version
FOR EACH ROW WHEN (OLD.status IN ('APPROVED', 'REJECTED', 'EXPIRED'))
EXECUTE FUNCTION crm_reject_immutable_change();
CREATE TRIGGER trg_crm_quote_line_append_only
BEFORE UPDATE OR DELETE ON crm_quote_line
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, 'QUOTE', p.action
FROM crm_tenant t
CROSS JOIN (VALUES
    ('quote:create','创建报价','CREATE'), ('quote:read:own','查看本人报价','READ_OWN'),
    ('quote:read:any','查看全部报价','READ_ANY'), ('quote:write:own','维护本人报价','WRITE_OWN'),
    ('quote:write:any','维护全部报价','WRITE_ANY'), ('quote:submit','提交报价','SUBMIT'),
    ('quote:reject','拒绝报价','REJECT'), ('quote:expire','报价过期处理','EXPIRE')
) p(code, name, action)
ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_quote_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES
        (p_tenant_id, 'quote:create', '创建报价', 'QUOTE', 'CREATE'),
        (p_tenant_id, 'quote:read:own', '查看本人报价', 'QUOTE', 'READ_OWN'),
        (p_tenant_id, 'quote:read:any', '查看全部报价', 'QUOTE', 'READ_ANY'),
        (p_tenant_id, 'quote:write:own', '维护本人报价', 'QUOTE', 'WRITE_OWN'),
        (p_tenant_id, 'quote:write:any', '维护全部报价', 'QUOTE', 'WRITE_ANY'),
        (p_tenant_id, 'quote:submit', '提交报价', 'QUOTE', 'SUBMIT'),
        (p_tenant_id, 'quote:reject', '拒绝报价', 'QUOTE', 'REJECT'),
        (p_tenant_id, 'quote:expire', '报价过期处理', 'QUOTE', 'EXPIRE')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
SELECT crm_seed_quote_permissions(id) FROM crm_tenant;
CREATE OR REPLACE FUNCTION crm_seed_quote_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    PERFORM crm_seed_quote_permissions(NEW.id);
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_tenant_seed_quote_permissions
AFTER INSERT ON crm_tenant
FOR EACH ROW EXECUTE FUNCTION crm_seed_quote_permissions_after_tenant_insert();
