-- A quote version is an immutable commercial offer and can back only one active contract.
CREATE UNIQUE INDEX uk_crm_contract_quote_version_active
    ON crm_contract(tenant_id, quote_id, quote_version_id)
    WHERE deleted_at IS NULL;
