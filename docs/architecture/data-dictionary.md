# CRM 数据字典（阶段 5D）

版本：`M5D-DB-1.0`；状态：产品目录、商机管道、报价审批、合同与销售订单闭环、CRM 工作台、目标、报表、规则驱动评分、组织/营销连接器、获客渠道归因、销售会话、销售资料库及销售话术已实现、待发布评审；数据库：PostgreSQL 16+。本文件与 Flyway `V1` 至 `V25` 共同构成字段冻结基线；SQL 为物理事实源，文档不得单独变更。

## 1. 类型与通用字段组

- 主键和引用均为 `BIGINT`，由应用 Snowflake 生成；JSON API 始终输出字符串。
- 时间均为 UTC `TIMESTAMPTZ`；金额统一 `NUMERIC(18,2)`；扩展和快照为 `JSONB`。
- `AUDIT_MUTABLE` = `created_by BIGINT NULL`、`updated_by BIGINT NULL`、`created_at TIMESTAMPTZ NOT NULL DEFAULT now()`、`updated_at TIMESTAMPTZ NOT NULL DEFAULT now()`、`deleted_at TIMESTAMPTZ NULL`、`deleted_by BIGINT NULL`。
- `tenant_id` 在所有租户业务表中 `NOT NULL`；`version BIGINT NOT NULL DEFAULT 0` 用于可变销售聚合的乐观锁。
- `NULL` 表示未知或不适用，不使用空字符串代替；业务编号和编码去除首尾空白后写入。

