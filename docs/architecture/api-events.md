# API 与事件契约

## 1. HTTP 规范

版本前缀为 `/api/v1`。成功与失败均使用 `code/message/data/traceId`；HTTP 状态码表达真实语义。分页参数从 1 开始，默认 `page=1,size=20`，最大 `size=200`。所有 ID 在 JSON 中表示为字符串。

创建、转换、合并、交接和外部回调必须携带 `Idempotency-Key`。更新命令必须携带聚合 `version`；版本冲突返回 `409 VERSION_CONFLICT`。所有请求接受/返回 `X-Trace-Id`，缺失时服务端生成；写命令的 JSON `Content-Type` 固定为 `application/json`。

列表响应为 `data.items/page/size/total`；时间为 ISO-8601 UTC，例如 `2026-09-07T02:30:00Z`。手机号、邮箱等字段在无查看权限时返回脱敏值，在完全无字段权限时省略；服务端绝不根据前端隐藏状态授权。

## 2. 第一里程碑端点

| 方法与路径 | 用途 | 权限 |
|---|---|---|
| `POST /api/v1/auth/login` | 租户、用户名、密码登录 | 匿名；仅签发身份 JWT，权限从数据库加载 |
| `GET /api/v1/leads/private` | 线索私海 | `lead:read:own/any` + 数据范围 |
| `GET /api/v1/leads/public` | 线索公海 | 已登录用户 |
| `POST /api/v1/leads` | 创建私海或公海线索 | `lead:create` |
| `POST /api/v1/leads/{id}/actions/claim` | 认领 | `lead:claim` |
| `POST /api/v1/leads/{id}/actions/release|assign|transfer|convert|invalidate` | 归属与状态命令，请求含 `version/reason` 及目标 ID | 对应独立权限 |
| `POST /api/v1/leads/{id}/follow-ups` | 记录跟进 | `lead:write:own/any` |
| `GET /api/v1/customers/private` | 客户私海 | `customer:read:own/any` + 数据范围 |
| `GET /api/v1/customers/public` | 客户公海 | 已登录用户 |
| `POST /api/v1/customers` | 创建私海或公海客户 | `customer:create` |
| `POST /api/v1/customers/{id}/actions/claim|release|assign|transfer|merge` | 归属与合并命令 | 对应权限 |
| `GET/POST /api/v1/customers/{id}/contacts` | 联系人列表与新增 | 客户读/写权限 |
| `POST /api/v1/customers/{id}/follow-ups` | 记录跟进 | `customer:write:own/any` |
| `POST /api/v1/handovers` | 指定资源离职交接 | `lead:handover/customer:handover` |
| `POST /api/v1/handovers/batch` | 批量离职交接，含 `batchNo/fromUserId/toUserId/pageSize` | 同时具备 `lead:handover` 与 `customer:handover` |
| `GET/POST /api/v1/opportunities` | 商机列表与创建 | `opportunity:read:own/any` + 数据范围；`opportunity:create` |
| `GET /api/v1/opportunities/{id}` | 商机详情 | `opportunity:read:own/any` + 数据范围 |
| `GET /api/v1/opportunities/{id}/stage-history` | 商机阶段历史 | 商机查看权限 |
| `POST /api/v1/opportunities/{id}/actions/stage|win|lose|restart` | 推进、赢单、输单和重启，请求含 `version` | 对应 `opportunity:*` 命令权限 |
| `POST /api/v1/quotes` | 以生效价目表创建带快照行的报价 | `quote:create` + `Idempotency-Key` |
| `GET /api/v1/quotes/{id}` | 查询报价根 | `quote:read:own/any` + 商机数据范围 |
| `GET /api/v1/quotes/{id}/versions` | 查询报价版本 | 同报价查看权限 |
| `GET /api/v1/quotes/{id}/versions/{versionNo}/lines` | 查询指定版本快照行 | 同报价查看权限 |
| `POST /api/v1/quotes/{id}/versions` | 为已驳回报价创建重报版本；请求须含 `expectedQuoteVersion`、报价行和 `Idempotency-Key` | `quote:write:own/any` + 商机数据范围 |
| `POST /api/v1/quotes/{id}/actions/submit` | 提交报价；请求须含 `rootVersion,version,approvalDefinitionCode` 和 `Idempotency-Key` | `quote:submit` |
| `POST /api/v1/quotes/{id}/actions/withdraw-approval` | 撤回在途报价审批；请求须含 `approvalInstanceId,instanceVersion` | `quote:withdraw` |
| `POST /api/v1/quotes/{id}/actions/expire` | 已批准报价到期处理；请求须含 `rootVersion,version` | `quote:expire` |
| `POST /api/v1/contracts` | 从已批准报价的当前批准版本创建合同草稿；请求须含合同名称和 `Idempotency-Key` | `contract:create` + 来源商机数据范围 |
| `GET /api/v1/contracts/{id}` | 查询合同根 | `contract:read:own/any` + 来源商机数据范围 |
| `GET /api/v1/contracts/{id}/lines` | 查询合同不可变商业快照行 | 同合同查看权限 |
| `POST /api/v1/contracts/{id}/actions/submit-signature` | 提交草稿合同签署；请求须含 `version` | `contract:submit-signature` |
| `POST /api/v1/contracts/{id}/actions/sign` | 确认待签署合同已签；请求须含 `version` | `contract:sign` |
| `POST /api/v1/contracts/{id}/actions/withdraw-signature|void` | 撤回签署或作废合同；请求须含 `version`，作废原因必填 | `contract:withdraw-signature` / `contract:void` |
| `POST /api/v1/contracts/{id}/changes` 及变更的 `submit|cancel|approve|reject` 动作 | 创建并处理合同变更台账；创建须含 `Idempotency-Key`，状态操作须含变更 `version` | `contract:change` / `contract:approve-change` |
| `POST /api/v1/orders` | 从已签合同创建完整销售订单草稿；请求含 `expectedDeliveryAt` 和 `Idempotency-Key` | `order:create` + 合同来源数据范围 |
| `GET /api/v1/orders/{id}` | 查询销售订单根 | `order:read:own/any` + 合同来源数据范围 |
| `GET /api/v1/orders/{id}/lines` | 查询销售订单不可变商业快照行 | 同订单查看权限 |
| `POST /api/v1/orders/{id}/actions/confirm|request-cancel|close` | 确认、申请取消或 CRM 内部关闭订单；申请须含 `Idempotency-Key`，关闭原因必填 | `order:confirm` / `order:cancel` / `order:close` |
| `POST /api/v1/orders/{id}/cancellations/{cancelId}/actions/approve|reject` | 销售管理员决定订单取消；请求须含订单与取消申请版本 | `order:approve-cancel` |
| `GET /api/v1/workbench/summary?from&to` | 查询 CRM 工作台销售汇总；日期为 UTC 左闭右开区间，省略时为当月 | `analytics:read` + 数据范围 |
| `GET /api/v1/reports/sales-funnel` | 查询当前可见 CRM 商机漏斗快照；含阶段数量、预计及加权金额 | `analytics:read` + 数据范围 |
| `GET /api/v1/reports/sales-performance?from&to` | 查询区间 CRM 赢单、合同签署和订单确认业绩；日期为 UTC 左闭右开区间 | `analytics:read` + 数据范围 |
| `POST /api/v1/sales-targets` | 创建个人 CRM 销售目标；须含 `Idempotency-Key` | `target:manage` |
| `GET /api/v1/sales-targets/{id}` | 查询本人或管理员范围内的销售目标与已确认结果 | `target:read:own/any` |
| `POST /api/v1/sales-targets/{id}/actions/activate|confirm-result` | 启用目标或确认期末 CRM 结果；请求含 `version` | `target:manage` / `target:confirm` |
| `POST /api/v1/performance-score-rules` | 创建 CRM 绩效评分规则与连续达成率分段；须含 `Idempotency-Key` | `performance:rule:manage` |
| `GET /api/v1/performance-score-rules/{id}` | 查询租户内绩效评分规则和分段 | `performance:rule:read/manage` |
| `POST /api/v1/performance-score-rules/{id}/actions/activate|retire` | 启用或退役规则；请求含 `version` | `performance:rule:manage` |
| `POST /api/v1/approval-definitions` | 创建审批定义 | `approval:manage` |
| `POST /api/v1/approval-definitions/{id}/actions/activate` | 启用审批定义，须含定义版本 | `approval:manage` |
| `GET /api/v1/approval-tasks/pending` | 当前用户待审批任务 | `approval:task:read` |
| `POST /api/v1/approval-tasks/{id}/actions/approve|reject|transfer` | 处理当前用户的审批任务，须含任务版本 | `approval:task:act` |

