# 获客渠道与归因

V22 在 `aicrm-sales` 内实现 CRM 自主管理的获客渠道。渠道不代表外部平台账户、广告消耗、财务成本或 ERP 履约事实；它只规范线索进入 CRM 时的来源归因。

`crm_acquisition_channel` 使用租户内不可重复的 `code`、显示名称和 `source_type`。状态为 `DRAFT -> ACTIVE -> DISABLED`，停用渠道可查询历史，但不能归因新的线索。创建、启用、停用分别需要 `channel:manage` 和匹配的 `Idempotency-Key` 或 `version`，均写审计与 Outbox。

创建线索可选 `acquisitionChannelId`。服务端只接受同租户的启用渠道，且渠道 `source_type` 必须等于请求 `sourceType`；随后把渠道 ID 与当时的渠道编码复制到 `crm_lead`。渠道日后改名、停用或删除均不能回写已有线索归因。未指定渠道的既有手工线索和连接器线索保持兼容。
