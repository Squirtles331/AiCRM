-- Stage 2A: product and price master data. All records are tenant-owned and mutable through optimistic locking.
CREATE TABLE crm_product_category (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    parent_id BIGINT,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    sort_order INTEGER NOT NULL DEFAULT 0,
    extension JSONB NOT NULL DEFAULT '{}'::jsonb,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_product_category_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_product_category_status CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT ck_crm_product_category_sort_order CHECK (sort_order >= 0),
    CONSTRAINT fk_crm_product_category_parent_tenant
        FOREIGN KEY (tenant_id, parent_id) REFERENCES crm_product_category(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_product_category_code_active
    ON crm_product_category(tenant_id, code) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_product_category_tenant_parent
    ON crm_product_category(tenant_id, parent_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_product (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    category_id BIGINT,
    product_no VARCHAR(32) NOT NULL,
    sku VARCHAR(64) NOT NULL,
    name VARCHAR(200) NOT NULL,
    specification VARCHAR(500),
    unit VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    sale_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    extension JSONB NOT NULL DEFAULT '{}'::jsonb,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_product_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_product_status CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT fk_crm_product_category_tenant
        FOREIGN KEY (tenant_id, category_id) REFERENCES crm_product_category(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_product_no_active ON crm_product(tenant_id, product_no) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_crm_product_sku_active ON crm_product(tenant_id, sku) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_product_tenant_category ON crm_product(tenant_id, category_id) WHERE deleted_at IS NULL;

CREATE TABLE crm_price_list (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    code VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    effective_from TIMESTAMPTZ,
    effective_to TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_price_list_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_price_list_status CHECK (status IN ('DRAFT', 'ACTIVE', 'EXPIRED', 'DISABLED')),
    CONSTRAINT ck_crm_price_list_effective_range CHECK (effective_to IS NULL OR effective_from IS NULL OR effective_to > effective_from),
    CONSTRAINT ck_crm_price_list_currency CHECK (currency ~ '^[A-Z]{3}$')
);
CREATE UNIQUE INDEX uk_crm_price_list_code_active ON crm_price_list(tenant_id, code) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_price_list_effective ON crm_price_list(tenant_id, status, effective_from, effective_to) WHERE deleted_at IS NULL;

CREATE TABLE crm_price_item (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    price_list_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    list_price NUMERIC(18,2) NOT NULL,
    minimum_price NUMERIC(18,2),
    tax_rate NUMERIC(5,4) NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT ck_crm_price_item_status CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT ck_crm_price_item_price CHECK (list_price >= 0 AND (minimum_price IS NULL OR minimum_price >= 0 AND minimum_price <= list_price)),
    CONSTRAINT ck_crm_price_item_tax_rate CHECK (tax_rate >= 0 AND tax_rate <= 1),
    CONSTRAINT fk_crm_price_item_price_list_tenant
        FOREIGN KEY (tenant_id, price_list_id) REFERENCES crm_price_list(tenant_id, id),
    CONSTRAINT fk_crm_price_item_product_tenant
        FOREIGN KEY (tenant_id, product_id) REFERENCES crm_product(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_price_item_product_active
    ON crm_price_item(tenant_id, price_list_id, product_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_price_item_product ON crm_price_item(tenant_id, product_id) WHERE deleted_at IS NULL;

CREATE OR REPLACE FUNCTION crm_seed_tenant_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES
        (p_tenant_id, 'lead:read', '查看线索', 'LEAD', 'READ'),
        (p_tenant_id, 'lead:create', '创建线索', 'LEAD', 'CREATE'),
        (p_tenant_id, 'lead:claim', '认领线索', 'LEAD', 'CLAIM'),
        (p_tenant_id, 'lead:release', '释放线索', 'LEAD', 'RELEASE'),
        (p_tenant_id, 'lead:assign', '分配线索', 'LEAD', 'ASSIGN'),
        (p_tenant_id, 'lead:transfer', '转移线索', 'LEAD', 'TRANSFER'),
        (p_tenant_id, 'lead:convert', '线索转客户', 'LEAD', 'CONVERT'),
        (p_tenant_id, 'lead:invalidate', '线索无效处理', 'LEAD', 'INVALIDATE'),
        (p_tenant_id, 'lead:batch', '批量操作线索', 'LEAD', 'BATCH'),
        (p_tenant_id, 'lead:export', '导出线索', 'LEAD', 'EXPORT'),
        (p_tenant_id, 'lead:handover', '交接线索', 'LEAD', 'HANDOVER'),
        (p_tenant_id, 'customer:read', '查看客户', 'CUSTOMER', 'READ'),
        (p_tenant_id, 'customer:create', '创建客户', 'CUSTOMER', 'CREATE'),
        (p_tenant_id, 'customer:claim', '认领客户', 'CUSTOMER', 'CLAIM'),
        (p_tenant_id, 'customer:release', '释放客户', 'CUSTOMER', 'RELEASE'),
        (p_tenant_id, 'customer:assign', '分配客户', 'CUSTOMER', 'ASSIGN'),
        (p_tenant_id, 'customer:transfer', '转移客户', 'CUSTOMER', 'TRANSFER'),
        (p_tenant_id, 'customer:merge', '合并客户', 'CUSTOMER', 'MERGE'),
        (p_tenant_id, 'customer:batch', '批量操作客户', 'CUSTOMER', 'BATCH'),
        (p_tenant_id, 'customer:export', '导出客户', 'CUSTOMER', 'EXPORT'),
        (p_tenant_id, 'customer:handover', '交接客户', 'CUSTOMER', 'HANDOVER'),
        (p_tenant_id, 'approval:manage', '管理审批', 'APPROVAL', 'MANAGE'),
        (p_tenant_id, 'audit:read', '查看审计日志', 'AUDIT', 'READ'),
        (p_tenant_id, 'outbox:retry', '重试事件投递', 'OUTBOX', 'RETRY'),
        (p_tenant_id, 'catalog:read', '查看产品与价格', 'CATALOG', 'READ'),
        (p_tenant_id, 'catalog:write', '维护产品与价格', 'CATALOG', 'WRITE'),
        (p_tenant_id, 'catalog:publish', '发布价目表', 'CATALOG', 'PUBLISH')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
SELECT crm_seed_tenant_permissions(id) FROM crm_tenant;
