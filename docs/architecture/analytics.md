# 阶段 4A：工作台与确定性销售统计

## 范围

`aicrm-analytics` 提供 `GET /api/v1/workbench/summary`。这是 CRM 内部的只读聚合，不创建投影表、不写入 Outbox，也不接入 ERP、库存、财务、开票、售后或客服系统。

调用者必须拥有 `analytics:read`。每项查询都包含 `tenant_id`，并复用本人、部门、部门及下级、全部四级数据范围。线索和客户公海仍按原有公海可见规则计入；商机及其派生的报价、合同和订单按来源商机负责人范围过滤。

## 指标契约

请求的 `from` 与 `to` 是 UTC 日期，区间为 `[from, to)`；省略时默认当前 UTC 自然月，最大范围为 366 天。返回包含：

- `leadsCreated`、`customersCreated`：区间内创建的 CRM 主数据。
- `openOpportunities`：请求时仍处于 `OPEN` 的可见商机。
- `opportunitiesWon`、`opportunitiesLost`：区间内赢单或输单的商机。
- `quotesSubmitted`、`quotesApproved`：区间内提交或获批的报价版本。
- `contractsSigned`：区间内完成签署的合同。
- `ordersConfirmed`、`ordersCancelled`、`ordersClosed`：区间内确认、取消或 CRM 内部关闭的销售订单。
- `overdueFollowUps`：请求时处于有效状态且下一次跟进时间早于数据库当前时间的可见线索和客户数量。

合同、订单指标只反映 CRM 自己的签署、确认、取消和手工关闭状态。工作台不得出现回款、发票、交付、库存、工单、退款或复购字段，更不得以任何外部事实推进订单状态。