## 2. 平台与权限

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_tenant` | `id BIGINT N`；`name VARCHAR(200) N`；`status SMALLINT N DEFAULT 1`；`AUDIT_MUTABLE` | PK `id`；`status IN (0,1)` |
| `crm_department` | `id BIGINT N`；`tenant_id BIGINT N`；`parent_id BIGINT NULL`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`path VARCHAR(1000) N`；`status SMALLINT N DEFAULT 1`；`AUDIT_MUTABLE` | PK；活跃 `(tenant_id,code)` 唯一；父部门复合 FK 保证同租户；`(tenant_id,parent_id)` 索引 |
| `crm_user` | `id BIGINT N`；`tenant_id BIGINT N`；`department_id BIGINT N`；`username VARCHAR(100) N`；`password_hash VARCHAR(100) NULL`；`name VARCHAR(100) N`；`mobile VARCHAR(32) NULL`；`email VARCHAR(200) NULL`；`status SMALLINT N DEFAULT 1`；`AUDIT_MUTABLE` | PK；部门复合 FK；活跃用户名/手机号/邮箱租户内唯一；`status IN (0,1)`；部门索引。`password_hash` 仅接受 BCrypt，系统任务用户保持 NULL 且不可登录 |
| `crm_role` | `id BIGINT N`；`tenant_id BIGINT N`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`status SMALLINT N DEFAULT 1`；`data_scope VARCHAR(32) N DEFAULT 'SELF'`；`AUDIT_MUTABLE` | PK；活跃 `(tenant_id,code)` 唯一；`data_scope` 为 `SELF/DEPARTMENT/DEPARTMENT_AND_SUB/ALL` |
| `crm_permission` | `tenant_id BIGINT N`；`code VARCHAR(100) N`；`name VARCHAR(200) N`；`resource_type VARCHAR(64) N`；`action VARCHAR(64) N`；`status SMALLINT N DEFAULT 1`；`description VARCHAR(500) NULL`；`AUDIT_MUTABLE` | PK `(tenant_id,code)`；权限随新租户自动初始化 |
| `crm_user_role` | `tenant_id BIGINT N`；`user_id BIGINT N`；`role_id BIGINT N`；`AUDIT_MUTABLE` | PK `(tenant_id,user_id,role_id)`；用户和角色复合 FK 保证同租户 |
| `crm_role_permission` | `tenant_id BIGINT N`；`role_id BIGINT N`；`permission_code VARCHAR(100) N`；`AUDIT_MUTABLE` | PK `(tenant_id,role_id,permission_code)`；角色和权限复合 FK |
| `crm_role_field_permission` | `tenant_id BIGINT N`；`role_id BIGINT N`；`resource_type VARCHAR(64) N`；`field_name VARCHAR(100) N`；`can_view/edit/export BOOLEAN N DEFAULT false`；`AUDIT_MUTABLE` | 组合 PK；编辑或导出为真时查看必须为真；角色复合 FK |

首期预置高风险权限包括 `lead/customer:assign`、`:transfer`、`:batch`、`:export`、`:handover`、`lead:convert`、`customer:merge`、`approval:manage`、`audit:read` 和 `outbox:retry`。权限码不可复用为不同语义。

## 3. 公海与销售主数据

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_public_pool` | `id BIGINT N`；`tenant_id BIGINT N`；`resource_type VARCHAR(20) N`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`status SMALLINT N DEFAULT 1`；`auto_recycle_enabled BOOLEAN N DEFAULT false`；`recycle_after_days INT NULL`；`claim_enabled/assign_enabled/release_enabled BOOLEAN N DEFAULT true`；`rule_version INT N DEFAULT 1`；`effective_from TIMESTAMPTZ N DEFAULT now()`；`AUDIT_MUTABLE` | `resource_type IN (LEAD,CUSTOMER)`；启用记录 `(tenant_id,resource_type,code)` 唯一；版本唯一；每租户、每资源类型最多一个启用的自动回收目标池；停用时三个手工动作均关闭；规则字段不可原地修改，只能停用旧版并新增高版本 |
| `crm_lead` | `id BIGINT N`；`tenant_id BIGINT N`；`lead_no VARCHAR(32) N`；`name VARCHAR(100) NULL`；`mobile VARCHAR(32) NULL`；`email VARCHAR(200) NULL`；`company_name VARCHAR(200) NULL`；`source_type VARCHAR(64) N`；`source_ref VARCHAR(200) NULL`；`intent VARCHAR(64) NULL`；`acquisition_channel_id BIGINT NULL`；`acquisition_channel_code VARCHAR(64) NULL`；`status VARCHAR(32) N DEFAULT 'NEW'`；`invalid_reason VARCHAR(500) NULL`；`customer_id BIGINT NULL`；`ownership_type VARCHAR(16) N`；`owner_user_id/owner_dept_id/public_pool_id BIGINT NULL`；`pool_entered_at/last_follow_up_at/next_follow_up_at TIMESTAMPTZ NULL`；`version BIGINT N DEFAULT 0`；`extension JSONB N DEFAULT '{}'`；`AUDIT_MUTABLE` | 活跃 `(tenant_id,lead_no)` 唯一；渠道 ID/编码须同时为空或非空，复合 FK 限制同租户渠道；状态和终态数据检查；公私海互斥；租户+归属+负责人/部门/公海+跟进时间组合索引 |
| `crm_acquisition_channel` | `id/tenant_id BIGINT N`；`code VARCHAR(64) N`；`name VARCHAR(200) N`；`source_type VARCHAR(64) N`；`status VARCHAR(16) N`；`version`、`AUDIT_MUTABLE` | 租户内渠道编码唯一；状态仅为 `DRAFT/ACTIVE/DISABLED`；线索通过 `(tenant_id, acquisition_channel_id)` 复合 FK 引用，历史线索另存编码快照。 |
| `crm_customer` | `id BIGINT N`；`tenant_id BIGINT N`；`customer_no VARCHAR(32) N`；`name VARCHAR(200) N`；`industry/region VARCHAR(100) NULL`；`status VARCHAR(32) N DEFAULT 'ACTIVE'`；归属和时间字段同线索；`version BIGINT N DEFAULT 0`；`extension JSONB N DEFAULT '{}'`；`merged_into_customer_id BIGINT NULL`；`merged_at TIMESTAMPTZ NULL`；`AUDIT_MUTABLE` | 活跃编号和 `lower(name)` 租户内唯一，即首期不允许同名活跃客户；公私海互斥；合并源必须同时设置目标、合并时间和软删除；目标必须同租户且有效 |
| `crm_contact` | `id BIGINT N`；`tenant_id BIGINT N`；`customer_id BIGINT N`；`source_customer_id BIGINT NULL`；`name VARCHAR(100) N`；`mobile VARCHAR(32) NULL`；`email VARCHAR(200) NULL`；`department/title VARCHAR(100) NULL`；`is_decision_maker BOOLEAN N DEFAULT false`；`extension JSONB N DEFAULT '{}'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 客户和原客户复合 FK；活跃客户联系人索引。`source_customer_id` 保留合并来源 |
| `crm_follow_up` | `id BIGINT N`；`tenant_id BIGINT N`；`lead_id/customer_id BIGINT NULL`；`actor_user_id BIGINT N`；`channel VARCHAR(32) N`；`content TEXT N`；`next_follow_up_at TIMESTAMPTZ NULL`；`extension JSONB N DEFAULT '{}'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 线索/客户二选一；目标和操作者复合 FK；分别按目标、创建时间倒序索引 |

