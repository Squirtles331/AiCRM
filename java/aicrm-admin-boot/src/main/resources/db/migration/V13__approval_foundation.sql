-- Stage 2D: reusable sequential approval definitions and runtime facts.
CREATE TABLE crm_approval_definition (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    code VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    definition_version INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_approval_definition_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_approval_definition_version UNIQUE (tenant_id, resource_type, code, definition_version),
    CONSTRAINT ck_crm_approval_definition_status CHECK (status IN ('DRAFT', 'ACTIVE', 'RETIRED', 'DISABLED')),
    CONSTRAINT ck_crm_approval_definition_version CHECK (definition_version >= 1)
);
CREATE UNIQUE INDEX uk_crm_approval_definition_active
    ON crm_approval_definition(tenant_id, resource_type, code)
    WHERE status = 'ACTIVE' AND deleted_at IS NULL;

CREATE TABLE crm_approval_definition_node (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    definition_id BIGINT NOT NULL,
    node_no INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    decision_mode VARCHAR(16) NOT NULL DEFAULT 'ALL',
    approver_user_id BIGINT NOT NULL,
    condition_expression JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_approval_definition_node_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_approval_definition_node_user UNIQUE (tenant_id, definition_id, node_no, approver_user_id),
    CONSTRAINT ck_crm_approval_definition_node_no CHECK (node_no >= 1),
    CONSTRAINT ck_crm_approval_definition_node_mode CHECK (decision_mode IN ('ANY', 'ALL')),
    CONSTRAINT fk_crm_approval_definition_node_definition FOREIGN KEY (tenant_id, definition_id)
        REFERENCES crm_approval_definition(tenant_id, id),
    CONSTRAINT fk_crm_approval_definition_node_approver FOREIGN KEY (tenant_id, approver_user_id)
        REFERENCES crm_user(tenant_id, id)
);
CREATE INDEX idx_crm_approval_definition_node_definition
    ON crm_approval_definition_node(tenant_id, definition_id, node_no) WHERE deleted_at IS NULL;

CREATE TABLE crm_approval_instance (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    resource_type VARCHAR(32) NOT NULL,
    resource_id BIGINT NOT NULL,
    definition_id BIGINT NOT NULL,
    definition_code VARCHAR(64) NOT NULL,
    definition_version INTEGER NOT NULL,
    definition_snapshot JSONB NOT NULL,
    applicant_user_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    current_node_no INTEGER,
    version BIGINT NOT NULL DEFAULT 0,
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_approval_instance_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT ck_crm_approval_instance_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'WITHDRAWN')),
    CONSTRAINT ck_crm_approval_instance_node CHECK (current_node_no IS NULL OR current_node_no >= 1),
    CONSTRAINT fk_crm_approval_instance_definition FOREIGN KEY (tenant_id, definition_id)
        REFERENCES crm_approval_definition(tenant_id, id),
    CONSTRAINT fk_crm_approval_instance_applicant FOREIGN KEY (tenant_id, applicant_user_id)
        REFERENCES crm_user(tenant_id, id)
);
CREATE UNIQUE INDEX uk_crm_approval_instance_pending_resource
    ON crm_approval_instance(tenant_id, resource_type, resource_id)
    WHERE status = 'PENDING' AND deleted_at IS NULL;
