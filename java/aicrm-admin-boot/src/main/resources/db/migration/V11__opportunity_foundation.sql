-- Stage 2B: opportunity pipeline. Opportunity belongs to sales and references only same-tenant sales master data.
CREATE UNIQUE INDEX uk_crm_contact_tenant_id ON crm_contact(tenant_id, id);
CREATE TABLE crm_opportunity (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    opportunity_no VARCHAR(32) NOT NULL,
    name VARCHAR(200) NOT NULL,
    customer_id BIGINT NOT NULL,
    contact_id BIGINT,
    source_lead_id BIGINT,
    stage VARCHAR(32) NOT NULL DEFAULT 'DISCOVERY',
    status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    expected_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    currency CHAR(3) NOT NULL,
    probability SMALLINT NOT NULL DEFAULT 0,
    expected_close_date DATE,
    owner_user_id BIGINT NOT NULL,
    owner_dept_id BIGINT NOT NULL,
    lost_reason VARCHAR(500),
    lost_at TIMESTAMPTZ,
    won_at TIMESTAMPTZ,
    extension JSONB NOT NULL DEFAULT '{}'::jsonb,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_opportunity_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_opportunity_stage CHECK (stage IN ('DISCOVERY', 'QUALIFICATION', 'SOLUTION', 'QUOTATION', 'NEGOTIATION', 'CLOSED_WON', 'CLOSED_LOST')),
    CONSTRAINT ck_crm_opportunity_status CHECK (status IN ('OPEN', 'WON', 'LOST')),
    CONSTRAINT ck_crm_opportunity_amount CHECK (expected_amount >= 0),
    CONSTRAINT ck_crm_opportunity_probability CHECK (probability >= 0 AND probability <= 100),
    CONSTRAINT ck_crm_opportunity_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_crm_opportunity_terminal_shape CHECK (
        (status = 'OPEN' AND stage NOT IN ('CLOSED_WON', 'CLOSED_LOST') AND lost_reason IS NULL AND lost_at IS NULL AND won_at IS NULL)
        OR (status = 'WON' AND stage = 'CLOSED_WON' AND probability = 100 AND lost_reason IS NULL AND lost_at IS NULL AND won_at IS NOT NULL)
        OR (status = 'LOST' AND stage = 'CLOSED_LOST' AND lost_reason IS NOT NULL AND lost_at IS NOT NULL AND won_at IS NULL)
    ),
    CONSTRAINT fk_crm_opportunity_customer_tenant FOREIGN KEY (tenant_id, customer_id) REFERENCES crm_customer(tenant_id, id),
    CONSTRAINT fk_crm_opportunity_contact_tenant FOREIGN KEY (tenant_id, contact_id) REFERENCES crm_contact(tenant_id, id),
    CONSTRAINT fk_crm_opportunity_lead_tenant FOREIGN KEY (tenant_id, source_lead_id) REFERENCES crm_lead(tenant_id, id),
    CONSTRAINT fk_crm_opportunity_owner_tenant FOREIGN KEY (tenant_id, owner_user_id) REFERENCES crm_user(tenant_id, id),
    CONSTRAINT fk_crm_opportunity_owner_dept_tenant FOREIGN KEY (tenant_id, owner_dept_id) REFERENCES crm_department(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_opportunity_no_active ON crm_opportunity(tenant_id, opportunity_no) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_opportunity_owner_pipeline ON crm_opportunity(tenant_id, owner_user_id, status, stage, expected_close_date) WHERE deleted_at IS NULL;
CREATE INDEX idx_crm_opportunity_customer_pipeline ON crm_opportunity(tenant_id, customer_id, status, expected_close_date) WHERE deleted_at IS NULL;

CREATE TABLE crm_opportunity_stage_history (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    opportunity_id BIGINT NOT NULL,
    action VARCHAR(32) NOT NULL,
    from_stage VARCHAR(32),
    to_stage VARCHAR(32) NOT NULL,
    from_status VARCHAR(16),
    to_status VARCHAR(16) NOT NULL,
    from_probability SMALLINT,
    to_probability SMALLINT NOT NULL,
    reason VARCHAR(500),
    operator_user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_crm_opportunity_stage_history_action CHECK (action IN ('CREATE', 'STAGE_CHANGED', 'WON', 'LOST', 'RESTARTED')),
    CONSTRAINT fk_crm_opportunity_stage_history_opportunity_tenant
        FOREIGN KEY (tenant_id, opportunity_id) REFERENCES crm_opportunity(tenant_id, id),
    CONSTRAINT fk_crm_opportunity_stage_history_operator_tenant
        FOREIGN KEY (tenant_id, operator_user_id) REFERENCES crm_user(tenant_id, id)
);
CREATE INDEX idx_crm_opportunity_stage_history_timeline
    ON crm_opportunity_stage_history(tenant_id, opportunity_id, created_at, id);
CREATE TRIGGER trg_crm_opportunity_stage_history_append_only
BEFORE UPDATE OR DELETE ON crm_opportunity_stage_history
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

CREATE OR REPLACE FUNCTION crm_validate_opportunity_reference()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM crm_customer c WHERE c.tenant_id = NEW.tenant_id AND c.id = NEW.customer_id
                   AND c.status = 'ACTIVE' AND c.deleted_at IS NULL AND c.merged_into_customer_id IS NULL) THEN
        RAISE EXCEPTION 'opportunity customer must be an active customer in the same tenant' USING ERRCODE = '23514';
    END IF;
    IF NEW.contact_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM crm_contact c WHERE c.tenant_id = NEW.tenant_id
                   AND c.id = NEW.contact_id AND c.customer_id = NEW.customer_id AND c.deleted_at IS NULL) THEN
        RAISE EXCEPTION 'opportunity contact must belong to its customer in the same tenant' USING ERRCODE = '23514';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM crm_user u WHERE u.tenant_id = NEW.tenant_id AND u.id = NEW.owner_user_id
                   AND u.status = 1 AND u.deleted_at IS NULL) THEN
        RAISE EXCEPTION 'opportunity owner must be active in the same tenant' USING ERRCODE = '23514';
    END IF;
    SELECT department_id INTO NEW.owner_dept_id FROM crm_user WHERE tenant_id = NEW.tenant_id AND id = NEW.owner_user_id;
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_opportunity_validate_reference
BEFORE INSERT OR UPDATE OF customer_id, contact_id, owner_user_id ON crm_opportunity
FOR EACH ROW EXECUTE FUNCTION crm_validate_opportunity_reference();

