-- Complete legacy organization adoption before authorization constraints are tightened.
DO $$
BEGIN
    IF to_regclass('public.department') IS NOT NULL THEN
        EXECUTE $sql$
            INSERT INTO crm_department (id, tenant_id, parent_id, name, path, status, created_at, updated_at)
            SELECT d.id, d.tenant_id, d.parent_id, d.name,
                   COALESCE(NULLIF(d.path, ''), '/' || d.id::text), d.status, d.created_at, d.updated_at
            FROM department d
            WHERE COALESCE(d.deleted, 0) = 0
              AND EXISTS (SELECT 1 FROM crm_tenant t WHERE t.id = d.tenant_id)
            ON CONFLICT (id) DO NOTHING
        $sql$;
    ELSIF to_regclass('public.departments') IS NOT NULL THEN
        EXECUTE $sql$
            INSERT INTO crm_department (id, tenant_id, parent_id, name, path, status, created_at, updated_at)
            SELECT d.id, d.tenant_id, d.parent_id, d.name,
                   COALESCE(NULLIF(d.path, ''), '/' || d.id::text), d.status, d.created_at, d.updated_at
            FROM departments d
            WHERE COALESCE(d.deleted, 0) = 0
              AND EXISTS (SELECT 1 FROM crm_tenant t WHERE t.id = d.tenant_id)
            ON CONFLICT (id) DO NOTHING
        $sql$;
    END IF;
END $$;

-- Every user belongs to a department. Legacy users without one are placed in a deterministic root department.
INSERT INTO crm_department (id, tenant_id, name, path, status, created_at, updated_at)
SELECT -(t.id * 1000 + 1), t.id, '默认部门', '/default', 1, now(), now()
FROM crm_tenant t
WHERE NOT EXISTS (SELECT 1 FROM crm_department d WHERE d.tenant_id = t.id AND d.deleted_at IS NULL)
ON CONFLICT (id) DO NOTHING;

UPDATE crm_user u
SET department_id = (
    SELECT d.id FROM crm_department d
    WHERE d.tenant_id = u.tenant_id AND d.deleted_at IS NULL
    ORDER BY CASE WHEN d.path = '/default' THEN 0 ELSE 1 END, d.id
    LIMIT 1
)
WHERE u.department_id IS NULL;

ALTER TABLE crm_tenant
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT;
ALTER TABLE crm_tenant
    ADD CONSTRAINT ck_crm_tenant_status CHECK (status IN (0, 1));

ALTER TABLE crm_department
    ADD COLUMN code VARCHAR(64),
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT;
UPDATE crm_department SET code = 'DEPT_' || id::text WHERE code IS NULL;
ALTER TABLE crm_department ALTER COLUMN code SET NOT NULL;
ALTER TABLE crm_department
    ADD CONSTRAINT ck_crm_department_status CHECK (status IN (0, 1));
CREATE UNIQUE INDEX uk_crm_department_code_active
    ON crm_department(tenant_id, code) WHERE deleted_at IS NULL;

ALTER TABLE crm_user
    ADD COLUMN username VARCHAR(100),
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT;
UPDATE crm_user SET username = 'user_' || id::text WHERE username IS NULL;
ALTER TABLE crm_user
    ALTER COLUMN username SET NOT NULL,
    ALTER COLUMN department_id SET NOT NULL;
ALTER TABLE crm_user
    ADD CONSTRAINT ck_crm_user_status CHECK (status IN (0, 1));
CREATE UNIQUE INDEX uk_crm_user_username_active
    ON crm_user(tenant_id, lower(username)) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_crm_user_mobile_active
    ON crm_user(tenant_id, mobile) WHERE deleted_at IS NULL AND mobile IS NOT NULL;
CREATE UNIQUE INDEX uk_crm_user_email_active
    ON crm_user(tenant_id, lower(email)) WHERE deleted_at IS NULL AND email IS NOT NULL;

ALTER TABLE crm_role
    ADD COLUMN data_scope VARCHAR(32) NOT NULL DEFAULT 'SELF',
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT;
ALTER TABLE crm_role
    ADD CONSTRAINT ck_crm_role_status CHECK (status IN (0, 1)),
    ADD CONSTRAINT ck_crm_role_data_scope
        CHECK (data_scope IN ('SELF', 'DEPARTMENT', 'DEPARTMENT_AND_SUB', 'ALL'));

-- V1 permissions were global. Expand each legacy permission into every existing tenant before changing the key.
ALTER TABLE crm_role_permission DROP CONSTRAINT crm_role_permission_permission_code_fkey;
ALTER TABLE crm_permission DROP CONSTRAINT crm_permission_pkey;
ALTER TABLE crm_permission
    ADD COLUMN tenant_id BIGINT,
    ADD COLUMN status SMALLINT NOT NULL DEFAULT 1,
    ADD COLUMN description VARCHAR(500),
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT;

INSERT INTO crm_permission (tenant_id, code, name, resource_type, action, status,
                            description, created_at, updated_at)
SELECT t.id, p.code, p.name, p.resource_type, p.action, p.status,
       p.description, p.created_at, p.updated_at
FROM crm_permission p
CROSS JOIN crm_tenant t
WHERE p.tenant_id IS NULL;
DELETE FROM crm_permission WHERE tenant_id IS NULL;

ALTER TABLE crm_permission
    ALTER COLUMN tenant_id SET NOT NULL,
    ADD CONSTRAINT crm_permission_pkey PRIMARY KEY (tenant_id, code),
    ADD CONSTRAINT fk_crm_permission_tenant FOREIGN KEY (tenant_id) REFERENCES crm_tenant(id),
    ADD CONSTRAINT ck_crm_permission_status CHECK (status IN (0, 1));

ALTER TABLE crm_user_role
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT;

ALTER TABLE crm_role_permission
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT,
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ADD COLUMN deleted_by BIGINT,
    ADD CONSTRAINT fk_crm_role_permission_permission
        FOREIGN KEY (tenant_id, permission_code) REFERENCES crm_permission(tenant_id, code);

CREATE TABLE crm_role_field_permission (
    tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id),
    role_id BIGINT NOT NULL,
    resource_type VARCHAR(64) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    can_view BOOLEAN NOT NULL DEFAULT FALSE,
    can_edit BOOLEAN NOT NULL DEFAULT FALSE,
    can_export BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT,
    PRIMARY KEY (tenant_id, role_id, resource_type, field_name),
    CHECK (can_edit = FALSE OR can_view = TRUE),
    CHECK (can_export = FALSE OR can_view = TRUE)
);

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
        (p_tenant_id, 'outbox:retry', '重试事件投递', 'OUTBOX', 'RETRY')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;

SELECT crm_seed_tenant_permissions(id) FROM crm_tenant;

CREATE OR REPLACE FUNCTION crm_seed_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    PERFORM crm_seed_tenant_permissions(NEW.id);
    RETURN NEW;
END $$;

CREATE TRIGGER trg_crm_tenant_seed_permissions
AFTER INSERT ON crm_tenant
FOR EACH ROW EXECUTE FUNCTION crm_seed_permissions_after_tenant_insert();
