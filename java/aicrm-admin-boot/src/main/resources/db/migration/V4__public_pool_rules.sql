ALTER TABLE crm_public_pool
    ADD COLUMN auto_recycle_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN recycle_after_days INT,
    ADD COLUMN claim_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN assign_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN release_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN rule_version INT NOT NULL DEFAULT 1,
    ADD COLUMN effective_from TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN created_by BIGINT,
    ADD COLUMN updated_by BIGINT;

UPDATE crm_public_pool
SET claim_enabled = FALSE, assign_enabled = FALSE, release_enabled = FALSE
WHERE status = 0;

ALTER TABLE crm_public_pool
    ADD CONSTRAINT ck_crm_public_pool_status CHECK (status IN (0, 1)),
    ADD CONSTRAINT ck_crm_public_pool_recycle_days CHECK (
        (auto_recycle_enabled = FALSE AND recycle_after_days IS NULL)
        OR (auto_recycle_enabled = TRUE AND recycle_after_days > 0)
    ),
    ADD CONSTRAINT ck_crm_public_pool_rule_version CHECK (rule_version > 0),
    ADD CONSTRAINT ck_crm_public_pool_disabled_actions CHECK (
        status = 1 OR (claim_enabled = FALSE AND assign_enabled = FALSE AND release_enabled = FALSE)
    );

DROP INDEX uk_crm_pool_active;
CREATE UNIQUE INDEX uk_crm_pool_code_active
    ON crm_public_pool(tenant_id, resource_type, code)
    WHERE deleted_at IS NULL AND status = 1;
CREATE UNIQUE INDEX uk_crm_pool_rule_version
    ON crm_public_pool(tenant_id, resource_type, code, rule_version);
CREATE INDEX idx_crm_pool_effective
    ON crm_public_pool(tenant_id, resource_type, status, effective_from DESC)
    WHERE deleted_at IS NULL;

-- Rule changes create a new row/version. Existing versions may only be disabled or soft-deleted.
CREATE OR REPLACE FUNCTION crm_guard_public_pool_rule_version()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF OLD.status = 1 AND NEW.status = 0
       AND NEW.claim_enabled = FALSE AND NEW.assign_enabled = FALSE AND NEW.release_enabled = FALSE
       AND ROW(NEW.resource_type, NEW.code, NEW.auto_recycle_enabled, NEW.recycle_after_days,
               NEW.rule_version, NEW.effective_from)
           IS NOT DISTINCT FROM
           ROW(OLD.resource_type, OLD.code, OLD.auto_recycle_enabled, OLD.recycle_after_days,
               OLD.rule_version, OLD.effective_from) THEN
        RETURN NEW;
    END IF;
    IF ROW(NEW.resource_type, NEW.code, NEW.auto_recycle_enabled, NEW.recycle_after_days,
           NEW.claim_enabled, NEW.assign_enabled, NEW.release_enabled, NEW.rule_version,
           NEW.effective_from)
       IS DISTINCT FROM
       ROW(OLD.resource_type, OLD.code, OLD.auto_recycle_enabled, OLD.recycle_after_days,
           OLD.claim_enabled, OLD.assign_enabled, OLD.release_enabled, OLD.rule_version,
           OLD.effective_from) THEN
        RAISE EXCEPTION 'public pool rules are immutable; insert a new rule_version'
            USING ERRCODE = '23514';
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_crm_public_pool_rule_immutable
BEFORE UPDATE ON crm_public_pool
FOR EACH ROW EXECUTE FUNCTION crm_guard_public_pool_rule_version();
