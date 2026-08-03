<div align="center">

# AiCRM · AI 获客销售系统

> 面向渠道获客场景的 AI 销售中台：多渠道线索接入、智能会话接待、客户运营一体化。

Java 17 · Spring Boot 3 · PostgreSQL 16 · Redis · RabbitMQ · 多租户 · OpenAPI 3.0 接口文档

</div>

---

## 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [模块结构](#模块结构)
- [快速开始](#快速开始)
- [接口文档](#接口文档)
- [本地开发环境](#本地开发环境)
- [目录结构](#目录结构)
- [文档索引](#文档索引)
- [环境变量](#环境变量)
- [License](#license)

---

## 项目简介

AiCRM 是一套 **AI 驱动的获客销售管理系统**，覆盖「渠道接入 → 线索捕获 → 智能接待 → 客户转化」全流程：

- 统一接入抖音 / 视频号 / 企业微信等渠道的评论、私信、表单事件；
- 基于 AI 的会话接待、意图识别、字段抽取与知识库问答；
- 线索自动分配、回收重分配、自动标签等自动化运营能力；
- 多租户隔离，内置用户 / 角色 / 权限 / 菜单体系，开箱即用。

项目采用 **Java + Python 双服务** 架构：Java 承担业务主链路（Spring Boot），Python 承担 AI 能力服务（FastAPI，可替换为任意 LLM Provider）。

## 功能特性

| 模块 | 核心能力 |
| --- | --- |
| 渠道接入 | 抖音 / 视频号 / 企业微信 Webhook 统一入口、幂等去重、渠道账号与活码管理、令牌自动刷新 |
| 客户线索 | 线索生成、跟进记录、自动分配 / 回收重分配、自动标签规则、线索状态流转 |
| 会话与 AI | 会话 / 消息管理、AI 智能回复、意图分析、字段抽取、知识库问答、自动转人工 |
| 企微侧边栏 | 客户画像、会话摘要、回复建议（企微 H5 场景） |
| 知识中台 | 产品库、竞品库、话术库、文档管理、向量检索 |
| 系统管理 | 租户 / 套餐、用户 / 角色 / 权限、菜单 / 字典 / 参数、操作日志 / 登录日志、定时任务、文件管理 |
| 数据统计 | 渠道转化、销售工作量、会话趋势等看板 |

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 3.2.5 · Spring MVC · Spring AOP |
| 持久层 | MyBatis-Plus 3.5 · PostgreSQL 16（JSONB / pgvector） |
| 缓存 | Redis（Redisson） |
| 消息队列 | RabbitMQ（Topic + 死信队列，不可用自动降级） |
| 定时任务 | Quartz |
| 认证授权 | JWT（jjwt）+ 自定义 `@RequirePermission` 注解 + 操作日志 AOP |
| 接口文档 | SpringDoc OpenAPI 2.3 + Knife4j 4.5（Knife4j UI + 原生 Swagger UI 双支持） |
| AI 服务 | Python FastAPI（LLM Provider：mock / openai / dashscope） |
| 构建 | Maven 多模块 |

## 模块结构

```
java/
├── aicrm-common      通用模块：Result/ResultCode、异常、JWT、工具类、租户上下文
├── aicrm-dao         数据访问：Entity / Mapper（MyBatis-Plus）
├── aicrm-service     业务服务：渠道 / 线索 / 会话 / AI / 知识库 / 统计等
├── aicrm-api         HTTP 接口层：Controller、OpenAPI 文档配置、Mock 注解
├── aicrm-admin-boot  启动模块：应用入口、全局配置（拦截器/消息队列/文件存储）
└── aicrm-generator   代码生成器（MyBatis-Plus FastAutoGenerator + FreeMarker 模板）

python/               AI 能力服务（FastAPI）：问答 / 意图 / 抽取 / 摘要
```

## 快速开始

### 环境要求

| 依赖 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 17+ | 必装 |
| Maven | 3.8+ | 必装 |
| PostgreSQL | 16 | 必装，业务主库（建议使用本目录 `docker-compose.yml` 或本机服务） |
| Redis | 6+ | 必装（本地 6379 默认） |
| RabbitMQ | 3.x | 可选；未启动时系统自动降级（消息通知仅记日志） |
| Python | 3.10+ | 可选；仅运行 AI 能力服务时需要 |

### 第一步：初始化数据库

方式一（Docker 一键）：

```bash
docker compose up -d
```

方式二（本机已有 PostgreSQL）：

```bash
# 创建数据库与用户
psql -U postgres -c "CREATE USER aicrm WITH PASSWORD 'aicrm_dev_123';"
psql -U postgres -c "CREATE DATABASE aicrm OWNER aicrm;"
# 执行初始化脚本（建表 + 种子数据）
psql -U aicrm -d aicrm -f java/aicrm-admin-boot/src/main/resources/db/init.sql
```

### 第二步：启动后端（Java）

```bash
cd java
mvn -DskipTests package
java -jar aicrm-admin-boot/target/aicrm.jar
```

默认使用 `dev` profile（`spring.profiles.active=dev`），监听 `http://localhost:8080`。

### 第三步（可选）：启动 AI 能力服务（Python）

```bash
cd python
pip install -r requirements.txt    # 若存在
cp .env.example .env               # 按需配置 LLM Provider（默认 mock 模式，无需密钥）
uvicorn app.main:app --host 0.0.0.0 --port 8100
```

> AI 服务未启动不影响业务接口调试：AI 相关调用会返回降级/模拟结果（可配置）。

### 验证启动

```bash
curl http://localhost:8080/actuator/health
# AI 服务：curl http://localhost:8100/health
```

## 接口文档

本地默认开启（`AICRM_DOC_ENABLED=true` 或未设置时；生产环境 `application-prod.yml` 强制关闭），支持双入口：

| 入口 | 地址 | 说明 |
| --- | --- | --- |
| Knife4j 增强 UI | http://localhost:8080/doc.html | 分组导航、全局参数、字段级 Mock、在线调试 |
| 原生 Swagger UI | http://localhost:8080/swagger-ui.html | 标准 Swagger UI，支持分组下拉与 Try it out |

补充说明：

- 原始 OpenAPI JSON：`http://localhost:8080/v3/api-docs`（可按分组：`/v3/api-docs/{分组名}`）；
- 全局请求头：多租户接口携带 `X-Tenant-Id`，鉴权接口携带 `Authorization: Bearer <token>`；
- 离线交付包：`docs/offline-api-doc.html`（浏览器直接打开）、`docs/offline-api-doc.md`、`docs/openapi.json`（可导入 Apifox / JMeter / Postman）。

## 本地开发环境

- 中间件编排：`docker-compose.yml`（PostgreSQL 16，Redis 使用本机 6379）；
- 默认账号：由 `init.sql` 种子数据生成（详见脚本内注释）；
- 代码生成器：运行 `aicrm-generator` 模块的 `CodeGenerator` 按表生成 Entity / Mapper / Service / Controller；
- 接口规范：详见 `docs/api-annotation-guide.md`。

## 目录结构

```
.
├── docs/                     # 项目文档（接口规范/错误码/Mock 链路/压测清单/维护规范等）
├── java/                     # Java 后端（Maven 多模块）
│   ├── aicrm-admin-boot/     # 启动模块（含 application-*.yml、db/init.sql）
│   └── ...（详见"模块结构"）
├── python/                   # AI 能力服务（FastAPI）
├── docker-compose.yml        # 本地中间件编排
└── README.md
```

## 文档索引

| 文档 | 说明 |
| --- | --- |
| [接口注解开发规范](docs/api-annotation-guide.md) | @Tag/@Operation/@Schema/@ApiResponse 用法约定 |
| [接口文档维护规范](docs/api-doc-maintenance-guide.md) | 变更流程、评审检查项、离线文档重新生成命令 |
| [错误码对照表](docs/error-code-table.md) | 全量错误码与边界场景对照 |
| [Mock 链路说明](docs/mock-chain-guide.md) | 评论→私信→加企微→会话全流程 Mock 配置 |
| [压测接口清单](docs/performance-testing-list.md) | 13 项核心接口 + JMeter 导入建议 |
| [联调问题台账](docs/integration-issues.md) | 前后端联调问题记录 |
| [WebSocket 接入指南](docs/websocket-guide.md) | 消息实时推送接入说明 |
| [生产环境配置校验单](docs/production-config-check.md) | 生产环境文档关闭核查与网关拦截规则 |
| [客户对接确认单](docs/customer-onboarding-confirmation.md) | 标杆客户对接交付物清单 |

## 环境变量

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `AICRM_DOC_ENABLED` | `true` | 接口文档开关（生产环境务必为 `false` 或不设置） |
| `AICRM_JWT_SECRET` | 开发默认值 | JWT 签名密钥（生产必须通过环境变量注入，≥32 字节） |
| `MINIO_ACCESS_KEY` / `MINIO_SECRET_KEY` | `minioadmin` | 对象存储访问凭证（`aicrm.storage.type=minio` 时） |

数据库 / Redis / RabbitMQ 连接信息在 `java/aicrm-admin-boot/src/main/resources/application-dev.yml` 中配置（本地默认值 `aicrm / aicrm_dev_123`）。

## License

本项目当前为内部项目，许可证待定（LICENSE 文件缺失时按仓库约定执行）。