CREATE OR REPLACE FUNCTION crm_require_opportunity_journal()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM crm_opportunity_stage_history h WHERE h.tenant_id = NEW.tenant_id
                   AND h.opportunity_id = NEW.id AND h.created_at >= transaction_timestamp()) THEN
        RAISE EXCEPTION 'opportunity change requires stage history in the same transaction' USING ERRCODE = '23514';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM crm_audit_log a WHERE a.tenant_id = NEW.tenant_id AND a.resource_type = 'OPPORTUNITY'
                   AND a.resource_id = NEW.id AND a.created_at >= transaction_timestamp()) THEN
        RAISE EXCEPTION 'opportunity change requires audit log in the same transaction' USING ERRCODE = '23514';
    END IF;
    RETURN NULL;
END $$;
CREATE CONSTRAINT TRIGGER trg_crm_opportunity_journal_insert
AFTER INSERT ON crm_opportunity DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION crm_require_opportunity_journal();
CREATE CONSTRAINT TRIGGER trg_crm_opportunity_journal_change
AFTER UPDATE OF stage, status, probability ON crm_opportunity DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION crm_require_opportunity_journal();

CREATE OR REPLACE FUNCTION crm_seed_tenant_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES
        (p_tenant_id, 'lead:read', '查看线索', 'LEAD', 'READ'), (p_tenant_id, 'lead:create', '创建线索', 'LEAD', 'CREATE'),
        (p_tenant_id, 'lead:claim', '认领线索', 'LEAD', 'CLAIM'), (p_tenant_id, 'lead:release', '释放线索', 'LEAD', 'RELEASE'),
        (p_tenant_id, 'lead:assign', '分配线索', 'LEAD', 'ASSIGN'), (p_tenant_id, 'lead:transfer', '转移线索', 'LEAD', 'TRANSFER'),
        (p_tenant_id, 'lead:convert', '线索转客户', 'LEAD', 'CONVERT'), (p_tenant_id, 'lead:invalidate', '线索无效处理', 'LEAD', 'INVALIDATE'),
        (p_tenant_id, 'lead:batch', '批量操作线索', 'LEAD', 'BATCH'), (p_tenant_id, 'lead:export', '导出线索', 'LEAD', 'EXPORT'),
        (p_tenant_id, 'lead:handover', '交接线索', 'LEAD', 'HANDOVER'),
        (p_tenant_id, 'customer:read', '查看客户', 'CUSTOMER', 'READ'), (p_tenant_id, 'customer:create', '创建客户', 'CUSTOMER', 'CREATE'),
        (p_tenant_id, 'customer:claim', '认领客户', 'CUSTOMER', 'CLAIM'), (p_tenant_id, 'customer:release', '释放客户', 'CUSTOMER', 'RELEASE'),
        (p_tenant_id, 'customer:assign', '分配客户', 'CUSTOMER', 'ASSIGN'), (p_tenant_id, 'customer:transfer', '转移客户', 'CUSTOMER', 'TRANSFER'),
        (p_tenant_id, 'customer:merge', '合并客户', 'CUSTOMER', 'MERGE'), (p_tenant_id, 'customer:batch', '批量操作客户', 'CUSTOMER', 'BATCH'),
        (p_tenant_id, 'customer:export', '导出客户', 'CUSTOMER', 'EXPORT'), (p_tenant_id, 'customer:handover', '交接客户', 'CUSTOMER', 'HANDOVER'),
        (p_tenant_id, 'approval:manage', '管理审批', 'APPROVAL', 'MANAGE'), (p_tenant_id, 'audit:read', '查看审计日志', 'AUDIT', 'READ'),
        (p_tenant_id, 'outbox:retry', '重试事件投递', 'OUTBOX', 'RETRY'),
        (p_tenant_id, 'catalog:read', '查看产品与价格', 'CATALOG', 'READ'), (p_tenant_id, 'catalog:write', '维护产品与价格', 'CATALOG', 'WRITE'),
        (p_tenant_id, 'catalog:publish', '发布价目表', 'CATALOG', 'PUBLISH'),
        (p_tenant_id, 'opportunity:read:own', '查看本人商机', 'OPPORTUNITY', 'READ_OWN'), (p_tenant_id, 'opportunity:read:any', '查看全部商机', 'OPPORTUNITY', 'READ_ANY'),
        (p_tenant_id, 'opportunity:create', '创建商机', 'OPPORTUNITY', 'CREATE'), (p_tenant_id, 'opportunity:write:own', '维护本人商机', 'OPPORTUNITY', 'WRITE_OWN'),
        (p_tenant_id, 'opportunity:write:any', '维护全部商机', 'OPPORTUNITY', 'WRITE_ANY'), (p_tenant_id, 'opportunity:stage', '推进商机阶段', 'OPPORTUNITY', 'STAGE'),
        (p_tenant_id, 'opportunity:win', '赢单商机', 'OPPORTUNITY', 'WIN'), (p_tenant_id, 'opportunity:lose', '输单商机', 'OPPORTUNITY', 'LOSE'),
        (p_tenant_id, 'opportunity:restart', '重启输单商机', 'OPPORTUNITY', 'RESTART')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
SELECT crm_seed_tenant_permissions(id) FROM crm_tenant;
