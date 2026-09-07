-- Authentication belongs to the new platform model. Existing service accounts remain non-login accounts
-- until an administrator explicitly provisions a BCrypt password hash.
ALTER TABLE crm_user ADD COLUMN password_hash VARCHAR(100);

-- The modular CRM runtime has no compatibility persistence path. These names are intentionally retired.
DROP TABLE IF EXISTS channel_qr_code CASCADE;
DROP TABLE IF EXISTS sys_job_log CASCADE;
DROP TABLE IF EXISTS sys_login_log CASCADE;
DROP TABLE IF EXISTS sys_oper_log CASCADE;
DROP TABLE IF EXISTS sys_file CASCADE;
DROP TABLE IF EXISTS sys_config CASCADE;
DROP TABLE IF EXISTS sys_dict_data CASCADE;
DROP TABLE IF EXISTS sys_dict_type CASCADE;
DROP TABLE IF EXISTS sys_user_role CASCADE;
DROP TABLE IF EXISTS sys_role_menu CASCADE;
DROP TABLE IF EXISTS sys_role CASCADE;
DROP TABLE IF EXISTS sys_menu CASCADE;
DROP TABLE IF EXISTS metric_daily CASCADE;
DROP TABLE IF EXISTS document CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS conversation CASCADE;
DROP TABLE IF EXISTS speech_library CASCADE;
DROP TABLE IF EXISTS competitor_product CASCADE;
DROP TABLE IF EXISTS competitor CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS product_category CASCADE;
DROP TABLE IF EXISTS follow_up CASCADE;
DROP TABLE IF EXISTS lead_assign_rule CASCADE;
DROP TABLE IF EXISTS customer_tag_rule CASCADE;
DROP TABLE IF EXISTS customer_tag_rel CASCADE;
DROP TABLE IF EXISTS customer_tag CASCADE;
DROP TABLE IF EXISTS identity_mapping CASCADE;
DROP TABLE IF EXISTS identity CASCADE;
DROP TABLE IF EXISTS contact CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS lead CASCADE;
DROP TABLE IF EXISTS channel_event CASCADE;
DROP TABLE IF EXISTS channel_account CASCADE;
DROP TABLE IF EXISTS channel CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS department CASCADE;
DROP TABLE IF EXISTS tenant CASCADE;
DROP TABLE IF EXISTS sys_plan CASCADE;