CREATE INDEX idx_crm_approval_instance_resource
    ON crm_approval_instance(tenant_id, resource_type, resource_id, created_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_approval_task (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    instance_id BIGINT NOT NULL,
    node_no INTEGER NOT NULL,
    node_name VARCHAR(100) NOT NULL,
    decision_mode VARCHAR(16) NOT NULL,
    approver_user_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    version BIGINT NOT NULL DEFAULT 0,
    transferred_from_task_id BIGINT,
    acted_at TIMESTAMPTZ,
    comment VARCHAR(1000),
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    CONSTRAINT uk_crm_approval_task_tenant_id UNIQUE (tenant_id, id),
    CONSTRAINT uk_crm_approval_task_node_user UNIQUE (tenant_id, instance_id, node_no, approver_user_id),
    CONSTRAINT ck_crm_approval_task_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'TRANSFERRED', 'CANCELLED')),
    CONSTRAINT ck_crm_approval_task_mode CHECK (decision_mode IN ('ANY', 'ALL')),
    CONSTRAINT fk_crm_approval_task_instance FOREIGN KEY (tenant_id, instance_id)
        REFERENCES crm_approval_instance(tenant_id, id),
    CONSTRAINT fk_crm_approval_task_approver FOREIGN KEY (tenant_id, approver_user_id)
        REFERENCES crm_user(tenant_id, id),
    CONSTRAINT fk_crm_approval_task_transferred_from FOREIGN KEY (tenant_id, transferred_from_task_id)
        REFERENCES crm_approval_task(tenant_id, id)
);
CREATE INDEX idx_crm_approval_task_inbox
    ON crm_approval_task(tenant_id, approver_user_id, status, created_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_approval_action (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    instance_id BIGINT NOT NULL,
    task_id BIGINT,
    action VARCHAR(16) NOT NULL,
    actor_user_id BIGINT NOT NULL,
    comment VARCHAR(1000),
    before_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    after_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    operation_id VARCHAR(64),
    trace_id VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_crm_approval_action_action CHECK (action IN ('START', 'APPROVE', 'REJECT', 'WITHDRAW', 'TRANSFER', 'ACTIVATE_NODE')),
    CONSTRAINT fk_crm_approval_action_instance FOREIGN KEY (tenant_id, instance_id)
        REFERENCES crm_approval_instance(tenant_id, id),
    CONSTRAINT fk_crm_approval_action_task FOREIGN KEY (tenant_id, task_id)
        REFERENCES crm_approval_task(tenant_id, id),
    CONSTRAINT fk_crm_approval_action_actor FOREIGN KEY (tenant_id, actor_user_id)
        REFERENCES crm_user(tenant_id, id)
);
CREATE INDEX idx_crm_approval_action_timeline
    ON crm_approval_action(tenant_id, instance_id, created_at, id);
CREATE TRIGGER trg_crm_approval_action_append_only
BEFORE UPDATE OR DELETE ON crm_approval_action
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT t.id, p.code, p.name, p.resource_type, p.action
FROM crm_tenant t
CROSS JOIN (VALUES
    ('approval:manage', '管理审批定义', 'APPROVAL', 'MANAGE'),
    ('approval:task:read', '查看待审批任务', 'APPROVAL_TASK', 'READ'),
    ('approval:task:act', '处理审批任务', 'APPROVAL_TASK', 'ACT'),
    ('quote:withdraw', '撤回报价审批', 'QUOTE', 'WITHDRAW')
) p(code, name, resource_type, action)
ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_approval_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES
        (p_tenant_id, 'approval:manage', '管理审批定义', 'APPROVAL', 'MANAGE'),
        (p_tenant_id, 'approval:task:read', '查看待审批任务', 'APPROVAL_TASK', 'READ'),
        (p_tenant_id, 'approval:task:act', '处理审批任务', 'APPROVAL_TASK', 'ACT'),
        (p_tenant_id, 'quote:withdraw', '撤回报价审批', 'QUOTE', 'WITHDRAW')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
SELECT crm_seed_approval_permissions(id) FROM crm_tenant;
CREATE OR REPLACE FUNCTION crm_seed_approval_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    PERFORM crm_seed_approval_permissions(NEW.id);
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_tenant_seed_approval_permissions
AFTER INSERT ON crm_tenant
FOR EACH ROW EXECUTE FUNCTION crm_seed_approval_permissions_after_tenant_insert();

-- A quotation may only be rejected by an approval task. Retire the former direct-rejection permission
-- and replace the V12 tenant seed function so future tenants do not receive it.
DELETE FROM crm_role_permission WHERE permission_code = 'quote:reject';
DELETE FROM crm_permission WHERE code = 'quote:reject';
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
        (p_tenant_id, 'quote:expire', '报价过期处理', 'QUOTE', 'EXPIRE')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;
