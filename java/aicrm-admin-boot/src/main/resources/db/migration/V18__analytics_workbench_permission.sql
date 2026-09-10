-- Stage 4A: a read-only CRM workbench permission. No analytics projection or external-domain table is created.
INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
SELECT id, 'analytics:read', '查看销售工作台', 'ANALYTICS', 'READ'
FROM crm_tenant
ON CONFLICT (tenant_id, code) DO NOTHING;

CREATE OR REPLACE FUNCTION crm_seed_analytics_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES (p_tenant_id, 'analytics:read', '查看销售工作台', 'ANALYTICS', 'READ')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;

CREATE OR REPLACE FUNCTION crm_seed_analytics_permissions_after_tenant_insert()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    PERFORM crm_seed_analytics_permissions(NEW.id);
    RETURN NEW;
END $$;

CREATE TRIGGER trg_crm_tenant_seed_analytics_permissions
AFTER INSERT ON crm_tenant
FOR EACH ROW EXECUTE FUNCTION crm_seed_analytics_permissions_after_tenant_insert();
