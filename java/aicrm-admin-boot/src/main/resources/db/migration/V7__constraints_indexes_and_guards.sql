-- Composite candidate keys allow foreign keys to prove tenant isolation.
CREATE UNIQUE INDEX uk_crm_department_tenant_id ON crm_department(tenant_id, id);
CREATE UNIQUE INDEX uk_crm_user_tenant_id ON crm_user(tenant_id, id);
CREATE UNIQUE INDEX uk_crm_role_tenant_id ON crm_role(tenant_id, id);
CREATE UNIQUE INDEX uk_crm_pool_tenant_id ON crm_public_pool(tenant_id, id);
CREATE UNIQUE INDEX uk_crm_lead_tenant_id ON crm_lead(tenant_id, id);
CREATE UNIQUE INDEX uk_crm_customer_tenant_id ON crm_customer(tenant_id, id);

ALTER TABLE crm_department
    ADD CONSTRAINT fk_crm_department_parent_tenant
        FOREIGN KEY (tenant_id, parent_id) REFERENCES crm_department(tenant_id, id);
ALTER TABLE crm_user
    ADD CONSTRAINT fk_crm_user_department_tenant
        FOREIGN KEY (tenant_id, department_id) REFERENCES crm_department(tenant_id, id);
ALTER TABLE crm_user_role
    ADD CONSTRAINT fk_crm_user_role_user_tenant
        FOREIGN KEY (tenant_id, user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_user_role_role_tenant
        FOREIGN KEY (tenant_id, role_id) REFERENCES crm_role(tenant_id, id);
ALTER TABLE crm_role_permission
    ADD CONSTRAINT fk_crm_role_permission_role_tenant
        FOREIGN KEY (tenant_id, role_id) REFERENCES crm_role(tenant_id, id);
ALTER TABLE crm_role_field_permission
    ADD CONSTRAINT fk_crm_role_field_permission_role_tenant
        FOREIGN KEY (tenant_id, role_id) REFERENCES crm_role(tenant_id, id);

ALTER TABLE crm_lead
    ADD CONSTRAINT fk_crm_lead_owner_tenant
        FOREIGN KEY (tenant_id, owner_user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_lead_owner_dept_tenant
        FOREIGN KEY (tenant_id, owner_dept_id) REFERENCES crm_department(tenant_id, id),
    ADD CONSTRAINT fk_crm_lead_pool_tenant
        FOREIGN KEY (tenant_id, public_pool_id) REFERENCES crm_public_pool(tenant_id, id),
    ADD CONSTRAINT fk_crm_lead_customer_tenant
        FOREIGN KEY (tenant_id, customer_id) REFERENCES crm_customer(tenant_id, id);
ALTER TABLE crm_customer
    ADD CONSTRAINT fk_crm_customer_owner_tenant
        FOREIGN KEY (tenant_id, owner_user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_customer_owner_dept_tenant
        FOREIGN KEY (tenant_id, owner_dept_id) REFERENCES crm_department(tenant_id, id),
    ADD CONSTRAINT fk_crm_customer_pool_tenant
        FOREIGN KEY (tenant_id, public_pool_id) REFERENCES crm_public_pool(tenant_id, id),
    ADD CONSTRAINT fk_crm_customer_merge_target_tenant
        FOREIGN KEY (tenant_id, merged_into_customer_id) REFERENCES crm_customer(tenant_id, id);
ALTER TABLE crm_contact
    ADD CONSTRAINT fk_crm_contact_customer_tenant
        FOREIGN KEY (tenant_id, customer_id) REFERENCES crm_customer(tenant_id, id),
    ADD CONSTRAINT fk_crm_contact_source_customer_tenant
        FOREIGN KEY (tenant_id, source_customer_id) REFERENCES crm_customer(tenant_id, id);
ALTER TABLE crm_follow_up
    ADD CONSTRAINT fk_crm_follow_up_lead_tenant
        FOREIGN KEY (tenant_id, lead_id) REFERENCES crm_lead(tenant_id, id),
    ADD CONSTRAINT fk_crm_follow_up_customer_tenant
        FOREIGN KEY (tenant_id, customer_id) REFERENCES crm_customer(tenant_id, id),
    ADD CONSTRAINT fk_crm_follow_up_actor_tenant
        FOREIGN KEY (tenant_id, actor_user_id) REFERENCES crm_user(tenant_id, id);
ALTER TABLE crm_resource_handover
    ADD CONSTRAINT fk_crm_handover_from_user_tenant
        FOREIGN KEY (tenant_id, from_user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_handover_to_user_tenant
        FOREIGN KEY (tenant_id, to_user_id) REFERENCES crm_user(tenant_id, id);
ALTER TABLE crm_ownership_history
    ADD CONSTRAINT fk_crm_ownership_operator_tenant
        FOREIGN KEY (tenant_id, operator_user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_ownership_from_owner_tenant
        FOREIGN KEY (tenant_id, from_owner_user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_ownership_to_owner_tenant
        FOREIGN KEY (tenant_id, to_owner_user_id) REFERENCES crm_user(tenant_id, id),
    ADD CONSTRAINT fk_crm_ownership_from_pool_tenant
        FOREIGN KEY (tenant_id, from_pool_id) REFERENCES crm_public_pool(tenant_id, id),
    ADD CONSTRAINT fk_crm_ownership_to_pool_tenant
        FOREIGN KEY (tenant_id, to_pool_id) REFERENCES crm_public_pool(tenant_id, id);
ALTER TABLE crm_audit_log
    ADD CONSTRAINT fk_crm_audit_actor_tenant
        FOREIGN KEY (tenant_id, actor_user_id) REFERENCES crm_user(tenant_id, id);

-- Normalize earlier ownership rows before tightening the invariant.
UPDATE crm_lead l
SET owner_dept_id = u.department_id
FROM crm_user u
WHERE l.ownership_type = 'PRIVATE' AND u.tenant_id = l.tenant_id AND u.id = l.owner_user_id;
UPDATE crm_customer c
SET owner_dept_id = u.department_id
FROM crm_user u
WHERE c.ownership_type = 'PRIVATE' AND u.tenant_id = c.tenant_id AND u.id = c.owner_user_id;
UPDATE crm_lead
SET owner_dept_id = NULL, pool_entered_at = COALESCE(pool_entered_at, created_at)
WHERE ownership_type = 'PUBLIC';
UPDATE crm_customer
SET owner_dept_id = NULL, pool_entered_at = COALESCE(pool_entered_at, created_at)
WHERE ownership_type = 'PUBLIC';

ALTER TABLE crm_lead DROP CONSTRAINT crm_lead_check;
ALTER TABLE crm_customer DROP CONSTRAINT crm_customer_check;
ALTER TABLE crm_lead
    ADD CONSTRAINT ck_crm_lead_ownership CHECK (
        (ownership_type = 'PRIVATE' AND owner_user_id IS NOT NULL AND owner_dept_id IS NOT NULL
            AND public_pool_id IS NULL AND pool_entered_at IS NULL)
        OR (ownership_type = 'PUBLIC' AND owner_user_id IS NULL AND owner_dept_id IS NULL
            AND public_pool_id IS NOT NULL AND pool_entered_at IS NOT NULL)
    );
ALTER TABLE crm_customer
    ADD CONSTRAINT ck_crm_customer_ownership CHECK (
        (ownership_type = 'PRIVATE' AND owner_user_id IS NOT NULL AND owner_dept_id IS NOT NULL
            AND public_pool_id IS NULL AND pool_entered_at IS NULL)
        OR (ownership_type = 'PUBLIC' AND owner_user_id IS NULL AND owner_dept_id IS NULL
            AND public_pool_id IS NOT NULL AND pool_entered_at IS NOT NULL)
    );

CREATE OR REPLACE FUNCTION crm_validate_sales_ownership()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
    v_department_id BIGINT;
    v_pool_status SMALLINT;
    v_pool_claim BOOLEAN;
    v_pool_assign BOOLEAN;
    v_pool_release BOOLEAN;
    v_pool_recycle BOOLEAN;
    v_resource_type VARCHAR(20) := CASE WHEN TG_TABLE_NAME = 'crm_lead' THEN 'LEAD' ELSE 'CUSTOMER' END;
BEGIN
    IF NEW.ownership_type = 'PRIVATE' THEN
        SELECT department_id INTO v_department_id
        FROM crm_user
        WHERE tenant_id = NEW.tenant_id AND id = NEW.owner_user_id
          AND status = 1 AND deleted_at IS NULL;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'private owner must be an active user in the same tenant' USING ERRCODE = '23514';
        END IF;
        NEW.owner_dept_id := v_department_id;
        NEW.public_pool_id := NULL;
        NEW.pool_entered_at := NULL;
    ELSIF NEW.ownership_type = 'PUBLIC' THEN
        SELECT status, claim_enabled, assign_enabled, release_enabled, auto_recycle_enabled
        INTO v_pool_status, v_pool_claim, v_pool_assign, v_pool_release, v_pool_recycle
        FROM crm_public_pool
        WHERE tenant_id = NEW.tenant_id AND id = NEW.public_pool_id
          AND resource_type = v_resource_type AND deleted_at IS NULL;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'public pool must match tenant and resource type' USING ERRCODE = '23514';
        END IF;
        IF TG_OP = 'INSERT' AND v_pool_status <> 1 THEN
            RAISE EXCEPTION 'resource cannot enter a disabled public pool' USING ERRCODE = '23514';
        END IF;
        IF TG_OP = 'UPDATE'
           AND (OLD.ownership_type IS DISTINCT FROM NEW.ownership_type
                OR OLD.public_pool_id IS DISTINCT FROM NEW.public_pool_id)
           AND (v_pool_status <> 1 OR NOT (v_pool_release OR v_pool_recycle)) THEN
            RAISE EXCEPTION 'release or recycle is disabled for this public pool' USING ERRCODE = '23514';
        END IF;
        NEW.owner_user_id := NULL;
        NEW.owner_dept_id := NULL;
        NEW.pool_entered_at := COALESCE(NEW.pool_entered_at, now());
    END IF;

    IF TG_OP = 'UPDATE' AND OLD.ownership_type = 'PUBLIC' AND NEW.ownership_type = 'PRIVATE' THEN
        SELECT status, claim_enabled, assign_enabled INTO v_pool_status, v_pool_claim, v_pool_assign
        FROM crm_public_pool
        WHERE tenant_id = OLD.tenant_id AND id = OLD.public_pool_id AND deleted_at IS NULL;
        IF NOT FOUND OR v_pool_status <> 1 OR NOT (v_pool_claim OR v_pool_assign) THEN
            RAISE EXCEPTION 'claim and assignment are disabled for this public pool' USING ERRCODE = '23514';
        END IF;
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_crm_lead_validate_ownership
BEFORE INSERT OR UPDATE OF ownership_type, owner_user_id, owner_dept_id, public_pool_id, pool_entered_at
ON crm_lead FOR EACH ROW EXECUTE FUNCTION crm_validate_sales_ownership();
CREATE TRIGGER trg_crm_customer_validate_ownership
BEFORE INSERT OR UPDATE OF ownership_type, owner_user_id, owner_dept_id, public_pool_id, pool_entered_at
ON crm_customer FOR EACH ROW EXECUTE FUNCTION crm_validate_sales_ownership();

CREATE OR REPLACE FUNCTION crm_guard_lead_terminal_state()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF OLD.status IN ('CONVERTED', 'INVALID')
       AND ROW(NEW.status, NEW.ownership_type, NEW.owner_user_id, NEW.owner_dept_id,
               NEW.public_pool_id, NEW.last_follow_up_at, NEW.next_follow_up_at)
           IS DISTINCT FROM
           ROW(OLD.status, OLD.ownership_type, OLD.owner_user_id, OLD.owner_dept_id,
               OLD.public_pool_id, OLD.last_follow_up_at, OLD.next_follow_up_at) THEN
        RAISE EXCEPTION 'terminal lead cannot be claimed, released, converted, invalidated or followed up again'
            USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_lead_terminal_state
BEFORE UPDATE ON crm_lead FOR EACH ROW EXECUTE FUNCTION crm_guard_lead_terminal_state();

CREATE OR REPLACE FUNCTION crm_validate_customer_merge()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.merged_into_customer_id IS NOT NULL
       AND OLD.merged_into_customer_id IS NULL THEN
        IF NEW.merged_into_customer_id = NEW.id OR NOT EXISTS (
            SELECT 1 FROM crm_customer target
            WHERE target.tenant_id = NEW.tenant_id
              AND target.id = NEW.merged_into_customer_id
              AND target.deleted_at IS NULL
              AND target.merged_into_customer_id IS NULL
              AND target.status = 'ACTIVE'
        ) THEN
            RAISE EXCEPTION 'merge target must be another active customer in the same tenant'
                USING ERRCODE = '23514';
        END IF;
    END IF;
    RETURN NEW;
END $$;
CREATE TRIGGER trg_crm_customer_validate_merge
BEFORE UPDATE OF merged_into_customer_id ON crm_customer
FOR EACH ROW EXECUTE FUNCTION crm_validate_customer_merge();

CREATE OR REPLACE FUNCTION crm_reject_immutable_change()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    RAISE EXCEPTION '% is append-only', TG_TABLE_NAME USING ERRCODE = '55000';
END $$;
CREATE TRIGGER trg_crm_ownership_history_append_only
BEFORE UPDATE OR DELETE ON crm_ownership_history
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();
CREATE TRIGGER trg_crm_audit_log_append_only
BEFORE UPDATE OR DELETE ON crm_audit_log
FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

-- A changed sales aggregate must be journaled in both immutable stores before commit.
CREATE OR REPLACE FUNCTION crm_require_sales_journal()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE
    v_resource_type VARCHAR(20) := CASE WHEN TG_TABLE_NAME = 'crm_lead' THEN 'LEAD' ELSE 'CUSTOMER' END;
    v_resource_id BIGINT := NEW.id;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM crm_ownership_history h
        WHERE h.tenant_id = NEW.tenant_id AND h.resource_type = v_resource_type
          AND h.resource_id = v_resource_id AND h.created_at >= transaction_timestamp()
    ) THEN
        RAISE EXCEPTION 'sales change requires ownership history in the same transaction'
            USING ERRCODE = '23514';
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM crm_audit_log a
        WHERE a.tenant_id = NEW.tenant_id AND a.resource_type = v_resource_type
          AND a.resource_id = v_resource_id AND a.created_at >= transaction_timestamp()
    ) THEN
        RAISE EXCEPTION 'sales change requires audit log in the same transaction'
            USING ERRCODE = '23514';
    END IF;
    RETURN NULL;
END $$;

CREATE CONSTRAINT TRIGGER trg_crm_lead_journal_insert
AFTER INSERT ON crm_lead DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION crm_require_sales_journal();
CREATE CONSTRAINT TRIGGER trg_crm_lead_journal_change
AFTER UPDATE OF status, ownership_type, owner_user_id, public_pool_id ON crm_lead
DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION crm_require_sales_journal();
CREATE CONSTRAINT TRIGGER trg_crm_customer_journal_insert
AFTER INSERT ON crm_customer DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION crm_require_sales_journal();
CREATE CONSTRAINT TRIGGER trg_crm_customer_journal_change
AFTER UPDATE OF status, ownership_type, owner_user_id, public_pool_id, merged_into_customer_id ON crm_customer
DEFERRABLE INITIALLY DEFERRED FOR EACH ROW EXECUTE FUNCTION crm_require_sales_journal();
