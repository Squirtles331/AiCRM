<div align="center">

# AiCRM

**AI 驱动的多渠道获客销售中台**

打通「渠道获客 → AI 接待 → 线索转化」全链路，让销售团队的每一次触达都有迹可循、有 AI 可依。

[![Release](https://img.shields.io/badge/Release-v0.1.0-blue.svg)](https://github.com/your-org/aicrm/releases)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-ff6600.svg)](https://www.rabbitmq.com/)
[![Python](https://img.shields.io/badge/Python-3.11-3776AB.svg)](https://www.python.org/)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](#开源协议)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](#参与贡献)

</div>

---

## 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [系统架构](#系统架构)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [接口文档](#接口文档)
- [项目结构](#项目结构)
- [文档索引](#文档索引)
- [参与贡献](#参与贡献)
- [开源协议](#开源协议)

## 项目简介

AiCRM 是一套 **AI 驱动的多渠道获客销售中台**，面向需要从公域渠道规模化获客并持续转化客户的销售团队：

- **多渠道统一接入**：抖音、视频号、企业微信三大渠道的评论、私信、表单事件统一 Webhook 接入，幂等去重、令牌自动刷新；
- **AI 贯穿销售全流程**：意向识别、RAG 知识问答、字段抽取、对话摘要、自动转人工，AI 能力以独立 Python 服务提供，可替换任意 LLM Provider；
- **自动化运营引擎**：线索自动分配与回收重分配、自动标签规则、定时任务（渠道同步 / 令牌刷新 / 评论拉取）全部内置；
- **多租户即开即用**：租户 / 套餐 / 用户 / 角色 / 菜单 / 权限完整体系，内置操作与登录审计、文件管理、代码生成器。

> 目标用户：SaaS 服务商、渠道型销售团队、需要将"公域流量"沉淀为"私域客户"的成长型公司。

## 功能特性

| 模块 | 核心能力 |
| --- | --- |
| 渠道接入 | 抖音 / 视频号 / 企微 Webhook 统一入口 · 幂等去重 · 渠道账号与活码管理 · 令牌自动刷新 · 消息网关（敏感词 / 风控 / 限流） |
| 客户与线索 | 客户主数据 · 多渠道身份归一与自动合并 · 标签体系与自动打标 · 线索分配引擎（产品线 / 地域 / 轮询）· 跟进记录 |
| 会话与 AI | 会话生命周期 · 全渠道消息落库 · AI 智能回复（RAG 知识问答）· 转人工评估与交接包 · 企微侧边栏 |
| 知识中台 | 产品库 · 竞品库 · 话术库 · 文档管理 · 知识向量同步（MQ 驱动） |
| 系统管理 | 租户 / 套餐 · 用户 / 角色 / 权限 · 菜单 / 字典 / 参数 · 操作日志 / 登录日志 · 定时任务 · 文件存储 |
| 数据统计 | 渠道转化漏斗 · 销售工作量 · 会话趋势 · AI 解决率看板 |
| 工程化 | OpenAPI 3.0 文档（Knife4j + Swagger UI 双入口）· 离线文档包 · 代码生成器 · 环境隔离开关 |

## 系统架构

```
                         ┌──────────────────────────────────────────────┐
                         │                   客户端 / 运营端              │
                         │         Web 管理端 · 企微侧边栏 H5             │
                         └──────────────────────┬───────────────────────┘
                                                │ HTTPS / JWT
                         ┌──────────────────────▼───────────────────────┐
  抖音 / 视频号 / 企微      │              Spring Boot 核心服务             │
  （Webhook 回调） ──────► │  ┌────────────┐  ┌────────────┐  ┌─────────┐ │
                         │  │  渠道接入    │  │  客户线索    │  │ 会话与AI │ │
                         │  │  Webhook    │  │ 身份归一    │  │ 消息/转接 │ │
                         │  └─────┬──────┘  └─────┬──────┘  └────┬────┘ │
                         │        │               │               │      │
                         │  ┌─────▼───────────────▼───────────────▼────┐ │
                         │  │            知识中台 · 数据统计             │ │
                         │  └─────┬───────────────────────────┬────────┘ │
                         └────────┼───────────────────────────┼──────────┘
                                  │ HTTP (FastAPI)            │ 异步事件
                         ┌────────▼─────────┐         ┌───────▼─────────┐
                         │  Python AI 服务   │         │  RabbitMQ       │
                         │  意向/问答/抽取/摘要│         │  Topic + DLQ    │
                         └────────┬─────────┘         └───────┬─────────┘
                                  │                           │
                     ┌────────────▼───────────┐   ┌───────────▼───────────┐
                     │      PostgreSQL 16     │   │        Redis          │
                     │   业务库 + JSONB + 向量 │   │  缓存 / 限流 / 分布式锁 │
                     └────────────────────────┘   └───────────────────────┘
```

关键设计：

- **Java + Python 双服务**：Java 承担业务主链路（事务、权限、租户隔离），Python 专注 AI 能力（可独立扩展 / 替换 LLM Provider）；
- **统一事件入口**：三大渠道 Webhook 统一收敛至 `POST /api/channel/events`，幂等去重后落库并发布异步事件；
- **消息降级**：RabbitMQ 不可用时自动降级为日志记录，业务主链路不受影响；
- **多租户隔离**：MyBatis-Plus 租户插件 + JWT（roles / perms）+ `@RequirePermission` 按钮级权限。

## 技术栈

| 分类 | 技术选型 |
| --- | --- |
| 后端框架 | Spring Boot 3.2.5 · Spring MVC · Spring AOP |
| 持久层 | MyBatis-Plus 3.5 · PostgreSQL 16（JSONB / 向量） |
| 缓存 | Redis 7（Redisson） |
| 消息队列 | RabbitMQ 3.x（Topic + 死信队列，自动降级） |
| 定时任务 | Quartz |
| 认证授权 | JWT（jjwt）+ `@RequirePermission` + `@OperLog` AOP 审计 |
| 接口文档 | SpringDoc OpenAPI 2.3 + Knife4j 4.5（双 UI 支持） |
| AI 服务 | Python 3.11 · FastAPI（LLM Provider：mock / openai / dashscope） |
| 构建部署 | Maven 多模块 · Docker Compose · （可选）Spring Boot Actuator |

## 快速开始

### 环境要求

| 依赖 | 版本 | 必须 | 说明 |
| --- | --- | --- | --- |
| JDK | 17+ | 是 | 后端运行环境 |
| Maven | 3.8+ | 是 | 构建工具 |
| PostgreSQL | 16 | 是 | 业务主库 |
| Redis | 6+ | 是 | 缓存 / 限流（本地 6379） |
| RabbitMQ | 3.x | 否 | 未启动时自动降级 |
| Python | 3.11+ | 否 | 仅运行 AI 能力服务时需要 |

### 第一步：启动基础设施

```bash
docker compose up -d
```

> 默认编排 PostgreSQL 16（用户 `aicrm` / 密码 `aicrm_dev_123` / 库 `aicrm`），Redis 复用本机 6379。
> 若使用本机已有 PostgreSQL，请跳过本步并按提示手动建库建用户（见下方备注）。

### 第二步：初始化数据库

Docker 方式首次启动会自动执行 `db/init.sql`；本机 PostgreSQL 手动初始化：

```bash
psql -U postgres -c "CREATE USER aicrm WITH PASSWORD 'aicrm_dev_123';"
psql -U postgres -c "CREATE DATABASE aicrm OWNER aicrm;"
psql -U aicrm -d aicrm -f java/aicrm-admin-boot/src/main/resources/db/init.sql
```

### 第三步：启动后端

```bash
cd java
mvn -DskipTests package
java -jar aicrm-admin-boot/target/aicrm.jar
```

启动成功后在浏览器访问 **http://localhost:8080/doc.html** 即可看到完整接口文档。

### 第四步（可选）：启动 AI 能力服务

```bash
cd python
cp .env.example .env          # 默认 mock 模式，无需任何 API Key
uv sync                       # 安装依赖（清华镜像加速）；或使用 pip install -e .
uv run uvicorn app.main:app --host 0.0.0.0 --port 8100
```

> AI 服务未启动时不影响业务接口调试，AI 相关调用自动降级返回模拟结果。

### 验证

```bash
curl http://localhost:8080/actuator/health        # Java 服务
curl http://localhost:8100/health                 # AI 服务（若已启动）
```

## 接口文档

本地 `dev` 环境默认开启双入口（生产环境由 `application-prod.yml` 强制关闭）：

| 入口 | 地址 | 说明 |
| --- | --- | --- |
| Knife4j 增强 UI | http://localhost:8080/doc.html | 分组导航 · 全局参数 · 字段级 Mock · 在线调试 |
| 原生 Swagger UI | http://localhost:8080/swagger-ui.html | 标准 Swagger UI · 分组下拉 · Try it out |
| OpenAPI JSON | http://localhost:8080/v3/api-docs | 标准 OpenAPI 3.0，可按分组 `/v3/api-docs/{分组名}` |

快速体验一个接口：

```bash
# 登录获取 JWT
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

离线交付包（无需启动项目，浏览器直接打开）：

| 文件 | 说明 |
| --- | --- |
| `docs/offline-api-doc.html` | 单文件离线 HTML 接口文档（ReDoc，完全自包含） |
| `docs/offline-api-doc.md` | Markdown 版接口文档 |
| `docs/openapi.json` | 标准 OpenAPI 3.0 定义，可直接导入 Apifox / JMeter / Postman |

## 项目结构

```
.
├── docs/                     # 项目文档（规范 / 错误码 / Mock / 压测 / 维护等）
├── java/                     # Java 后端（Maven 多模块）
│   ├── aicrm-common          #   通用模块：Result / ResultCode / JWT / 租户上下文
│   ├── aicrm-dao             #   数据访问：Entity / Mapper（MyBatis-Plus）
│   ├── aicrm-service         #   业务服务：渠道 / 线索 / 会话 / AI / 知识库 / 统计
│   ├── aicrm-api             #   HTTP 接口层：Controller + OpenAPI 配置 + Mock 注解
│   ├── aicrm-admin-boot      #   启动模块：应用入口 / 全局配置 / db/init.sql
│   └── aicrm-generator       #   代码生成器（FastAutoGenerator + FreeMarker 模板）
├── python/                   # AI 能力服务（FastAPI）
│   └── app/                  #   main.py 入口 / api / core / schemas / services
├── docker-compose.yml        # 本地中间件编排（PostgreSQL）
└── README.md
```

## 文档索引

| 文档 | 说明 |
| --- | --- |
| [本地接口文档配置说明](docs/api-doc-setup-guide.md) | 双入口地址 / 开关 / 常见问题 |
| [接口注解开发规范](docs/api-annotation-guide.md) | `@Tag / @Operation / @Schema / @ApiResponse` 用法约定 |
| [接口文档迭代维护规范](docs/api-doc-maintenance-guide.md) | 变更流程 / 评审检查项 / 离线文档重新生成 |
| [错误码对照表](docs/error-code-table.md) | 全量错误码与边界场景对照 |
| [Mock 链路说明](docs/mock-chain-guide.md) | 评论 → 私信 → 加企微 → 会话全流程 Mock |
| [压测接口清单](docs/performance-testing-list.md) | 核心接口 + JMeter 导入建议 |
| [生产环境配置校验单](docs/production-config-check.md) | 生产文档关闭核查 / 网关拦截规则 |
| [WebSocket 接入指南](docs/websocket-guide.md) | 实时消息推送接入说明 |
| [客户对接确认单](docs/customer-onboarding-confirmation.md) | 客户侧对接交付物清单 |

## 参与贡献

我们欢迎任何形式的贡献：功能建议、Bug 报告、文档完善、代码提交。

建议流程：

1. Fork 本仓库并创建特性分支：`git checkout -b feature/xxx`
2. 提交变更并确保通过 `mvn -DskipTests package` 构建
3. 遵循《[接口文档迭代维护规范](docs/api-doc-maintenance-guide.md)》中的评审检查项
4. 发起 Pull Request，描述变更内容与验证结果

## 开源协议

本项目基于 **Apache License 2.0** 开源，完整协议文本见 [LICENSE](./LICENSE)。

**免责声明**：AiCRM 仅提供获客与客户管理能力，不提供任何绕过平台规则的功能；接入各渠道时请遵守对应开放平台的使用规范与当地法律法规。

---

<div align="center">

**AiCRM** · AI 获客销售中台

如果这个项目对你有帮助，欢迎 Star 支持。

</div>
