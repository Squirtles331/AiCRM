# 阶段 4E：组织与营销连接器

## 范围

V21 和 `aicrm-integration` 只实现 CRM 受控的入站 Webhook。连接器有租户、启停状态、执行用户、公海配置与 BCrypt 单向共享密钥哈希；明文密钥仅在创建请求中传入，永不在读取接口、审计或 Outbox 载荷中返回。

`MARKETING_WEBHOOK` 接收营销捕获事件并以配置的启用 CRM 用户身份创建公海线索。`ORGANIZATION_WEBHOOK` 只保存不可变的接收回执和领域事件，不创建或更新 `crm_user`、`crm_department`、角色或权限。没有 ERP、库存、履约、回款、开票、售后、退款或客服事件适配器。

## API 与幂等

管理 API 需要 JWT：`POST /api/v1/connectors` 需要 `connector:manage` 与 `Idempotency-Key`；`GET /api/v1/connectors/{id}` 需要 `connector:read/manage`；启用和停用命令需要 `connector:manage` 与当前 `version`。

入站 API 是唯一允许匿名访问的 V1 业务地址：`POST /api/v1/connectors/{id}/events`。调用方必须提供 `X-Connector-Secret`，正文必须含 `eventId`、`eventType`；营销正文还必须含 `lead.name`。同租户、同连接器、同事件编号只能接收一次，并比较规范 JSON 的 SHA-256。相同载荷返回原回执且 `replayed=true`；不同载荷复用事件编号返回 `IDEMPOTENCY_KEY_REUSED`。

营销事件在同一事务中创建线索、追加不可变 `crm_connector_event`、审计与 `ConnectorEventAccepted` Outbox；线索自身仍会产生既有 `LeadCreated` 审计与 Outbox。组织事件只追加接收记录、审计和 `ConnectorEventAccepted`。停用连接器或密钥不匹配返回 `403`。

## 数据与状态

`crm_connector.status` 为 `ACTIVE/DISABLED`，状态变更采用乐观锁。营销连接器必须引用同租户、启用的 `LEAD` 公海；组织连接器禁止填写公海。两类连接器都必须绑定同租户启用用户，营销连接器执行用户还必须拥有 `lead:create`。

`crm_connector_event` 是追加式事实，唯一键为 `(tenant_id, connector_id, external_event_id)`。结果只可能为 `LEAD_CREATED` 或 `ACCEPTED`；前者必须引用 CRM 线索，后者不得引用线索。原始 JSON 仅保存在受控事件表，不被放入 Outbox 载荷。

## 投递重试与监控

每条连接器接收记录都会有一条 `ConnectorEventAccepted` Outbox。现有发布器对 `PENDING/FAILED` 自动按 1 分钟、5 分钟、30 分钟、2 小时、12 小时退避重试；耗尽后为 `DEAD`。这是事件投递可靠性，不会重放线索创建或组织事件接收事务。

`GET /api/v1/connectors/{id}/monitoring?from&to` 返回 UTC 左闭右开、1 至 31 天窗口中的受理数、营销线索数、组织接收数及对应 Outbox 的待投递、已发布、死信数。省略日期为最近七个 UTC 自然日。`connector:read/manage` 可查看计数；最近故障的错误文本仅对 `connector:manage` 返回。

`POST /api/v1/connectors/{id}/outbox/{outboxEventId}/actions/retry` 只允许同时具备 `connector:manage` 和 `outbox:retry` 的管理员操作。目标必须属于该连接器且状态为 `DEAD`；操作把其恢复为 `PENDING`、清除错误和租约、重置尝试计数，并写 `OUTBOX/RETRY` 审计。它不创建新的 Outbox 事件，随后由原有调度器投递。
