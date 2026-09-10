-- The first order slice creates one full sales order for one signed contract.
CREATE UNIQUE INDEX uk_crm_sales_order_contract_active
    ON crm_sales_order(tenant_id, contract_id)
    WHERE deleted_at IS NULL;
