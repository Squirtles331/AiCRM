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

事件信封固定包含 `eventId,eventType,eventVersion,tenantId,aggregateType,aggregateId,operationId,occurredAt,traceId,payload`。新增可选字段保持同版本，删除/改义或类型变化必须升 `eventVersion` 并提供兼容期。

事件先写 `crm_outbox_event`，提交后再投递 RabbitMQ；消费者以 Inbox 或业务幂等键去重。消息不是事实源，失败必须可重试和对账。消费者不得通过事件反查另一个租户的数据。

RabbitMQ 使用持久化 Topic Exchange `crm.events`。领域事件路由键为 `crm.{aggregateType小写}.{eventType小写}`，例如 `crm.lead.leadcreated`；消息 `messageId` 等于 Outbox ID。信封中的所有 ID 均为字符串，`payload` 为 JSON 对象而不是转义后的 JSON 字符串。发布端必须等待 Broker Confirm，未确认时保留数据库事件并进入退避重试。

## 4. 单轨约束

`/api/leads`、`/api/customers`、`/api/auth/login` 等旧路径必须返回 404，且不得提供重定向、适配 DTO 或双写。尚未进入领域阶段门的能力视为未提供；必须先完成领域设计与 Flyway 迁移，再以新的 `/api/v1` 契约交付。