归属约束的最终形态：私海必须同时有启用负责人和负责人部门，不得有公海或入池时间；公海必须有类型匹配的公海和入池时间，不得有负责人或部门。负责人部门由数据库按用户当前部门写入，作为操作时组织快照。

## 4. 交接、审计与可靠性事实

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_ownership_history` | `id BIGINT N`；`tenant_id BIGINT N`；`resource_type VARCHAR(20) N`；`resource_id BIGINT N`；`action VARCHAR(32) N`；`from_owner_user_id/to_owner_user_id/from_pool_id/to_pool_id BIGINT NULL`；`operation_id VARCHAR(64) NULL`；`source VARCHAR(32) N DEFAULT 'API'`；`reason VARCHAR(500) NULL`；`before_snapshot/after_snapshot JSONB N DEFAULT '{}'`；`batch_no/trace_id VARCHAR(64) NULL`；`operator_user_id BIGINT N`；`created_at TIMESTAMPTZ N DEFAULT now()` | 动作为 `CREATE/CLAIM/RELEASE/ASSIGN/TRANSFER/RECYCLE/CONVERT/MERGE/HANDOVER/INVALIDATE/STATUS_CHANGE`；只允许 INSERT；资源、操作和批次索引 |
| `crm_resource_handover` | `id BIGINT N`；`tenant_id BIGINT N`；`from_user_id/to_user_id BIGINT N`；`resource_type VARCHAR(20) N`；`resource_id BIGINT N`；`status VARCHAR(20) N DEFAULT 'COMPLETED'`；`operation_id/source/reason/before_snapshot/after_snapshot/batch_no/trace_id`；`created_by/updated_by BIGINT NULL`；`created_at/updated_at TIMESTAMPTZ N DEFAULT now()`；`completed_at/deleted_at TIMESTAMPTZ NULL`；`deleted_by BIGINT NULL` | 交接双方同租户且不同；状态 `PENDING/PROCESSING/COMPLETED/FAILED`；批次和原负责人索引 |
| `crm_idempotency_record` | PK 字段 `tenant_id BIGINT N`、`operation VARCHAR(100) N`、`idempotency_key VARCHAR(128) N`；`status VARCHAR(20) N`；`request_hash VARCHAR(64) NULL`；`response_body JSONB NULL`；`http_status INT NULL`；`error_code VARCHAR(100) NULL`；`error_message VARCHAR(1000) NULL`；`attempt_count INT N DEFAULT 1`；`created_at/updated_at TIMESTAMPTZ N DEFAULT now()`；`completed_at/failed_at/expires_at TIMESTAMPTZ NULL`；`created_by/updated_by/deleted_by BIGINT NULL`；`deleted_at TIMESTAMPTZ NULL` | 状态 `PROCESSING/COMPLETED/FAILED` 与完成/失败时间一致；作用域组合 PK；过期索引 |
| `crm_audit_log` | `id/tenant_id BIGINT N`；`actor_user_id BIGINT NULL`；`action VARCHAR(100) N`；`resource_type VARCHAR(64) N`；`resource_id BIGINT NULL`；`before_data/after_data JSONB NULL`；`trace_id/operation_id/batch_no VARCHAR(64) NULL`；`source VARCHAR(32) N DEFAULT 'API'`；`client_ip INET NULL`；`user_agent VARCHAR(500) NULL`；`result VARCHAR(16) N DEFAULT 'SUCCESS'`；`error_code VARCHAR(100) NULL`；`AUDIT_MUTABLE` | 只允许 INSERT，故更新/删除字段恒为空；资源、操作和 trace 索引。`before_data/after_data` 的业务语义即前后快照 |
| `crm_outbox_event` | `id/tenant_id/aggregate_id BIGINT N`；`aggregate_type VARCHAR(64) N`；`event_type VARCHAR(100) N`；`event_version INT N DEFAULT 1`；`operation_id/trace_id VARCHAR(64) NULL`；`payload JSONB N`；`status VARCHAR(16) N DEFAULT 'PENDING'`；`occurred_at/available_at/created_at/updated_at TIMESTAMPTZ N`；`published_at/next_retry_at/locked_at/dead_lettered_at/deleted_at TIMESTAMPTZ NULL`；`locked_by VARCHAR(100) NULL`；`retry_count INT N DEFAULT 0`；`last_error VARCHAR(1000) NULL`；`created_by/updated_by/deleted_by BIGINT NULL` | 状态 `PENDING/PUBLISHING/PUBLISHED/FAILED/DEAD`；发布态必须有 `published_at`；操作事件部分唯一；待发送和对账索引 |
| `crm_inbox_record` | `tenant_id BIGINT N`；`consumer VARCHAR(100) N`；`message_id VARCHAR(128) N`；`business_key VARCHAR(200) NULL`；`status VARCHAR(16) N DEFAULT 'PROCESSING'`；`payload_hash VARCHAR(64) NULL`；`attempt_count INT N DEFAULT 1`；`first_received_at/updated_at TIMESTAMPTZ N DEFAULT now()`；`completed_at/failed_at/deleted_at TIMESTAMPTZ NULL`；`last_error VARCHAR(1000) NULL`；`created_by/updated_by/deleted_by BIGINT NULL` | PK `(tenant_id,consumer,message_id)`；业务键部分唯一；状态 `PROCESSING/COMPLETED/FAILED` |

销售聚合的创建、状态或归属变化在提交前必须同时存在 `crm_ownership_history` 和 `crm_audit_log`；业务应用还必须在同一事务写对应 Outbox。数据库以延迟约束触发器强制前两项，事务测试强制三项完整。

每个租户自动拥有用户名为 `__system__` 的启用系统任务用户。它只作为定时回收等非人工操作的审计操作者，不参与普通登录、数据授权或业务负责人分配。

## 5. 产品与价格目录

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_product_category` | `id/tenant_id BIGINT N`；`parent_id BIGINT NULL`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`status VARCHAR(16) N DEFAULT 'ACTIVE'`；`sort_order INT N DEFAULT 0`；`extension JSONB N DEFAULT '{}'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 活跃 `(tenant_id,code)` 唯一；状态为 `ACTIVE/DISABLED`；父分类复合 FK 保证同租户；父级索引 |
| `crm_product` | `id/tenant_id BIGINT N`；`category_id BIGINT NULL`；`product_no VARCHAR(32) N`；`sku VARCHAR(64) N`；`name VARCHAR(200) N`；`specification VARCHAR(500) NULL`；`unit VARCHAR(32) N`；`status VARCHAR(16) N DEFAULT 'ACTIVE'`；`sale_enabled BOOLEAN N DEFAULT true`；`extension JSONB N DEFAULT '{}'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 活跃 `(tenant_id,product_no)`、`(tenant_id,sku)` 唯一；分类复合 FK；分类索引；状态为 `ACTIVE/DISABLED` |
| `crm_price_list` | `id/tenant_id BIGINT N`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`currency CHAR(3) N`；`status VARCHAR(16) N DEFAULT 'DRAFT'`；`effective_from/effective_to TIMESTAMPTZ NULL`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 活跃编码租户内唯一；状态为 `DRAFT/ACTIVE/EXPIRED/DISABLED`；币种三位大写；结束时间晚于开始时间；生效区间索引 |
| `crm_price_item` | `id/tenant_id BIGINT N`；`price_list_id/product_id BIGINT N`；`list_price NUMERIC(18,2) N`；`minimum_price NUMERIC(18,2) NULL`；`tax_rate NUMERIC(5,4) N DEFAULT 0`；`status VARCHAR(16) N DEFAULT 'ACTIVE'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 价目表、产品复合 FK；活跃 `(tenant_id,price_list_id,product_id)` 唯一；最低价不得大于目录价；税率 0 到 1；产品索引 |

目录权限为 `catalog:read`、`catalog:write`、`catalog:publish`。产品目录写操作与审计、Outbox 同事务保存；发布后价目表不允许继续新增价格项。

## 6. 商机管道

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_opportunity` | `id/tenant_id BIGINT N`；`opportunity_no VARCHAR(32) N`；`name VARCHAR(200) N`；`customer_id BIGINT N`；`contact_id/source_lead_id BIGINT NULL`；`stage VARCHAR(32) N DEFAULT 'DISCOVERY'`；`status VARCHAR(16) N DEFAULT 'OPEN'`；`expected_amount NUMERIC(18,2) N DEFAULT 0`；`currency CHAR(3) N`；`probability SMALLINT N DEFAULT 0`；`expected_close_date DATE NULL`；`owner_user_id/owner_dept_id BIGINT N`；`lost_reason VARCHAR(500) NULL`；`lost_at/won_at TIMESTAMPTZ NULL`；`extension JSONB N DEFAULT '{}'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 活跃 `(tenant_id,opportunity_no)` 唯一；客户、联系人、来源线索、负责人和部门均为同租户复合 FK；联系人必须属于客户，客户必须有效且未合并，负责人必须启用。阶段为 `DISCOVERY/QUALIFICATION/SOLUTION/QUOTATION/NEGOTIATION/CLOSED_WON/CLOSED_LOST`；状态为 `OPEN/WON/LOST`。终态形状、金额、概率和币种均由 CHECK 约束验证；负责人管道和客户管道索引支持工作台和列表。 |
| `crm_opportunity_stage_history` | `id/tenant_id/opportunity_id BIGINT N`；`action VARCHAR(32) N`；`from_stage/from_status/from_probability NULL`；`to_stage/to_status/to_probability N`；`reason VARCHAR(500) NULL`；`operator_user_id BIGINT N`；`created_at TIMESTAMPTZ N DEFAULT now()` | 动作为 `CREATE/STAGE_CHANGED/WON/LOST/RESTARTED`；商机与操作者使用同租户复合 FK；按 `(tenant_id,opportunity_id,created_at,id)` 查询；仅允许新增。 |

商机创建和每次阶段、状态或概率变化必须在同一事务写阶段历史、`crm_audit_log` 与 `crm_outbox_event`。数据库用延迟约束触发器拦截缺少历史或审计记录的写入；Outbox 由应用服务同事务写入。商机不写入 `crm_ownership_history`，归属历史仅记录私海/公海资源。

## 7. 报价

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_quote` | `id/tenant_id BIGINT N`；`quote_no VARCHAR(32) N`；`opportunity_id/customer_id/price_list_id BIGINT N`；`currency CHAR(3) N`；`status VARCHAR(16) N DEFAULT 'DRAFT'`；`current_version_no INTEGER N DEFAULT 1`；`valid_until DATE NULL`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 活跃 `(tenant_id,quote_no)` 唯一；商机和客户为同租户复合 FK；状态为 `DRAFT/SUBMITTED/APPROVED/REJECTED/EXPIRED/CANCELLED`；有效版本号至少为 1；按商机、状态和更新时间查询。 |
| `crm_quote_version` | `id/tenant_id/quote_id BIGINT N`；`version_no INTEGER N`；`status VARCHAR(16) N DEFAULT 'DRAFT'`；`subtotal/discount_amount/tax_amount/total_amount NUMERIC(18,2) N DEFAULT 0`；`discount_rate NUMERIC(5,4) N DEFAULT 0`；`rejection_reason VARCHAR(500) NULL`；`submitted_at/approved_at/rejected_at/expired_at TIMESTAMPTZ NULL`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 每报价版本号唯一；同租户报价根复合 FK；金额非负、折扣率 0 到 1；拒绝、过期和批准版本不可修改或删除；按版本号倒序查询。 |
| `crm_quote_line` | `id/tenant_id/quote_version_id BIGINT N`；`line_no INTEGER N`；`product_id/price_item_id BIGINT N`；`product_no_snapshot/sku_snapshot VARCHAR`；`product_name_snapshot VARCHAR(200) N`；`unit VARCHAR(32) N`；`quantity NUMERIC(18,4) N`；`list_price/minimum_price/unit_price/line_amount NUMERIC(18,2)`；`discount_rate/tax_rate NUMERIC(5,4)`；`AUDIT_MUTABLE` | 每版本行号唯一；同租户版本复合 FK；数量正数、金额非负、成交价不低于最低价、折扣和税率 0 到 1；行永久不可更新和删除。 |

