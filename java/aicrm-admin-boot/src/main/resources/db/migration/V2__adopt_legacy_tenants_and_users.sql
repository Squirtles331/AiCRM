DO $$
BEGIN
    IF to_regclass('public.tenant') IS NOT NULL THEN
        INSERT INTO crm_tenant (id, name, status, created_at, updated_at)
        SELECT id, name, status, created_at, updated_at
        FROM tenant
        WHERE deleted = 0
        ON CONFLICT (id) DO NOTHING;
    END IF;

    IF to_regclass('public.users') IS NOT NULL THEN
        INSERT INTO crm_user (id, tenant_id, name, mobile, email, status, created_at, updated_at)
        SELECT u.id, u.tenant_id, u.name, u.mobile, u.email, u.status, u.created_at, u.updated_at
        FROM users u
        WHERE u.deleted = 0
          AND EXISTS (SELECT 1 FROM crm_tenant t WHERE t.id = u.tenant_id)
        ON CONFLICT (id) DO NOTHING;

        INSERT INTO crm_role (id, tenant_id, code, name)
        SELECT DISTINCT
            (u.tenant_id * 1000) + CASE u.role_code WHEN 'admin' THEN 1 WHEN 'supervisor' THEN 2 ELSE 3 END,
            u.tenant_id,
            u.role_code,
            CASE u.role_code WHEN 'admin' THEN '超级管理员' WHEN 'supervisor' THEN '销售主管' ELSE '销售' END
        FROM users u
        WHERE u.deleted = 0
          AND EXISTS (SELECT 1 FROM crm_tenant t WHERE t.id = u.tenant_id)
        ON CONFLICT (id) DO NOTHING;

        INSERT INTO crm_user_role (tenant_id, user_id, role_id)
        SELECT u.tenant_id, u.id,
            (u.tenant_id * 1000) + CASE u.role_code WHEN 'admin' THEN 1 WHEN 'supervisor' THEN 2 ELSE 3 END
        FROM users u
        WHERE u.deleted = 0
          AND EXISTS (SELECT 1 FROM crm_user cu WHERE cu.id = u.id)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;
