-- Stage 5B: CRM-owned sales conversation summaries and curated message records.
CREATE TABLE crm_sales_conversation (
    id BIGINT PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id), customer_id BIGINT NOT NULL, contact_id BIGINT,
    subject VARCHAR(200) NOT NULL, channel VARCHAR(32) NOT NULL, status VARCHAR(16) NOT NULL DEFAULT 'OPEN', summary VARCHAR(2000),
    owner_user_id BIGINT NOT NULL, closed_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL, updated_by BIGINT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, deleted_by BIGINT,
    CONSTRAINT uk_crm_sales_conversation_tenant_id UNIQUE (tenant_id,id),
    CONSTRAINT ck_crm_sales_conversation_status CHECK (status IN ('OPEN','CLOSED')),
    CONSTRAINT ck_crm_sales_conversation_closed CHECK ((status='CLOSED' AND closed_at IS NOT NULL) OR (status='OPEN' AND closed_at IS NULL)),
    CONSTRAINT fk_crm_sales_conversation_customer FOREIGN KEY (tenant_id,customer_id) REFERENCES crm_customer(tenant_id,id),
    CONSTRAINT fk_crm_sales_conversation_contact FOREIGN KEY (tenant_id,contact_id) REFERENCES crm_contact(tenant_id,id),
    CONSTRAINT fk_crm_sales_conversation_owner FOREIGN KEY (tenant_id,owner_user_id) REFERENCES crm_user(tenant_id,id)
);
CREATE INDEX idx_crm_sales_conversation_customer ON crm_sales_conversation(tenant_id,customer_id,updated_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE crm_sales_conversation_entry (
    id BIGINT PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES crm_tenant(id), conversation_id BIGINT NOT NULL,
    direction VARCHAR(16) NOT NULL, content VARCHAR(4000) NOT NULL, occurred_at TIMESTAMPTZ NOT NULL, created_by BIGINT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_crm_sales_conversation_entry_tenant_id UNIQUE (tenant_id,id),
    CONSTRAINT ck_crm_sales_conversation_entry_direction CHECK (direction IN ('INBOUND','OUTBOUND','NOTE')),
    CONSTRAINT fk_crm_sales_conversation_entry_conversation FOREIGN KEY (tenant_id,conversation_id) REFERENCES crm_sales_conversation(tenant_id,id),
    CONSTRAINT fk_crm_sales_conversation_entry_creator FOREIGN KEY (tenant_id,created_by) REFERENCES crm_user(tenant_id,id)
);
CREATE INDEX idx_crm_sales_conversation_entry_time ON crm_sales_conversation_entry(tenant_id,conversation_id,occurred_at,id);
CREATE TRIGGER trg_crm_sales_conversation_entry_append_only BEFORE UPDATE OR DELETE ON crm_sales_conversation_entry FOR EACH ROW EXECUTE FUNCTION crm_reject_immutable_change();

INSERT INTO crm_permission (tenant_id,code,name,resource_type,action)
SELECT t.id,p.code,p.name,'SALES_CONVERSATION',p.action FROM crm_tenant t CROSS JOIN (VALUES
 ('conversation:read:any','查看全部销售会话','READ_ANY'),('conversation:write:any','维护全部销售会话','WRITE_ANY')
) p(code,name,action) ON CONFLICT (tenant_id,code) DO NOTHING;
CREATE OR REPLACE FUNCTION crm_seed_sales_conversation_permissions(p_tenant_id BIGINT) RETURNS VOID LANGUAGE sql AS $$
 INSERT INTO crm_permission (tenant_id,code,name,resource_type,action) VALUES
 (p_tenant_id,'conversation:read:any','查看全部销售会话','SALES_CONVERSATION','READ_ANY'),(p_tenant_id,'conversation:write:any','维护全部销售会话','SALES_CONVERSATION','WRITE_ANY') ON CONFLICT (tenant_id,code) DO NOTHING;
$$;
CREATE OR REPLACE FUNCTION crm_seed_sales_conversation_permissions_after_tenant_insert() RETURNS TRIGGER LANGUAGE plpgsql AS $$ BEGIN PERFORM crm_seed_sales_conversation_permissions(NEW.id); RETURN NEW; END $$;
CREATE TRIGGER trg_crm_tenant_seed_sales_conversation_permissions AFTER INSERT ON crm_tenant FOR EACH ROW EXECUTE FUNCTION crm_seed_sales_conversation_permissions_after_tenant_insert();
