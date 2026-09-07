# 数据字典

## 1. 通用字段规范

聚合根统一使用应用生成的 `BIGINT id`，HTTP 层以字符串输出；业务编号单独建列。租户业务表必须包含 `tenant_id`，可变聚合包含 `version`；审计字段为 `created_by/created_at/updated_by/updated_at`，软删除字段为 `deleted_at/deleted_by`。时间使用 `TIMESTAMPTZ` 并按 UTC 存储；金额使用 `NUMERIC(18,2)`，币种使用 ISO 4217 三字符代码。

所有查询以 `tenant_id` 为首要条件。核心检索条件必须结构化建列；`JSONB` 仅用于扩展字段、规则快照与外部原文。跨上下文只保存聚合标识，不建立对象级依赖。

## 2. 平台与销售表

| 表 | 主责 | 关键字段 | 约束与索引 |
|---|---|---|---|
| `crm_tenant` | 租户 | `id,name,status` | 主键 `id` |
| `crm_department` | 组织树 | `tenant_id,parent_id,path,status` | 租户+父部门索引 |
| `crm_user` | 平台用户 | `tenant_id,department_id,status` | 租户+部门索引 |
| `crm_role` | 角色与数据范围 | `tenant_id,code,data_scope,status` | 活跃角色编码唯一 |
| `crm_permission` | 功能权限 | `code,resource_type,action` | 权限码主键 |
| `crm_user_role` | 用户角色 | `tenant_id,user_id,role_id` | 组合主键 |
| `crm_role_permission` | 角色授权 | `tenant_id,role_id,permission_code` | 组合主键 |
| `crm_idempotency_record` | 命令去重 | `operation,idempotency_key,status,response_body` | 租户+操作+键唯一 |
| `crm_audit_log` | 不可变审计 | `actor_user_id,action,resource_type,resource_id,before_data,after_data,trace_id` | 资源时间倒序索引 |
| `crm_outbox_event` | 事务事件 | `aggregate_type,aggregate_id,event_type,payload,published_at,retry_count` | 未发布部分索引 |
| `crm_public_pool` | 线索/客户公海 | `resource_type,code,name,status,recycle_after_days` | 活跃池编码唯一 |
| `crm_lead` | 线索聚合 | `lead_no,source_type,status,ownership_type,owner_user_id,owner_dept_id,public_pool_id,customer_id,last_follow_up_at,next_follow_up_at,version` | 私海、公海、待跟进索引 |
| `crm_customer` | 客户主数据 | `customer_no,name,status,ownership_type,owner_user_id,owner_dept_id,public_pool_id,last_follow_up_at,next_follow_up_at,version` | 编号、活跃名称唯一；私海/公海索引 |
| `crm_contact` | 客户联系人 | `customer_id,name,mobile,email,is_decision_maker,version` | 客户索引 |
| `crm_follow_up` | 跟进事实 | `lead_id/customer_id,actor_user_id,channel,content,next_follow_up_at` | 目标+时间索引，目标二选一 |
| `crm_ownership_history` | 归属变更历史 | `resource_type,resource_id,action,from/to_owner,from/to_pool,operator_user_id` | 资源+时间索引，不可更新 |
| `crm_resource_handover` | 离职交接事实 | `from_user_id,to_user_id,resource_type,resource_id,status` | 原负责人+时间索引 |

## 3. 归属不变量

- 私海：`ownership_type=PRIVATE`、`owner_user_id` 必填、`public_pool_id` 为空。
- 公海：`ownership_type=PUBLIC`、`owner_user_id` 为空、`public_pool_id` 必填。
- 私海记录同时保存 `owner_dept_id`，用于部门与下级部门数据范围过滤。
- 认领必须以 `id + tenant_id + pool_id + ownership_type + version` 条件更新，受影响行数为 1 才成功。
- 转换、合并、回收和交接必须在同一事务写聚合、归属历史、审计日志与 Outbox。

## 4. 后续上下文对象清单

后续阶段必须在编码前扩充到字段级设计：产品、分类、价格表、价格项、折扣规则、商机、商机阶段历史、报价及版本、审批定义/实例/节点、合同及变更、销售订单及取消、交付计划/发货回传/验收/异常、回款计划/到账/核销/冲销、开票申请/发票/红冲、售后工单、退货、退款和复购关联。
