# Changelog

本项目的所有重要变更都会记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)（`主版本.次版本.修订版`）。

## [Unreleased]

### Added

- （暂无，欢迎通过 Issue / PR 提出）

## [0.1.0] - 2026-08-03

首个可运行里程碑版本：**AI 驱动的多渠道获客销售中台 MVP**。

### Added

- **多模块工程骨架**：`aicrm-common` / `aicrm-dao` / `aicrm-service` / `aicrm-api` / `aicrm-admin-boot` / `aicrm-generator`
- **系统基础**：租户与套餐、用户 / 角色 / 菜单 / 按钮级权限（`@RequirePermission`）、字典 / 系统参数、文件存储（本地 / MinIO）、操作与登录日志（`@OperLog` AOP 审计）、定时任务（Quartz）、轻量代码生成器
- **渠道接入**：抖音 / 视频号 / 企微账号管理与 token 自动刷新、统一 Webhook 事件入口（幂等去重）、消息网关（敏感词拦截 / 风控 / 限流）、渠道活码与扫码归因、异步链路（MQ Topic + 死信队列 + 失败重试）
- **客户与线索**：客户主数据、多渠道身份归一与自动合并、标签体系与自动打标、线索分配引擎（产品线 / 地域 / 轮询）、跟进记录
- **会话与 AI**：会话生命周期、全渠道消息落库、AI 智能回复（RAG 知识问答）、转人工评估与交接包、企微侧边栏
- **知识中台与统计**：产品库 / 竞品库 / 话术库、文档管理、知识向量同步（MQ 驱动）、数据看板（转化漏斗 / 工作量 / 会话趋势 / AI 解决率）
- **Python AI 能力服务**：FastAPI 提供意向分类 / RAG 问答 / 实体抽取 / 对话摘要，LLM Provider 可插拔（mock / openai / dashscope）
- **API 文档体系**：SpringDoc OpenAPI 2.3 + Knife4j 4.5 双 UI 入口、9 个接口分组、全局 `X-Tenant-Id` 与 Bearer JWT、离线文档包（HTML / Markdown / OpenAPI JSON）
- **开源交付**：README、Apache-2.0 LICENSE、贡献指南与社区治理文件

### Changed

- `springdoc-openapi` 由 2.5.0 调整至 **2.3.0**，对齐 Knife4j 4.5.0 官方配套版本
- 接口文档 `@ApiResponse` 响应码标准化：29 处业务码改为合法 HTTP 状态码，业务码移入描述
- 生产环境 `application-prod.yml` 强制关闭全部文档入口（springdoc + knife4j）

### Fixed

- 修复 Knife4j 4.5.0 与 springdoc 2.5.0 不兼容导致的 `/v3/api-docs` 返回 500（`NoSuchMethodError: getGroupConfigs()`）
- 修复 OpenAPI JSON 导出中文双重编码乱码（改用 `curl -o` 保存原始 UTF-8 字节）
- 修复离线文档 HTML 外部 CDN 资源引用，改为完全自包含单文件

### Security

- 生产环境无法访问 `/doc.html`、`/v3/api-docs`、`/swagger-ui*` 等任何文档地址
- 日志落库前对敏感参数（密码、token 等）进行脱敏处理

[Unreleased]: https://github.com/your-org/aicrm/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/your-org/aicrm/releases/tag/v0.1.0