命令成功返回更新后的资源摘要及新 `version`；创建返回 201，幂等重放返回原 HTTP 状态和原响应。认领/版本竞争返回 409；池停用返回 422；跨租户 ID 与不存在统一返回 404。批量接口必须返回逐项结果，且 `batchNo` 可用于审计检索。

## 3. 事件目录

| 事件 | 聚合 | 关键载荷 | 消费目的 |
|---|---|---|---|
| `LeadCreated` | Lead | `leadId,ownershipType,sourceType` | 报表、外部同步 |
| `LeadClaimed/Released/Assigned/Transferred/Recycled` | Lead | 前后负责人、部门、公海、规则版本 | 审计、提醒 |
| `LeadInvalidated` | Lead | `leadId,reason` | 渠道质量统计 |
| `LeadConverted` | Lead | `leadId,customerId` | 主数据衔接 |
| `CustomerCreated` | Customer | `customerId,ownershipType` | 报表、外部同步 |
| `CustomerClaimed/Released/Assigned/Transferred/Recycled` | Customer | 前后负责人、部门、公海、规则版本 | 审计、提醒 |
| `CustomerMerged` | Customer | `sourceCustomerId,targetCustomerId` | 下游引用修正 |
| `ResourcesHandedOver` | Handover | `fromUserId,toUserId,resourceIds` | 离职流程确认 |
| `CategoryCreated` | ProductCategory | `categoryId,code` | 目录同步、审计 |
| `ProductCreated` | Product | `productId,sku` | 目录同步、报价可选产品投影 |
| `PriceListCreated/PriceItemCreated` | PriceList / PriceItem | 价目表和价格项快照 | 报价准备、审计 |
| `PriceListPublished` | PriceList | `priceListId,currency,effectiveFrom` | 报价可用价格表投影 |
| `OpportunityCreated` | Opportunity | `opportunityId,customerId,stage` | 工作台、报表 |
| `OpportunityStageChanged` | Opportunity | `opportunityId,stage,probability` | 漏斗报表、提醒 |
| `OpportunityWon/Lost/Restarted` | Opportunity | `opportunityId,status,reason` | 后续报价、经营报表 |
| `QuoteCreated` | Quote | `quoteId,opportunityId,priceListId,currentVersionNo` | 审计、工作台 |
| `QuoteSubmitted/Approved/Rejected/ApprovalWithdrawn/Expired` | Quote | `quoteId,status,currentVersionNo` | 审批衔接、提醒、报表 |
| `ContractCreated` | Contract | `contractId,quoteId,quoteVersionId,customerId,totalAmount` | 合同台账、审计 |
| `ContractSignatureSubmitted/ContractSigned` | Contract | `contractId,status,version` | 待办、订单准入 |
| `ContractSignatureWithdrawn/ContractVoided` | Contract | `contractId,status,version` | 合同台账、审计 |
| `ContractChangeCreated/Submitted/Cancelled/Approved/Rejected` | ContractChange | `changeId,contractId,status,version` | 变更台账、审计 |
| `OrderCreated/Confirmed/CancellationRequested/CancellationRejected/Cancelled/Closed` | Order | `orderId,contractId,status,version` | 销售订单台账、审计 |
| `SalesTargetCreated/Activated/ResultConfirmed` | SalesTarget | `targetId,targetUserId,metric,status,version` | CRM 目标与确认结果审计 |
| `PerformanceScoreRuleCreated/Activated/Retired` | PerformanceScoreRule | `ruleId,metric,status,version` | 评分规则审计 |
| `SalesTargetScoreConfirmed` | SalesTargetScore | `targetId,targetResultId,scoreRuleId,achievementRate,score` | CRM 绩效评分审计 |
| `ApprovalDefinitionCreated/Activated` | Approval | `definitionId,resourceType,status` | 审计、配置投影 |
| `ApprovalStarted` | Approval | `instanceId,resourceType,resourceId,definitionVersion` | 待办、提醒 |
| `ApprovalTaskApproved/Rejected/Transferred` | Approval | `instanceId,taskId,status` | 待办、业务状态同步 |
| `ApprovalWithdrawn` | Approval | `instanceId,resourceType,resourceId` | 待办取消、业务状态同步 |

事件信封固定包含 `eventId,eventType,eventVersion,tenantId,aggregateType,aggregateId,operationId,occurredAt,traceId,payload`。新增可选字段保持同版本，删除/改义或类型变化必须升 `eventVersion` 并提供兼容期。

事件先写 `crm_outbox_event`，提交后再投递 RabbitMQ；消费者以 Inbox 或业务幂等键去重。消息不是事实源，失败必须可重试和对账。消费者不得通过事件反查另一个租户的数据。

RabbitMQ 使用持久化 Topic Exchange `crm.events`。领域事件路由键为 `crm.{aggregateType小写}.{eventType小写}`，例如 `crm.lead.leadcreated`；消息 `messageId` 等于 Outbox ID。信封中的所有 ID 均为字符串，`payload` 为 JSON 对象而不是转义后的 JSON 字符串。发布端必须等待 Broker Confirm，未确认时保留数据库事件并进入退避重试。

## 4. 单轨约束

`/api/leads`、`/api/customers`、`/api/auth/login` 等旧路径必须返回 404，且不得提供重定向、适配 DTO 或双写。尚未进入领域阶段门的能力视为未提供；必须先完成领域设计与 Flyway 迁移，再以新的 `/api/v1` 契约交付。
