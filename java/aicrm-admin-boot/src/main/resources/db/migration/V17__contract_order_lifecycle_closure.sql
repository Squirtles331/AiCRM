-- Complete the CRM-owned contract and sales-order lifecycle without changing V14.

-- ERP callbacks are outside the CRM runtime boundary. Remove historical grants first.
DELETE FROM crm_role_permission
WHERE permission_code = 'integration:erp:callback';

DELETE FROM crm_permission
WHERE code = 'integration:erp:callback';

CREATE OR REPLACE FUNCTION crm_seed_contract_order_permissions(p_tenant_id BIGINT)
RETURNS VOID LANGUAGE sql AS $$
    INSERT INTO crm_permission (tenant_id, code, name, resource_type, action)
    VALUES
        (p_tenant_id, 'contract:create', '创建合同', 'CONTRACT', 'CREATE'),
        (p_tenant_id, 'contract:read:own', '查看本人合同', 'CONTRACT', 'READ_OWN'),
        (p_tenant_id, 'contract:read:any', '查看全部合同', 'CONTRACT', 'READ_ANY'),
        (p_tenant_id, 'contract:write:own', '维护本人合同', 'CONTRACT', 'WRITE_OWN'),
        (p_tenant_id, 'contract:write:any', '维护全部合同', 'CONTRACT', 'WRITE_ANY'),
        (p_tenant_id, 'contract:submit-signature', '提交合同签署', 'CONTRACT', 'SUBMIT_SIGNATURE'),
        (p_tenant_id, 'contract:withdraw-signature', '撤回合同签署', 'CONTRACT', 'WITHDRAW_SIGNATURE'),
        (p_tenant_id, 'contract:sign', '确认合同签署', 'CONTRACT', 'SIGN'),
        (p_tenant_id, 'contract:void', '作废合同', 'CONTRACT', 'VOID'),
        (p_tenant_id, 'contract:change', '发起合同变更', 'CONTRACT', 'CHANGE'),
        (p_tenant_id, 'contract:approve-change', '审批合同变更', 'CONTRACT', 'APPROVE_CHANGE'),
        (p_tenant_id, 'order:create', '创建销售订单', 'ORDER', 'CREATE'),
        (p_tenant_id, 'order:read:own', '查看本人订单', 'ORDER', 'READ_OWN'),
        (p_tenant_id, 'order:read:any', '查看全部订单', 'ORDER', 'READ_ANY'),
        (p_tenant_id, 'order:write:own', '维护本人订单', 'ORDER', 'WRITE_OWN'),
        (p_tenant_id, 'order:write:any', '维护全部订单', 'ORDER', 'WRITE_ANY'),
        (p_tenant_id, 'order:confirm', '确认销售订单', 'ORDER', 'CONFIRM'),
        (p_tenant_id, 'order:cancel', '申请取消销售订单', 'ORDER', 'CANCEL'),
        (p_tenant_id, 'order:approve-cancel', '审批订单取消', 'ORDER', 'APPROVE_CANCEL'),
        (p_tenant_id, 'order:close', '关闭销售订单', 'ORDER', 'CLOSE')
    ON CONFLICT (tenant_id, code) DO NOTHING;
$$;

SELECT crm_seed_contract_order_permissions(id) FROM crm_tenant;

ALTER TABLE crm_sales_order
    ADD COLUMN close_reason VARCHAR(500);

ALTER TABLE crm_sales_order
    ADD CONSTRAINT ck_crm_sales_order_close
    CHECK ((status = 'CLOSED' AND closed_at IS NOT NULL AND close_reason IS NOT NULL)
        OR (status <> 'CLOSED' AND closed_at IS NULL AND close_reason IS NULL));

CREATE UNIQUE INDEX uk_crm_contract_change_open
    ON crm_contract_change(tenant_id, contract_id)
    WHERE status IN ('DRAFT', 'SUBMITTED') AND deleted_at IS NULL;
