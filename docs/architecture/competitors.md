# CRM 竞争信息

V26 提供 CRM 内部竞争对手台账，记录名称、定位、优势、短板和负责人，帮助销售在商机推进时维护可复用的竞争信息。它不接入外部市场情报、爬虫、自动推荐或生成式分析。

竞争对手状态为 `ACTIVE/ARCHIVED`。创建使用 `Idempotency-Key`，名称在租户内的活跃记录唯一；归档要求 `competitor:manage` 和匹配 `version`。查询支持 `competitor:read` 或管理权限。每次创建和归档都在事务内写审计及 `CompetitorCreated/CompetitorArchived` Outbox 事件，所有数据按租户隔离。