报价创建只允许引用进行中或赢单商机与生效价目表，并将产品与价格复制到版本行。报价根 `version` 和当前报价版本 `version` 是独立乐观锁；提交和过期命令必须同时提供 `rootVersion` 和 `version`，分别校验两个聚合记录。提交创建审批实例；审批结论同步报价，不存在绕过审批的报价直接驳回命令。当前报价被拒绝后，可使用根版本创建 `current_version_no + 1` 的草稿重报版本，原版本与行保持不可变。Outbox 断言和查询必须使用 `tenant_id + aggregate_type + aggregate_id` 精确定位，不以租户内事件总数推断状态。

## 8. 合同与销售订单

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_contract` | `id/tenant_id BIGINT N`；`contract_no VARCHAR(32) N`；`name VARCHAR(200) N`；`quote_id/quote_version_id/customer_id BIGINT N`；`currency CHAR(3) N`；`subtotal/discount_amount/tax_amount/total_amount NUMERIC(18,2) N DEFAULT 0`；`status VARCHAR(24) N DEFAULT 'DRAFT'`；`effective_from/effective_to DATE NULL`；`submitted_at/signed_at/voided_at TIMESTAMPTZ NULL`；`void_reason VARCHAR(500) NULL`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 活跃合同号租户内唯一；活跃 `(tenant_id,quote_id,quote_version_id)` 唯一；状态为 `DRAFT/PENDING_SIGNATURE/SIGNED/VOIDED`；金额非负、币种三位大写、截止日不得早于生效日；作废状态必须同时有时间与原因。报价、报价版本、客户只存 ID，由应用服务校验。 |
| `crm_contract_line` | `id/tenant_id/contract_id BIGINT N`；`line_no INTEGER N`；`quote_line_id/product_id BIGINT N`；产品编号、SKU、名称、单位快照；`quantity NUMERIC(18,4) N`；`list_price/unit_price/line_amount NUMERIC(18,2) N`；`discount_rate/tax_rate NUMERIC(5,4) N`；`AUDIT_MUTABLE` | 同合同活跃行号唯一；同领域合同复合 FK；数量正数，金额非负，折扣和税率为 0 到 1。合同明细只在创建时由获批报价版本复制。 |
| `crm_contract_change` | `id/tenant_id/contract_id BIGINT N`；`change_no VARCHAR(32) N`；`status VARCHAR(16) N DEFAULT 'DRAFT'`；`reason VARCHAR(500) N`；`before_snapshot/after_snapshot JSONB N`；审批时间、拒绝原因、`version`、`AUDIT_MUTABLE` | 活跃变更号租户内唯一；每合同最多一个草稿或已提交申请；同领域合同复合 FK；状态 `DRAFT/SUBMITTED/APPROVED/REJECTED/CANCELLED`。快照只允许表达合同名称和有效期的拟变更，批准不改写已签合同。 |
| `crm_sales_order` | `id/tenant_id BIGINT N`；`order_no VARCHAR(32) N`；`contract_id/customer_id BIGINT N`；`external_order_no VARCHAR(100) NULL`；`currency CHAR(3) N`；`total_amount NUMERIC(18,2) N`；确认/取消/关闭时间、`close_reason VARCHAR(500) NULL`、`version`、`AUDIT_MUTABLE` | 活跃订单号、非空外部订单号和活跃 `contract_id` 均租户内唯一；状态 `DRAFT/CONFIRMED/CANCELLING/CANCELLED/CLOSED`。仅 `CLOSED` 状态可保存关闭时间和关闭原因；交付、库存、回款、开票和售后事实不在 CRM 表中。 |
| `crm_sales_order_line` | `id/tenant_id/order_id BIGINT N`；`line_no INTEGER N`；`contract_line_id/product_id BIGINT N`；产品快照、数量、成交单价、税率、行金额、`AUDIT_MUTABLE` | 同订单行号唯一；同领域订单复合 FK；数量正数，金额非负，税率为 0 到 1。 |
| `crm_order_cancel` | `id/tenant_id/order_id BIGINT N`；`cancel_no VARCHAR(32) N`；`status VARCHAR(16) N DEFAULT 'PENDING'`；`reason VARCHAR(500) N`；`request_snapshot JSONB N`；处理时间、拒绝原因、`version`、`AUDIT_MUTABLE` | 活跃取消编号租户内唯一；每订单至多一个未完成取消；状态 `PENDING/APPROVED/REJECTED/COMPLETED`。 |

合同根和快照行在同一事务创建，同时写入 `crm_audit_log` 与 `crm_outbox_event`。签署状态转换使用根 `version` 条件更新；任何报价、产品、价格项的后续变更均不得回写合同快照。

## 9. 审批

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_approval_definition` | `id/tenant_id BIGINT N`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`resource_type VARCHAR(32) N`；`definition_version INT N DEFAULT 1`；`status VARCHAR(16) N DEFAULT 'DRAFT'`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 同编码定义版本唯一；每租户、资源类型、编码最多一个启用定义；状态为 `DRAFT/ACTIVE/RETIRED/DISABLED`。 |
| `crm_approval_definition_node` | `id/tenant_id/definition_id BIGINT N`；`node_no INT N`；`name VARCHAR(100) N`；`decision_mode VARCHAR(16) N DEFAULT 'ALL'`；`approver_user_id BIGINT N`；`condition_expression JSONB N DEFAULT '{}'`；`AUDIT_MUTABLE` | 定义和审批人为同租户复合外键；同一节点审批人唯一；决策方式为 `ANY/ALL`；条件是受控字段、比较符和值的 JSON，不执行脚本。 |
| `crm_approval_instance` | `id/tenant_id BIGINT N`；`resource_type VARCHAR(32) N`；`resource_id/definition_id/applicant_user_id BIGINT N`；`definition_code VARCHAR(64) N`；`definition_version INT N`；`definition_snapshot JSONB N`；`status VARCHAR(16) N DEFAULT 'PENDING'`；`current_node_no INT NULL`；`submitted_at/completed_at TIMESTAMPTZ`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 定义和申请人复合外键；同资源只允许一个未结束实例；状态为 `PENDING/APPROVED/REJECTED/WITHDRAWN`。 |
| `crm_approval_task` | `id/tenant_id/instance_id/approver_user_id BIGINT N`；`node_no INT N`；`node_name VARCHAR(100) N`；`decision_mode VARCHAR(16) N`；`status VARCHAR(16) N DEFAULT 'PENDING'`；`transferred_from_task_id BIGINT NULL`；`acted_at TIMESTAMPTZ NULL`；`comment VARCHAR(1000) NULL`；`version BIGINT N DEFAULT 0`；`AUDIT_MUTABLE` | 实例、审批人与转交来源均为同租户复合外键；节点审批人唯一；待办索引 `(tenant_id,approver_user_id,status,created_at)`。 |
| `crm_approval_action` | `id/tenant_id/instance_id/actor_user_id BIGINT N`；`task_id BIGINT NULL`；`action VARCHAR(16) N`；`comment VARCHAR(1000) NULL`；`before_snapshot/after_snapshot JSONB N`；`operation_id/trace_id VARCHAR(64) NULL`；`created_at TIMESTAMPTZ N` | 实例、任务和操作人均为同租户复合外键；动作固定枚举；触发器禁止更新与删除。 |

