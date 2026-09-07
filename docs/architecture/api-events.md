# API 与事件契约

## 1. HTTP 规范

版本前缀为 `/api/v1`。成功与失败均使用 `code/message/data/traceId`；HTTP 状态码表达真实语义。分页参数从 1 开始，默认 `page=1,size=20`，最大 `size=200`。所有 ID 在 JSON 中表示为字符串。

创建、转换、合并和交接请求必须携带 `Idempotency-Key`。更新命令必须携带聚合 `version`；版本冲突返回 `409 CONFLICT`。

## 2. 第一里程碑端点

| 方法与路径 | 用途 | 权限 |
|---|---|---|
| `GET /api/v1/leads/private` | 线索私海 | `lead:read:own/any` + 数据范围 |
| `GET /api/v1/leads/public` | 线索公海 | 已登录用户 |
| `POST /api/v1/leads` | 创建私海或公海线索 | `lead:create` |
| `POST /api/v1/leads/{id}/actions/claim` | 认领 | `lead:claim` |
| `POST /api/v1/leads/{id}/actions/release|assign|transfer|convert|invalidate` | 归属与状态命令 | 对应写/分配权限 |
| `POST /api/v1/leads/{id}/follow-ups` | 记录跟进 | `lead:write:own/any` |
| `GET /api/v1/customers/private` | 客户私海 | `customer:read:own/any` + 数据范围 |
| `GET /api/v1/customers/public` | 客户公海 | 已登录用户 |
| `POST /api/v1/customers` | 创建私海或公海客户 | `customer:create` |
| `POST /api/v1/customers/{id}/actions/claim|release|assign|transfer|merge` | 归属与合并命令 | 对应权限 |
| `GET/POST /api/v1/customers/{id}/contacts` | 联系人列表与新增 | 客户读/写权限 |
| `POST /api/v1/customers/{id}/follow-ups` | 记录跟进 | `customer:write:own/any` |
| `POST /api/v1/handovers` | 指定资源离职交接 | `lead:handover/customer:handover` |

## 3. 事件目录

| 事件 | 聚合 | 关键载荷 | 消费目的 |
|---|---|---|---|
| `LeadCreated` | Lead | `leadId,ownershipType,sourceType` | 报表、外部同步 |
| `LeadClaimed/Released/Assigned/Transferred` | Lead | 前后负责人和公海 | 审计、提醒 |
| `LeadInvalidated` | Lead | `leadId,reason` | 渠道质量统计 |
| `LeadConverted` | Lead | `leadId,customerId` | 主数据衔接 |
| `CustomerCreated` | Customer | `customerId,ownershipType` | 报表、外部同步 |
| `CustomerClaimed/Released/Assigned/Transferred` | Customer | 前后负责人和公海 | 审计、提醒 |
| `CustomerMerged` | Customer | `sourceCustomerId,targetCustomerId` | 下游引用修正 |
| `ResourcesHandedOver` | Handover | `fromUserId,toUserId,resourceIds` | 离职流程确认 |

事件先写 `crm_outbox_event`，提交后再投递 RabbitMQ；消费者以 Inbox 或业务幂等键去重。消息不是事实源，失败必须可重试和对账。
