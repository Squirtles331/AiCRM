# 后续交易域上下文登记

产品与价格目录已在阶段 2A 通过 V10 建表。下列其余对象仅冻结主责和引用方向，不代表字段级设计已通过；正式表必须在对应阶段门用新 Flyway 版本创建。

| 上下文 | 对象 | 主键与租户 | 跨域引用规则 |
|---|---|---|---|
| Catalog | 分类、产品、价目表、价格项已实现；折扣规则待后续迁移 | Snowflake `BIGINT`；租户表必带 `tenant_id` | 销售/交易只存 `product_id/price_list_id` 和成交快照；不 FK 到 Catalog |
| Opportunity | 商机、阶段历史、输单、重启 | 聚合根和历史均 Snowflake；租户隔离 | 引用 `customer_id/contact_id/lead_id`；客户主数据不反向持有商机字段 |
| Quotation | 报价、报价版本、报价审批 | 报价根 ID + 不可变版本 ID；租户隔离 | 引用商机/客户/产品 ID；每版固化品名、价格、税率和币种快照 |
| Contract | 合同、合同变更、合同作废 | 合同根和变更 ID；租户隔离 | 引用获批报价/客户 ID；客户合并通过事件更新引用映射，不级联删除 |
| Order | 销售订单、订单取消 | 订单根和行 ID；租户隔离 | 引用合同、客户、产品 ID；订单行保留商业快照 |
| Fulfillment | 交付计划、发货回传、验收、交付异常 | 各聚合 Snowflake；租户隔离 | 引用订单/订单行 ID；ERP/WMS 外部键另存并幂等 |
| Receivables | 回款计划、到账、核销、冲销 | 事实记录不可变；租户隔离 | 引用合同/订单/客户 ID；到账和核销以反向事实冲销，不覆盖原记录 |
| Invoicing | 开票申请、发票、红冲 | 申请和票据各自 ID；租户隔离 | 引用订单/回款/客户 ID；正式发票号作为业务键，红冲引用原发票 ID |
| Aftersales | 售后工单、退货、退款、复购关联 | 聚合和动作事实 ID；租户隔离 | 引用客户/订单/交付 ID；退款事实由财务域主责，售后只保存协同状态 |

全局规则：金额 `NUMERIC(18,2)`，币种 ISO 4217，时间 UTC `TIMESTAMPTZ`；跨上下文只保存 ID，不建实体级 FK；跨域一致性依赖应用校验、Outbox 事件和对账。除已实现的目录表外，禁止提前创建 `crm_opportunity`、`crm_quote`、`crm_contract`、`crm_order`、`crm_delivery`、`crm_payment`、`crm_invoice` 或 `crm_ticket` 等正式表。
