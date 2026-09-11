-- Stage 5D: CRM-owned sales playbooks; no dialer, generative AI, or external messaging integration.
CREATE TABLE crm_sales_playbook (
 id BIGINT PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id), title VARCHAR(200) NOT NULL,
 sales_stage VARCHAR(64) NOT NULL, scenario VARCHAR(200) NOT NULL, content TEXT NOT NULL,
 status VARCHAR(16) NOT NULL DEFAULT 'DRAFT', owner_user_id BIGINT NOT NULL, published_at TIMESTAMPTZ, archived_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0,
 created_by BIGINT NOT NULL, updated_by BIGINT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, deleted_by BIGINT,
 CONSTRAINT uk_crm_sales_playbook_tenant_id UNIQUE (tenant_id,id), CONSTRAINT ck_crm_sales_playbook_status CHECK(status IN ('DRAFT','PUBLISHED','ARCHIVED')),
 CONSTRAINT ck_crm_sales_playbook_times CHECK ((status='DRAFT' AND published_at IS NULL AND archived_at IS NULL) OR (status='PUBLISHED' AND published_at IS NOT NULL AND archived_at IS NULL) OR (status='ARCHIVED' AND published_at IS NOT NULL AND archived_at IS NOT NULL)),
 CONSTRAINT fk_crm_sales_playbook_owner FOREIGN KEY(tenant_id,owner_user_id) REFERENCES crm_user(tenant_id,id)
);
CREATE INDEX idx_crm_sales_playbook_stage ON crm_sales_playbook(tenant_id,status,sales_stage,updated_at DESC) WHERE deleted_at IS NULL;
INSERT INTO crm_permission (tenant_id,code,name,resource_type,action) SELECT t.id,p.code,p.name,'SALES_PLAYBOOK',p.action FROM crm_tenant t CROSS JOIN (VALUES ('playbook:read','查看销售话术','READ'),('playbook:manage','维护销售话术','MANAGE'))p(code,name,action) ON CONFLICT DO NOTHING;
CREATE OR REPLACE FUNCTION crm_seed_sales_playbook_permissions(p_tenant_id BIGINT) RETURNS VOID LANGUAGE sql AS $$ INSERT INTO crm_permission(tenant_id,code,name,resource_type,action) VALUES(p_tenant_id,'playbook:read','查看销售话术','SALES_PLAYBOOK','READ'),(p_tenant_id,'playbook:manage','维护销售话术','SALES_PLAYBOOK','MANAGE') ON CONFLICT DO NOTHING; $$;
CREATE OR REPLACE FUNCTION crm_seed_sales_playbook_permissions_after_tenant_insert() RETURNS TRIGGER LANGUAGE plpgsql AS $$ BEGIN PERFORM crm_seed_sales_playbook_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_sales_playbook_permissions AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_sales_playbook_permissions_after_tenant_insert();