## 10. 工作台与统计

工作台与确定性销售报表使用已有 CRM 表计算指标；不创建统计、投影、交付、回款、发票或售后表。`analytics:read` 是唯一新增权限，租户创建触发器会同步种子化该权限。漏斗只读取当前 `OPEN` 商机；业绩只按赢单、合同签署和订单确认的发生时间聚合 CRM 金额。

V19 的 `crm_sales_target` 保存个人、指标、日期区间、目标值、状态和版本；指标仅为 `SIGNED_CONTRACT_AMOUNT/CONFIRMED_ORDER_AMOUNT`，状态为 `DRAFT/ACTIVE/RESULT_CONFIRMED`。`crm_sales_target_result` 对每个目标最多保存一条确认后的实际值、达成率、计算快照、确认人和时间，触发器禁止更新或删除。两表均不保存回款、发票、履约或售后字段。

V20 的 `crm_performance_score_rule` 保存名称、匹配指标、`DRAFT/ACTIVE/RETIRED` 状态和版本；`crm_performance_score_band` 保存顺序、达成率下限/可选上限和分值，启用或退役规则的分段禁止更新或删除。`crm_sales_target.score_rule_id` 是可选同租户引用，因此历史目标无需回填。`crm_sales_target_score` 对每个目标和目标结果各至多保存一条不可变评分事实，包含达成率、分值、规则和分段快照、确认人及时间。所有评分表仅保存 CRM 目标指标，禁止加入回款、发票、履约、库存或售后字段。

