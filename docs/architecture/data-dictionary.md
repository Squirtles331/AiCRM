# 第一里程碑数据字典（冻结版）

版本：`M1-DB-1.0`；状态：待团队评审；数据库：PostgreSQL 16+。本文件与 Flyway `V1` 至 `V7` 共同构成字段冻结基线；SQL 为物理事实源，文档不得单独变更。

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
| `crm_user` | `id BIGINT N`；`tenant_id BIGINT N`；`department_id BIGINT N`；`username VARCHAR(100) N`；`name VARCHAR(100) N`；`mobile VARCHAR(32) NULL`；`email VARCHAR(200) NULL`；`status SMALLINT N DEFAULT 1`；`AUDIT_MUTABLE` | PK；部门复合 FK；活跃用户名/手机号/邮箱租户内唯一；`status IN (0,1)`；部门索引 |
| `crm_role` | `id BIGINT N`；`tenant_id BIGINT N`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`status SMALLINT N DEFAULT 1`；`data_scope VARCHAR(32) N DEFAULT 'SELF'`；`AUDIT_MUTABLE` | PK；活跃 `(tenant_id,code)` 唯一；`data_scope` 为 `SELF/DEPARTMENT/DEPARTMENT_AND_SUB/ALL` |
| `crm_permission` | `tenant_id BIGINT N`；`code VARCHAR(100) N`；`name VARCHAR(200) N`；`resource_type VARCHAR(64) N`；`action VARCHAR(64) N`；`status SMALLINT N DEFAULT 1`；`description VARCHAR(500) NULL`；`AUDIT_MUTABLE` | PK `(tenant_id,code)`；权限随新租户自动初始化 |
| `crm_user_role` | `tenant_id BIGINT N`；`user_id BIGINT N`；`role_id BIGINT N`；`AUDIT_MUTABLE` | PK `(tenant_id,user_id,role_id)`；用户和角色复合 FK 保证同租户 |
| `crm_role_permission` | `tenant_id BIGINT N`；`role_id BIGINT N`；`permission_code VARCHAR(100) N`；`AUDIT_MUTABLE` | PK `(tenant_id,role_id,permission_code)`；角色和权限复合 FK |
| `crm_role_field_permission` | `tenant_id BIGINT N`；`role_id BIGINT N`；`resource_type VARCHAR(64) N`；`field_name VARCHAR(100) N`；`can_view/edit/export BOOLEAN N DEFAULT false`；`AUDIT_MUTABLE` | 组合 PK；编辑或导出为真时查看必须为真；角色复合 FK |

首期预置高风险权限包括 `lead/customer:assign`、`:transfer`、`:batch`、`:export`、`:handover`、`lead:convert`、`customer:merge`、`approval:manage`、`audit:read` 和 `outbox:retry`。权限码不可复用为不同语义。

## 3. 公海与销售主数据

| 表 | 字段（类型；N=NOT NULL；默认值） | 键、检查与主要索引 |
|---|---|---|
| `crm_public_pool` | `id BIGINT N`；`tenant_id BIGINT N`；`resource_type VARCHAR(20) N`；`code VARCHAR(64) N`；`name VARCHAR(100) N`；`status SMALLINT N DEFAULT 1`；`auto_recycle_enabled BOOLEAN N DEFAULT false`；`recycle_after_days INT NULL`；`claim_enabled/assign_enabled/release_enabled BOOLEAN N DEFAULT true`；`rule_version INT N DEFAULT 1`；`effective_from TIMESTAMPTZ N DEFAULT now()`；`AUDIT_MUTABLE` | `resource_type IN (LEAD,CUSTOMER)`；启用记录 `(tenant_id,resource_type,code)` 唯一；版本唯一；停用时三个手工动作均关闭；规则字段不可原地修改，只能停用旧版并新增高版本 |
| `crm_lead` | `id BIGINT N`；`tenant_id BIGINT N`；`lead_no VARCHAR(32) N`；`name VARCHAR(100) NULL`；`mobile VARCHAR(32) NULL`；`email VARCHAR(200) NULL`；`company_name VARCHAR(200) NULL`；`source_type VARCHAR(64) N`；`source_ref VARCHAR(200) NULL`；`intent VARCHAR(64) NULL`；`status VARCHAR(32) N DEFAULT 'NEW'`；`invalid_reason VARCHAR(500) NULL`；`customer_id BIGINT NULL`；`ownership_type VARCHAR(16) N`；`owner_user_id/owner_dept_id/public_pool_id BIGINT NULL`；`pool_entered_at/last_follow_up_at/next_follow_up_at TIMESTAMPTZ NULL`；`version BIGINT N DEFAULT 0`；`extension JSONB N DEFAULT '{}'`；`AUDIT_MUTABLE` | 活跃 `(tenant_id,lead_no)` 唯一；状态和终态数据检查；公私海互斥；租户+归属+负责人/部门/公海+跟进时间组合索引；复合 FK 保证引用同租户 |
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

## 5. 后续对象登记

交易域对象不在本冻结版本建表。其主责、主键、租户和跨域引用规则见 [future-contexts.md](future-contexts.md)；任何正式字段必须经对应阶段门并通过新 Flyway 版本创建，禁止提前塞入 `crm_lead.extension` 或 `crm_customer.extension`。
