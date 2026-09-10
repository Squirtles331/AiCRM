# 阶段 4：工作台、目标与确定性销售统计

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

## 销售目标与结果确认

V19 增加个人销售目标与不可变结果快照。目标可为 `DRAFT`、`ACTIVE` 或 `RESULT_CONFIRMED`；管理员创建和启用，目标期结束后由具备 `target:confirm` 权限的管理员确认一次结果。创建使用 `Idempotency-Key`，状态命令使用 `version`。

首版只有两种金额指标：`SIGNED_CONTRACT_AMOUNT` 按合同签署时间统计，`CONFIRMED_ORDER_AMOUNT` 按订单确认时间统计。二者均以来源商机的负责人归属到个人目标，且只累计 CRM 中的合同或订单金额。结果确认固化实际金额、达成率和计算输入快照，之后不会因合同、订单或负责人变化回写。

权限为 `target:read:own`、`target:read:any`、`target:manage` 和 `target:confirm`。不存在以到账、开票、履约、退货、退款、工单或外部系统数据作为目标或绩效依据的字段、表或接口。

## 规则驱动计分

V20 增加评分规则、达成率分段与目标评分快照。规则只能匹配 `SIGNED_CONTRACT_AMOUNT` 或 `CONFIRMED_ORDER_AMOUNT` 两种既有 CRM 指标；管理员创建连续覆盖 `[0,+∞)` 的达成率区间和对应分值，并将规则从 `DRAFT` 启用，之后可退役但不可修改其分段。

创建销售目标时可选绑定同指标的已启用规则。目标期结束并确认结果时，系统从该规则中选择 `[minimumAchievementRate, maximumAchievementRate)` 分段，固化规则编号、指标、分段、达成率和分值到不可变 `crm_sales_target_score`。未绑定规则的既有目标保持原有结果确认行为；规则退役后不再允许新绑定，但已绑定目标仍使用原分段完成一次评分。

规则权限为 `performance:rule:read` 与 `performance:rule:manage`。评分仍只依赖 CRM 内目标实际金额和达成率，不能使用到账、发票、履约、库存、退货、退款、工单或外部事实。

## 确定性销售报表

`GET /api/v1/reports/sales-funnel` 返回当前时刻的可见 `OPEN` 商机阶段分布、预计金额与按商机概率计算的加权金额。它是当前快照，不能也不会用当前状态伪造任意历史时点的漏斗。

`GET /api/v1/reports/sales-performance?from&to` 返回 UTC 左闭右开日期区间内、按来源商机当前负责人聚合的 CRM 赢单、合同签署和订单确认数量及金额。日期省略时为当前 UTC 自然月，最大跨度为 366 天。已关闭订单仍代表曾在 CRM 中确认的销售订单，故仍计入确认订单指标。

两个报表均要求 `analytics:read`，并复用本人、部门、部门及下级、全部四级数据范围；不创建报表投影、不写审计或 Outbox。报表仅包含 CRM 销售事实，明确排除到账、发票、履约、库存、工单、退款和任何外围系统数据。
