-- Stage 3: contract and sales order facts. ERP owns inventory and actual fulfillment facts.
CREATE TABLE crm_contract (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    contract_no VARCHAR(32) NOT NULL,
    name VARCHAR(200) NOT NULL,
    quote_id BIGINT NOT NULL,
    quote_version_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    currency CHAR(3) NOT NULL,
    subtotal NUMERIC(18,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    effective_from DATE,
    effective_to DATE,
    submitted_at TIMESTAMPTZ,
    signed_at TIMESTAMPTZ,
    voided_at TIMESTAMPTZ,
    void_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_contract_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_contract_status CHECK (status IN ('DRAFT', 'PENDING_SIGNATURE', 'SIGNED', 'VOIDED')),
    CONSTRAINT ck_crm_contract_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_crm_contract_amount CHECK (subtotal >= 0 AND discount_amount >= 0 AND tax_amount >= 0 AND total_amount >= 0),
    CONSTRAINT ck_crm_contract_period CHECK (effective_to IS NULL OR effective_from IS NULL OR effective_to >= effective_from),
    CONSTRAINT ck_crm_contract_void CHECK ((status = 'VOIDED' AND voided_at IS NOT NULL AND void_reason IS NOT NULL) OR (status <> 'VOIDED' AND voided_at IS NULL AND void_reason IS NULL))
);
CREATE UNIQUE INDEX uk_crm_contract_no_active ON crm_contract(tenant_id, contract_no) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_contract_quote ON crm_contract(tenant_id, quote_id, status, updated_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_contract_customer ON crm_contract(tenant_id, customer_id, status, updated_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_contract_line (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    contract_id BIGINT NOT NULL,
    line_no INTEGER NOT NULL,
    quote_line_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_no_snapshot VARCHAR(32) NOT NULL,
    sku_snapshot VARCHAR(64) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    list_price NUMERIC(18,2) NOT NULL,
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
    CONSTRAINT uk_crm_contract_line_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_contract_line_no UNIQUE (tenant_id, contract_id, line_no),
    CONSTRAINT ck_crm_contract_line_quantity CHECK (quantity > 0),
    CONSTRAINT ck_crm_contract_line_amount CHECK (list_price >= 0 AND unit_price >= 0 AND line_amount >= 0),
    CONSTRAINT ck_crm_contract_line_rates CHECK (discount_rate >= 0 AND discount_rate <= 1 AND tax_rate >= 0 AND tax_rate <= 1),
    CONSTRAINT fk_crm_contract_line_contract FOREIGN KEY (tenant_id, contract_id) REFERENCES crm_contract(tenant_id, id)
);
CREATE INDEX idx_crm_contract_line_product ON crm_contract_line(tenant_id, product_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_contract_change (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    contract_id BIGINT NOT NULL,
    change_no VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    reason VARCHAR(500) NOT NULL,
    before_snapshot JSONB NOT NULL,
    after_snapshot JSONB NOT NULL,
    submitted_at TIMESTAMPTZ,
    approved_at TIMESTAMPTZ,
    rejected_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    rejection_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_contract_change_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_contract_change_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT fk_crm_contract_change_contract FOREIGN KEY (tenant_id, contract_id) REFERENCES crm_contract(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_contract_change_no_active ON crm_contract_change(tenant_id, change_no) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_contract_change_contract ON crm_contract_change(tenant_id, contract_id, created_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_sales_order (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    order_no VARCHAR(32) NOT NULL,
    contract_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    external_order_no VARCHAR(100),
    currency CHAR(3) NOT NULL,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    expected_delivery_at TIMESTAMPTZ,
    confirmed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_sales_order_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_sales_order_status CHECK (status IN ('DRAFT', 'CONFIRMED', 'CANCELLING', 'CANCELLED', 'CLOSED')),
    CONSTRAINT ck_crm_sales_order_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_crm_sales_order_amount CHECK (total_amount >= 0)
);
CREATE UNIQUE INDEX uk_crm_sales_order_no_active ON crm_sales_order(tenant_id, order_no) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_crm_sales_order_external ON crm_sales_order(tenant_id, external_order_no) WHERE external_order_no IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_crm_sales_order_contract ON crm_sales_order(tenant_id, contract_id, status, updated_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_sales_order_line (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    order_id BIGINT NOT NULL,
    line_no INTEGER NOT NULL,
    contract_line_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_no_snapshot VARCHAR(32) NOT NULL,
    sku_snapshot VARCHAR(64) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    tax_rate NUMERIC(5,4) NOT NULL DEFAULT 0,
    line_amount NUMERIC(18,2) NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_sales_order_line_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_sales_order_line_no UNIQUE (tenant_id, order_id, line_no),
    CONSTRAINT ck_crm_sales_order_line_quantity CHECK (quantity > 0),
    CONSTRAINT ck_crm_sales_order_line_amount CHECK (unit_price >= 0 AND line_amount >= 0),
    CONSTRAINT ck_crm_sales_order_line_tax CHECK (tax_rate >= 0 AND tax_rate <= 1),
    CONSTRAINT fk_crm_sales_order_line_order FOREIGN KEY (tenant_id, order_id) REFERENCES crm_sales_order(tenant_id, id)
);
CREATE INDEX idx_crm_sales_order_line_product ON crm_sales_order_line(tenant_id, product_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_order_cancel (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    order_id BIGINT NOT NULL,
    cancel_no VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(500) NOT NULL,
    request_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    requested_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    approved_at TIMESTAMPTZ,
    rejected_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    rejection_reason VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_order_cancel_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_order_cancel_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED')),
    CONSTRAINT fk_crm_order_cancel_order FOREIGN KEY (tenant_id, order_id) REFERENCES crm_sales_order(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_order_cancel_no_active ON crm_order_cancel(tenant_id, cancel_no) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_crm_order_cancel_pending ON crm_order_cancel(tenant_id, order_id) WHERE status IN ('PENDING', 'APPROVED') AND deleted_at IS NULL;
CREATE INDEX idx_crm_order_cancel_order ON crm_order_cancel(tenant_id, order_id, created_at DESC) WHERE deleted_at IS NULL;

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, p.resource_type, p.action FROM crm_tenant t
CROSS JOIN (VALUES
    ('contract:create', '创建合同', 'CONTRACT', 'CREATE'),
    ('contract:read:own', '查看本人合同', 'CONTRACT', 'READ_OWN'),
    ('contract:read:any', '查看全部合同', 'CONTRACT', 'READ_ANY'),
    ('contract:write:own', '维护本人合同', 'CONTRACT', 'WRITE_OWN'),
    ('contract:write:any', '维护全部合同', 'CONTRACT', 'WRITE_ANY'),
    ('contract:submit-signature', '提交合同签署', 'CONTRACT', 'SUBMIT_SIGNATURE'),
    ('contract:sign', '确认合同签署', 'CONTRACT', 'SIGN'),
    ('contract:void', '作废合同', 'CONTRACT', 'VOID'),
    ('contract:change', '发起合同变更', 'CONTRACT', 'CHANGE'),
    ('order:create', '创建销售订单', 'ORDER', 'CREATE'),
    ('order:read:own', '查看本人订单', 'ORDER', 'READ_OWN'),
    ('order:read:any', '查看全部订单', 'ORDER', 'READ_ANY'),
    ('order:write:own', '维护本人订单', 'ORDER', 'WRITE_OWN'),
    ('order:write:any', '维护全部订单', 'ORDER', 'WRITE_ANY'),
    ('order:confirm', '确认销售订单', 'ORDER', 'CONFIRM'),
    ('order:cancel', '申请取消销售订单', 'ORDER', 'CANCEL'),
    ('integration:erp:callback', '接收 ERP 回传', 'INTEGRATION', 'ERP_CALLBACK')
) p(code, name, resource_type, action) ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_contract_order_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES
        (p_tenant_id, 'contract:create', '创建合同', 'CONTRACT', 'CREATE'),
        (p_tenant_id, 'contract:read:own', '查看本人合同', 'CONTRACT', 'READ_OWN'),
        (p_tenant_id, 'contract:read:any', '查看全部合同', 'CONTRACT', 'READ_ANY'),
        (p_tenant_id, 'contract:write:own', '维护本人合同', 'CONTRACT', 'WRITE_OWN'),
        (p_tenant_id, 'contract:write:any', '维护全部合同', 'CONTRACT', 'WRITE_ANY'),
        (p_tenant_id, 'contract:submit-signature', '提交合同签署', 'CONTRACT', 'SUBMIT_SIGNATURE'),
        (p_tenant_id, 'contract:sign', '确认合同签署', 'CONTRACT', 'SIGN'),
        (p_tenant_id, 'contract:void', '作废合同', 'CONTRACT', 'VOID'),
        (p_tenant_id, 'contract:change', '发起合同变更', 'CONTRACT', 'CHANGE'),
        (p_tenant_id, 'order:create', '创建销售订单', 'ORDER', 'CREATE'),
        (p_tenant_id, 'order:read:own', '查看本人订单', 'ORDER', 'READ_OWN'),
        (p_tenant_id, 'order:read:any', '查看全部订单', 'ORDER', 'READ_ANY'),
        (p_tenant_id, 'order:write:own', '维护本人订单', 'ORDER', 'WRITE_OWN'),
        (p_tenant_id, 'order:write:any', '维护全部订单', 'ORDER', 'WRITE_ANY'),
        (p_tenant_id, 'order:confirm', '确认销售订单', 'ORDER', 'CONFIRM'),
        (p_tenant_id, 'order:cancel', '申请取消销售订单', 'ORDER', 'CANCEL'),
        (p_tenant_id, 'integration:erp:callback', '接收 ERP 回传', 'INTEGRATION', 'ERP_CALLBACK')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
SELECT crm_seed_contract_order_permissions(id) FROM crm_tenant;
CREATE OR REPLACE FUNCTION crm_seed_contract_order_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    PERFORM crm_seed_contract_order_permissions(NEW.id);
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_tenant_seed_contract_order_permissions
AFTER INSERT ON crm_tenant
FOR EACH ROW EXECUTE FUNCTION crm_seed_contract_order_permissions_after_tenant_insert();
