-- Stage 5E: CRM-owned competitor intelligence registry; external market data stays outside CRM.
CREATE TABLE crm_competitor (
 id BIGINT PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id), name VARCHAR(200) NOT NULL,
 positioning VARCHAR(1000) NULL, strengths VARCHAR(2000) NULL, weaknesses VARCHAR(2000) NULL,
 status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE', owner_user_id BIGINT NOT NULL, version BIGINT NOT NULL DEFAULT 0,
 created_by BIGINT NOT NULL, updated_by BIGINT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, deleted_by BIGINT,
 CONSTRAINT uk_crm_competitor_tenant_id UNIQUE (tenant_id,id), CONSTRAINT ck_crm_competitor_status CHECK(status IN ('ACTIVE','ARCHIVED')),
 CONSTRAINT fk_crm_competitor_owner FOREIGN KEY(tenant_id,owner_user_id) REFERENCES crm_user(tenant_id,id)
);
CREATE UNIQUE INDEX uk_crm_competitor_name_active ON crm_competitor(tenant_id,lower(name)) WHERE deleted_at IS NULL AND status='ACTIVE';
CREATE INDEX idx_crm_competitor_status ON crm_competitor(tenant_id,status,updated_at DESC) WHERE deleted_at IS NULL;
INSERT INTO crm_permission (tenant_id,code,name,resource_type,action) SELECT t.id,p.code,p.name,'COMPETITOR',p.action FROM crm_tenant t CROSS JOIN (VALUES ('competitor:read','查看竞争信息','READ'),('competitor:manage','维护竞争信息','MANAGE'))p(code,name,action) ON CONFLICT DO NOTHING;
CREATE OR REPLACE FUNCTION crm_seed_competitor_permissions(p_tenant_id BIGINT) RETURNS VOID LANGUAGE sql AS $$ INSERT INTO crm_permission(tenant_id,code,name,resource_type,action) VALUES(p_tenant_id,'competitor:read','查看竞争信息','COMPETITOR','READ'),(p_tenant_id,'competitor:manage','维护竞争信息','COMPETITOR','MANAGE') ON CONFLICT DO NOTHING; $$;
CREATE OR REPLACE FUNCTION crm_seed_competitor_permissions_after_tenant_insert() RETURNS TRIGGER LANGUAGE plpgsql AS $$ BEGIN PERFORM crm_seed_competitor_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_competitor_permissions AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_competitor_permissions_after_tenant_insert();
