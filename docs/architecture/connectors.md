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
