-- A private resource has no pool reference, therefore each tenant/resource pair
-- needs one unambiguous active auto-recycle destination.
CREATE UNIQUE INDEX uk_crm_public_pool_auto_recycle_target
    ON crm_public_pool(tenant_id, resource_type)
    WHERE deleted_at IS NULL AND status = 1 AND auto_recycle_enabled = TRUE;

-- Scheduled jobs need a tenant-scoped, auditable operator that remains distinct
-- from the employee whose records are being recycled.
CREATE SEQUENCE crm_system_department_id_seq START WITH 7000000000000000000 INCREMENT BY 1 NO CYCLE;
CREATE SEQUENCE crm_system_user_id_seq START WITH 8000000000000000000 INCREMENT BY 1 NO CYCLE;

CREATE OR REPLACE FUNCTION crm_ensure_tenant_automation_actor(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE plpgsql AS $$
DECLARE
    v_department_id BIGINT;
BEGIN
    SELECT id INTO v_department_id
    FROM crm_department
    WHERE tenant_id = p_tenant_id AND status = 1 AND deleted_at IS NULL
    ORDER BY id
    LIMIT 1;

    IF v_department_id IS NULL THEN
        v_department_id := nextval('crm_system_department_id_seq');
        INSERT INTO crm_department (id, tenant_id, code, name, path, status, created_at, updated_at)
        VALUES (v_department_id, p_tenant_id, '__SYSTEM__', '系统任务', '/_system', 1, now(), now());
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM crm_user
        WHERE tenant_id = p_tenant_id AND username = '__system__' AND deleted_at IS NULL
    ) THEN
        INSERT INTO crm_user (id, tenant_id, department_id, username, name, status, created_at, updated_at)
        VALUES (nextval('crm_system_user_id_seq'), p_tenant_id, v_department_id,
                '__system__', '系统任务', 1, now(), now());
    END IF;
END $$;

SELECT crm_ensure_tenant_automation_actor(id) FROM crm_tenant WHERE deleted_at IS NULL;

CREATE OR REPLACE FUNCTION crm_create_tenant_automation_actor()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    PERFORM crm_ensure_tenant_automation_actor(NEW.id);
    RETURN NEW;
END $$;

CREATE TRIGGER trg_crm_tenant_automation_actor
AFTER INSERT ON crm_tenant
FOR EACH ROW EXECUTE FUNCTION crm_create_tenant_automation_actor();
