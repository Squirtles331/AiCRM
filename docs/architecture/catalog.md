# 阶段 2A：产品与价格设计

状态：已进入实现；物理基线：Flyway `V10__catalog_foundation.sql`。本阶段只交付产品目录和价目表，不创建商机、报价或审批表。

| 聚合 | 表 | 状态 | 关键规则 |
|---|---|---|---|
| 产品分类 | `crm_product_category` | `ACTIVE`、`DISABLED` | 分类编码租户内唯一，上级分类必须同租户。 |
| 产品 | `crm_product` | `ACTIVE`、`DISABLED` | `product_no` 与 `sku` 租户内唯一，不可销售产品不得进入价格项。 |
| 价目表 | `crm_price_list` | `DRAFT`、`ACTIVE`、`EXPIRED`、`DISABLED` | 仅草稿可维护价格项；发布需至少一个有效价格项且使用乐观锁。 |
| 价格项 | `crm_price_item` | `ACTIVE`、`DISABLED` | 同价目表内产品唯一；最低价不得高于目录价，税率范围为 0 至 1。 |

所有表使用应用生成 `BIGINT`、`tenant_id` 共享表隔离、UTC `TIMESTAMPTZ`、`NUMERIC(18,2)` 金额及软删除审计字段。产品目录是报价的可引用主数据；后续报价必须固化产品、价格、税率和币种快照，不能回写历史交易。

| API | 权限 | 规则 |
|---|---|---|
| `GET/POST /api/v1/catalog/categories` | `catalog:read` / `catalog:write` | 创建需要 `Idempotency-Key`。 |
| `GET/POST /api/v1/catalog/products` | `catalog:read` / `catalog:write` | 创建需要 `Idempotency-Key`。 |
| `POST /api/v1/catalog/price-lists` | `catalog:write` | 创建草稿，须 `Idempotency-Key`。 |
| `GET /api/v1/catalog/price-lists/{id}` | `catalog:read` | 查询价目表。 |
| `GET/POST /api/v1/catalog/price-lists/{id}/items` | `catalog:read` / `catalog:write` | 仅草稿可创建价格项。 |
| `POST /api/v1/catalog/price-lists/{id}/actions/publish` | `catalog:publish` | 请求带 `version`，发布后不可继续加项。 |

写操作与审计、Outbox 同事务写入，事件包括 `CategoryCreated`、`ProductCreated`、`PriceListCreated`、`PriceItemCreated`、`PriceListPublished`。商机、报价版本、折扣规则与审批实例仍需通过下一条 Flyway 迁移建模，禁止通过扩展字段提前落库。