## 11. 组织与营销连接器

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_connector` | `id/tenant_id BIGINT N`；`connector_no VARCHAR(32) N`；`name VARCHAR(200) N`；`type/status VARCHAR(32/16) N`；`operator_user_id BIGINT N`；`public_pool_id BIGINT NULL`；`secret_hash VARCHAR(100) N`；`version`、`AUDIT_MUTABLE` | 类型为 `MARKETING_WEBHOOK/ORGANIZATION_WEBHOOK`，状态为 `ACTIVE/DISABLED`；营销连接器必须是启用线索公海，组织连接器不得有公海；执行用户必须为同租户启用用户。密钥只保存 BCrypt 哈希。 |
| `crm_connector_event` | `id/tenant_id/connector_id BIGINT N`；`external_event_id VARCHAR(128) N`；`event_type VARCHAR(64) N`；`payload JSONB N`；`payload_hash CHAR(64) N`；`outcome VARCHAR(32) N`；`lead_id BIGINT NULL`；`received_at TIMESTAMPTZ N` | `(tenant_id, connector_id, external_event_id)` 唯一且触发器禁止更新/删除；结果为 `LEAD_CREATED/ACCEPTED`，前者必须引用线索，后者禁止引用线索。 |

连接器监控不创建投影或告警表，而是按 UTC 时间窗关联 `crm_connector_event` 和聚合类型为 `CONNECTOR_EVENT` 的 `crm_outbox_event`，计算受理、线索、待投递、已发布和死信数量。死信重放复用既有 Outbox 行，把状态恢复为 `PENDING` 并写 `crm_audit_log`，不复制连接器事件或 CRM 线索。

## 12. 销售会话、资料库与话术

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_sales_conversation` | `id/tenant_id/customer_id BIGINT N`；`contact_id BIGINT NULL`；`subject VARCHAR(200) N`；`channel VARCHAR(32) N`；`status VARCHAR(16) N`；`summary VARCHAR(2000) NULL`；`owner_user_id BIGINT N`；`closed_at TIMESTAMPTZ NULL`；`version`、`AUDIT_MUTABLE` | 状态为 `OPEN/CLOSED`；关闭时间必须与关闭状态一致；客户、联系人、负责人均用同租户复合 FK；按客户和更新时间索引。 |
| `crm_sales_conversation_entry` | `id/tenant_id/conversation_id BIGINT N`；`direction VARCHAR(16) N`；`content VARCHAR(4000) N`；`occurred_at TIMESTAMPTZ N`；`created_by/created_at` | 方向仅 `INBOUND/OUTBOUND/NOTE`；同租户会话复合 FK；触发器禁止更新和删除；按会话与沟通时间排序。 |
| `crm_sales_document` | `id/tenant_id BIGINT N`；`title VARCHAR(200) N`；`category VARCHAR(64) N`；`content TEXT N`；`status VARCHAR(16) N`；`owner_user_id BIGINT N`；`published_at/archived_at TIMESTAMPTZ NULL`；`version`、`AUDIT_MUTABLE` | 状态为 `DRAFT/PUBLISHED/ARCHIVED`，发布时间和归档时间与状态一致；按租户、状态及更新时间索引。仅保存 CRM 文本资料，不存二进制附件。 |
| `crm_sales_playbook` | `id/tenant_id BIGINT N`；`title VARCHAR(200) N`；`sales_stage VARCHAR(64) N`；`scenario VARCHAR(200) N`；`content TEXT N`；`status VARCHAR(16) N`；`owner_user_id BIGINT N`；`published_at/archived_at TIMESTAMPTZ NULL`；`version`、`AUDIT_MUTABLE` | 状态为 `DRAFT/PUBLISHED/ARCHIVED`，发布时间和归档时间与状态一致；按租户、状态、销售阶段及更新时间索引。仅保存 CRM 话术文本，不接入自动外呼或外部沟通事实。 |

## 13. 后续对象登记

交易域对象不在本冻结版本建表。其主责、主键、租户和跨域引用规则见 [future-contexts.md](future-contexts.md)；任何正式字段必须经对应阶段门并通过新 Flyway 版本创建，禁止提前塞入 `crm_lead.extension`、`crm_customer.extension` 或 `crm_opportunity.extension`。
