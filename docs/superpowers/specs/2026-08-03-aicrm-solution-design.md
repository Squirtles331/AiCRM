# AI获客销售系统（AiCRM）初步解决方案设计文档

- 版本：v1.0
- 日期：2026-08-03
- 状态：已确认（待评审）

---

## 1. 项目概述

### 1.1 背景

B2B 高客单行业（工业品/装备制造、仪器仪表、材料化工、工程服务、ToB 软件/企业服务）普遍存在：线索获取分散且贵、销售跟进不及时、资料分发不标准、FAQ 与选型答疑大量消耗人力、报价前信息收集不完整、管理层过程不可见等共性痛点。

本项目拟构建一套「AI 获客销售系统」，将短视频/社媒互动与留资自动转化为可跟进的线索，并用 AI 对话 + SOP 工作流完成"资料分发、FAQ 答疑、意向分层、转人工交接"，显著提升有效线索率与报价转化率。

### 1.2 目标

- 三渠道获客（抖音、微信视频号、TikTok）统一承接与归因
- 双阵地成交承接（企业微信、WhatsApp）
- AI 深度闭环：入池 → 承接 → 对话 → 意向分层 → 转人工 → 报价前信息收集 → 复盘
- 按坐席/账号订阅模式可售卖、可控毛利、可复制交付

### 1.3 文档范围

本文档固化以下已确认决策：目标客户、产品定位、产品组合、版本路线、商业模式、市场与技术评估、客户需求与核心功能、解决方案、软件架构（Java + Python 双栈）、数据库设计、项目进度与里程碑。

---

## 2. 市场与竞争调研结论

### 2.1 行业趋势（国内 + 海外双栈）

**国内栈（抖音/视频号 + 企微私域）**
- 从"线索采集"转向"内容-互动-成交一体化"；评论/私信/直播间互动是比纯表单更贴近成交的线索入口
- AI 从"写文案"进入"半自动经营"（人机协同 SOP 产品化）
- 平台引流规则、私信频控、敏感词、夸大宣传等风控加码，账号安全与内容合规成为运营成本
- 企微成为"成交操作系统"，但需把"人设+话术+节奏+质检"产品化才能规模化

**出海栈（Email/LinkedIn/WhatsApp/社媒）**
- 从"买线索"转向"可衡量的增长系统"，重视全链路归因、CAC/LTV、渠道 ROI
- AI 外联规模化，但"可持续触达"（交付率、封号、追踪受限）是成败关键
- GDPR/CPRA 等合规与平台反滥用加强，"抓取/强自动化"风险上升

**共同趋势**：行业共识从"线索量"转向"线索质量、可持续触达、合规、可度量归因"；越自动化越需要风控与人工兜底。

### 2.2 客户需求（成熟市场验证）

- 从"线索量"到"线索质量 + 可转化率"：线索评分、有效联系方式、意向识别、去重合并、无效拦截
- 强调"销售过程可控"：跟进合规率、触达时效、话术质量、成单周期、团队产能
- 私域精细化：标签体系、社群分层、触达频率控制、直播间到企微承接漏斗

### 2.3 竞争对手版图

**国内**
- 企微 SCRM/私域承接：微盛、探马SCRM、尘锋SCRM、微伴助手（强项：客户沉淀、标签画像、SOP触达、质检）
- 抖音获客与线索闭环：巨量引擎线索组件/飞鱼线索（线索拉取 + 有效线索回传学习）
- 外呼与联络中心：阿里云AICCS、腾讯企点（通话转写、质检、结构化意向）

**海外**
- 对话获客（TikTok/IG/WhatsApp）：ManyChat（对话漏斗）、respond.io/SleekFlow/Kommo（全渠道会话协同 + 路由 + 转人工）
- B2B 外联与 AI SDR：Outreach/Salesloft（企业级）、Apollo（数据+触达一体）、Instantly/Smartlead（邮件交付率）、Expandi/Dripify（LinkedIn 自动化）

**竞争结论**：机会不在"再造一个 SCRM"，而在 ① 三渠道线索统一承接与归因；② 意向结构化 + 报价前信息收集 + 转人工交接的深闭环；③ 产品/竞品知识资产运营（battle card、选型规则、FAQ 可迭代）。

### 2.4 技术方向

- 统一线索池 + 身份图谱 + 工作流编排 + 知识库问答（RAG）+ 风控审计的中台思路
- 渠道层插件化适配（国内：企微/巨量线索；海外：WA/Email/TikTok/IG）
- 高风险动作（自动加微信/自动通过好友/大规模私信）必须以"官方能力边界 + 频控 + 合规留痕 + 人工确认"方式实现，否则稳定性与合规不可控

---

## 3. 产品定义

### 3.1 目标客户

**核心画像**：B2B 高客单、顾问式销售、决策链较长、强资料/选型/报价需求的企业；销售坐席 3-10 人。

**客户类型优先级**
- P1（最优先）：工业品/装备/仪器、材料化工、工程服务、企业IT/ToB软件
- P2（第二阶段）：医疗器械/实验室设备等"强合规 + 强选型"行业
- P3（后续扩展）：招商加盟/本地生活（量大但玩法不同，容易带偏产品，谨慎进入）

**购买与交付特征**：有明确销售团队（3-50 人），愿意为"有效线索率 + 报价转化率 + 管理可控"付费，能接受 SOP 落地与知识库建设。

### 3.2 产品定位

**一句话定位**：面向 B2B 高客单行业的「三渠道获客统一承接 + AI 销售协同闭环系统」——把抖音/视频号/TikTok 的互动与留资统一进线索池，通过 AI 对话与 SOP 让线索快速完成"资料→问答→意向分层→转人工→报价前信息收集"。

**核心差异化（护城河）**
- 跨渠道统一线索身份：同一人来自不同渠道的线索合并、去重、打通旅程
- 意向结构化：把"要报价/要样本/要选型"变成可驱动流程的数据，而非聊天记录
- 人机协同可控：AI 自动化在风控、低置信度、敏感行业场景下自动降级并交接人工
- 内容资产中台：产品/竞品/选型规则/FAQ 可运营、可迭代、可审计

**不做/谨慎做的边界**
- 不以"灰产式全自动加人/全自动私信轰炸"为卖点；以官方能力 + 频控 + 合规留痕 + 人机协同为底线
- 不和通用 CRM 正面硬刚（回款/财务/复杂项目管理），优先做"线索到商机前半段"强闭环，再通过集成对接 CRM
- "自动获取客户公司人员架构及联系方式"仅做公开数据与客户授权数据补全，默认关闭，作为可选增值包

### 3.3 产品组合

**1 个平台 + 3 个套件**

1. **平台底座：增长与线索中台（必须共用）**
   - 统一线索池：多渠道线索落库、去重合并、生命周期、线索评分
   - 统一客户身份：手机号/邮箱/企业域名/社媒ID/企微ID/WhatsApp 身份图谱
   - 统一归因：渠道/内容/投放活动/触点 → 线索 → 成交最小闭环

2. **套件A：三渠道接入套件（抖音/视频号/TikTok）**
   - 三渠道均先做"最小可用接入"：事件采集（评论/私信/表单/点击咨询）→ 线索落库 → 基础路由
   - 渠道差异能力以插件方式做，逐个渠道补深，避免互相拖累

3. **套件B：AI 销售协同套件（深度闭环核心）**
   - 加微/进私域后的自动化：资料包发送、FAQ 问答、意向识别（报价/样本/选型）
   - 报价前信息收集：自动追问缺失字段（应用场景、数量、预算、交期、型号等）
   - 转人工与协作：一键接管 + 上下文摘要 + 推荐回复 + 下一步动作建议

4. **套件C：运营与质检套件（规模化与可控）**
   - 对话质检：敏感承诺/违规引流/不合规表述提醒与抽检
   - 团队产能与漏斗：响应时效、有效对话率、意向分层准确率、报价转化率
   - 内容与话术运营：话术库/FAQ库/竞品对比卡（battle card）/优秀对话样本

> 可选加购（第二阶段）：外呼与联络中心（通话转写、质检、结构化结果）、CRM 深度集成（商机/合同/回款）。

### 3.4 版本路线

**V1 MVP（跑通"线索→加微→资料→分层→转人工"闭环）**
- 三渠道最小接入：线索/互动事件统一入池、基础归因
- 双阵地承接基础能力：企微欢迎语/资料发送基础链路；WhatsApp 官方通道架构预留
- AI 可控问答（带引用、可回溯）；意向三分类（报价/样本/选型）
- 转人工交接包；最小看板（响应时效、有效对话率、意向分布）

**V2 标准版（形成可复制的销售产线）**
- 工作流编排：按意向/来源/行业自动触发 SOP（资料→追问→预约→报价准备）
- 产品/竞品中台：产品库、资料包、选型规则、竞品对比与异议处理
- 质检与合规：敏感词、频控策略、审计日志、知识库更新闭环
- 三渠道增强：每个渠道补一个最关键的深能力
- 双阵地统一客户视图（企微 + WhatsApp 身份合并落地）

**V3 企业版（大客户决策点）**
- 权限/审计/数据分级/脱敏/导出删除/行业合规模板
- 生态集成：CRM、广告平台、数据仓库、联络中心
- Agent 化（谨慎）：强风控下的"半自动 SDR"（建议与执行分离、可回滚、可审计）

### 3.5 商业模式（按坐席/账号订阅）

**坐席定义**
- 销售坐席：使用线索池、对话工作台、资料发送、AI建议、转人工处理、任务跟进
- 主管坐席：质检、看板、分配策略、SOP 配置、话术/知识审核
- 管理员账号：免费或少量赠送（配置/权限，不占用销售价值）

**三档套餐**
- Starter（试点/小团队）：三渠道最小接入 + FAQ 问答/摘要/意向三分类（有额度）+ 基础看板
- Pro（主推款）：工作流编排 + 产品/竞品库 + 质检与风控基础（频控、敏感词、审计）
- Enterprise（大客户）：权限审计增强、数据分级、合规留痕、私有化选项 + 深度集成 + 行业包

**成本控制机制**
- AI 能力默认"额度制 + 超额降级"（Fair Use），超出可购买 AI 包或降级为"建议生成/人工确认"模式
- 高风险动作默认保守：默认不开或要求主管审批开启
- 渠道适配插件化：深能力作为独立交付增量包

---

## 4. 市场价值、技术可行性、资源投入、风险收益

### 4.1 市场价值

- 需求强度高：B2B 高客单行业共性痛点明确，可量化提升（有效线索率、响应时效、报价转化率）即可成交
- 付费意愿相对强：客户为"可衡量的商机增量/人效提升"付费，但看重可交付、稳定、合规
- 竞争结论：机会在"三渠道统一承接归因 + 意向结构化深闭环 + 内容资产运营"，而非再造 SCRM

### 4.2 技术可行性

| 可行性 | 能力 |
|---|---|
| 高可行（核心交付） | 多渠道线索落库、统一线索池、去重合并、评分路由；AI 自动回复/FAQ（RAG 带引用与置信度）；意向识别与工作流编排；转人工交接；产品/竞品库 |
| 中可行（取决于通道与合规实现） | 评论/私信监控与自动回复（严格按官方能力边界 + 频控 + 账号健康）；自动发送资料（企微/WhatsApp 可做，触发/频率/内容合规决定稳定性） |
| 高风险（从产品承诺剥离或做成可配置辅助） | 全自动加微信/自动通过好友（规模化触碰平台风控，落地为引导+半自动确认+频控+审计）；自动获取公司人员架构及联系方式（仅公开/授权数据，完整性合法性不确定） |

### 4.3 资源投入（最小可落地团队）

- 产品：1（主产品/闭环）+ 1（渠道&风控/合规）
- 后端（Java）：2-3（线索中台、工作流、权限审计、集成）
- 渠道/集成：2（抖音/视频号/TikTok 适配，做成插件）
- AI/数据（Python）：1-2（RAG、分类抽取、评测体系、提示词与安全策略）
- 前端：1-2（销售工作台、管理看板）
- 测试/交付：1（自动化测试、灰度、客户上线与 SOP）

**投入优先级（深度闭环优先）**
1. 统一线索池/身份合并/工作流/知识库问答/转人工/审计与频控
2. 三渠道"最小接入稳定化"（都能落库 + 可归因）
3. 逐个渠道补一个最关键的深能力，不平均用力

### 4.4 风险收益

**最大收益点**
- 人效：减少销售在"重复答疑/找资料/追问信息"上的时间
- 转化：响应更快、分层更准、报价前信息更完整 → 报价转化率提升
- 管控：SLA、质检、漏斗数据可见 → 管理者愿意买单

**最大风险点**
- 渠道风控与封禁：过度自动化（私信/加人/通过好友）带来不可控账号风险
- 合规与数据风险：个人信息采集、联系方式 enrichment、跨境数据流转
- AI 答错/乱答风险：报价、参数、合规承诺、竞品对比场景
- 三渠道并行产品稀释：渠道适配拖慢闭环，两头不到岸

**风险对冲策略**
- 高风险动作做成"可配置策略 + 默认保守 + 强审计 + 人工确认"
- POC 验收指标驱动成交与续费，避免卖"全自动"
- 渠道层插件化：一个渠道出问题不影响其他渠道与中台能力

---

## 5. 客户需求与核心功能

### 5.1 角色 × 场景 × 痛点

| 角色 | 需求 | 痛点 |
|---|---|---|
| 老板/负责人 | 获客可持续、线索成本下降、转化率提升、人效提升 | 线索来源分散无法归因；销售跟进不及时；过程不可见 |
| 销售 | 自动收线索、自动发资料、自动答常见问题、自动判断客户要什么（报价/样本/选型）、复杂问题一键转人工 | 重复答疑与找资料耗时间；信息收集不完整导致报价反复；新销售不会讲、易违规承诺 |
| 运营/投放 | 评论/私信线索不漏、有效线索回传、线索分层（冷热）、素材与渠道效果可复盘 | 线索质量难评估；渠道规则变化快；线索与成交断链 |
| 管理/合规 | 敏感词/违规承诺提醒、频控、审计日志、数据权限、可追溯 | 过度自动化封号/风控；个人信息合规风险；AI 乱答带来法律与口碑风险 |

### 5.2 核心功能清单（MVP 闭环 10 项，优先级从高到低）

1. **统一线索池（Lead Hub）**：三渠道线索/互动事件统一入库
2. **身份合并与去重**：手机号/邮箱/企业域名/社媒ID/企微/WhatsApp 映射成"一个人"
3. **线索生命周期与 SLA**：新线索→已响应→有效→待报价→已报价→商机… + 逾期提醒/回收
4. **资料包中心**：产品资料、案例、白皮书、选型表一键发送（企微/WhatsApp 都支持）
5. **FAQ/知识库问答（可控 RAG）**：基于产品资料与 FAQ 回答，支持引用与版本管理
6. **意向识别与结构化**：识别"要报价/要样本/要选型"，抽取关键字段（场景、数量、预算、交期、型号等）
7. **工作流编排（SOP 引擎）**：按来源/意向/行业触发"追问→发资料→预约→转人工"
8. **转人工协作（交接包）**：一键接管 + 对话摘要 + 推荐回复 + 缺失信息清单
9. **风控与质检基础**：频控、敏感词、违规承诺提示、审计日志
10. **核心看板与归因**：响应时效、有效对话率、意向分层准确率、线索→报价转化率、渠道贡献

---

## 6. 解决方案

### 6.1 端到端业务逻辑（闭环路径）

1. **获客**：抖音/视频号/TikTok 产生互动（评论/私信/表单/点击咨询）
2. **入池**：渠道事件进入统一线索池，生成线索并触发分配/SLA
3. **承接**：引导进入企微或 WhatsApp；进入后自动发送资料包与欢迎语
4. **对话**：AI 基于知识库自动答疑；持续做意向识别与字段抽取
5. **分层**：命中"报价/样本/选型"则进入对应 SOP（追问关键信息→预约→转人工）
6. **交接**：复杂/低置信度/高风险话题自动转人工，给销售"交接包"
7. **复盘**：线索结果回写线索池与渠道归因看板，用于优化投放/内容与 SOP

### 6.2 系统架构（Java + Python 双栈）

```
┌──────────────────────────────────────────────────────────┐
│  接入层（渠道适配 · 插件化）     【Java Spring Boot】       │
│  抖音/视频号/TikTok 适配器 · 企微/WhatsApp 适配器          │
│  统一事件模型 · Webhook网关 · 限流重试 · 字段映射           │
├──────────────────────────────────────────────────────────┤
│  核心业务服务层         【Java Spring Boot 单体+模块化】    │
│  线索服务 · 身份服务 · 会话服务 · 任务服务 · 工作流引擎      │
│  资料包服务 · 产品/竞品 · 质检 · 看板 · 多租户 · RBAC       │
│  风控引擎 · 审计日志 · 定时任务(Quartz)                     │
├──────────────────────────────────────────────────────────┤
│  AI 能力层              【Python FastAPI 独立服务】        │
│  LLM网关(国内/海外可切换) · RAG问答 · 意图分类              │
│  实体抽取 · 对话摘要 · 向量检索(pgvector) · 模型评测        │
├──────────────────────────────────────────────────────────┤
│  领域数据层                                              │
│  PostgreSQL(主库+pgvector) · Redis · 对象存储              │
├──────────────────────────────────────────────────────────┤
│  消息与事件总线                                          │
│  RabbitMQ(可靠事件) · Redis(限流/锁/缓存)                  │
├──────────────────────────────────────────────────────────┤
│  治理层                                                  │
│  网关 · 鉴权(JWT/OAuth) · 配置中心 · 监控(日志/指标/告警)   │
└──────────────────────────────────────────────────────────┘
```

### 6.3 技术选型

| 层 | 选型 | 理由 |
|---|---|---|
| 核心业务后端 | Java 17 + Spring Boot 3 | 事务/并发/生态成熟，坐席工作台与渠道网关稳定性 |
| AI 能力服务 | Python 3.11 + FastAPI | LLM/RAG/向量生态最快迭代（LangChain/LangGraph） |
| 服务通信 | HTTP (OpenAPI) + RabbitMQ 事件 | 同步查（AI结果）走 HTTP，异步触发（新线索→AI分析）走 MQ |
| 主库 | PostgreSQL 16 + pgvector | 关系数据 + JSONB + 向量一体，两栈原生支持 |
| 缓存/限流/锁 | Redis 7 | Java 侧 Redisson，Python 侧 redis-py |
| 队列 | RabbitMQ | 事件总线、工作流延时节点、重试与死信 |
| 定时任务 | Quartz (Java) | SLA 逾期、线索回收、日聚合报表 |
| AI 框架 | LangChain/LangGraph + Pydantic | 意图分类、RAG、抽取、摘要流程编排 |
| ORM | MyBatis-Plus (Java) / SQLAlchemy (Python) | 团队熟悉度优先 |
| 文件 | S3 兼容对象存储 | 资料包、附件、录音 |
| 前端 | Vue3 管理后台 | 坐席工作台/主管看板 |

### 6.4 Java / Python 职责边界与调用约定

```
Java（核心业务域）                 Python（AI能力域）
─────────────────                 ──────────────────
· 渠道接入/事件映射                · 意图分类(报价/样本/选型)
· 线索/客户/身份图谱               · 实体抽取(场景/数量/预算/交期)
· 会话/消息落库                    · RAG问答(知识检索+生成)
· 工作流引擎(状态机+延时)          · 对话摘要
· 风控/质检/审计                   · 自动回复建议生成
· 看板/归因/权限                   · 知识库向量化/检索
· 订阅计费/坐席管理                · 模型版本与评测
     ↓ HTTP同步(REST) / RabbitMQ异步(事件) ↑
```

- Java → Python：`POST /ai/intent`、`/ai/answer`、`/ai/extract`、`/ai/summary`（同步，带超时与降级：AI 不可用→降级为规则回复/转人工）
- Python → Java：消息回写走 RabbitMQ（如 `ai.analysis.completed` 事件），不反向直连业务表

### 6.5 部署形态

| 组件 | 部署 | 说明 |
|---|---|---|
| Java 业务服务 | 主容器（可水平扩展） | 无状态，Session 外置 Redis |
| Python AI 服务 | 独立容器（按推理量伸缩） | 与业务解耦，AI 故障不影响核心链路 |
| 前端 | Nginx + Vue3 静态 | |
| PostgreSQL / Redis / RabbitMQ | 托管或自建 | 建议云 RDS + 云 Redis，减少运维 |
| 对象存储 | 云 OSS/MinIO | |

---

## 7. 数据库设计

### 7.1 表清单与字段

#### 7.1.1 组织与权限

**tenant（租户/企业）** — 订阅模式根实体
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| name | VARCHAR(200) | 企业名称 |
| plan_code | VARCHAR(50) | starter/pro/enterprise |
| seat_count | INT | 坐席数上限 |
| ai_quota_month | BIGINT | 月度 AI 调用额度 |
| status | SMALLINT | 1 启用 / 0 停用 |
| created_at / updated_at | TIMESTAMPTZ | |

**user（坐席/员工）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK→tenant | |
| name / mobile / email | | |
| role_code | VARCHAR(50) | sales/supervisor/admin |
| status | SMALLINT | |
| last_active_at | TIMESTAMPTZ | |

#### 7.1.2 渠道与接入

**channel（渠道定义）**
`id, code(douyin/video_channel/tiktok/wecom/whatsapp), name, type, config_schema JSONB, status`

**channel_account（渠道账号）**
`id, tenant_id, channel_id FK, account_name, external_id, auth_config JSONB(加密), health_status SMALLINT, risk_level, created_at`

**channel_event（原始事件缓冲表）** — 三渠道统一入口
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| channel_account_id | BIGINT FK | |
| event_type | VARCHAR(50) | comment/dm/form/click/lead |
| external_event_id | VARCHAR(100) | 渠道事件唯一 ID（去重键） |
| external_user_id | VARCHAR(100) | 渠道侧用户 ID |
| raw_payload | JSONB | 原始事件数据 |
| mapped | SMALLINT | 是否已映射落线索 |
| created_at | TIMESTAMPTZ | |
| UNIQUE(tenant_id, channel_account_id, external_event_id) | | 幂等去重 |

#### 7.1.3 线索与客户（核心域）

**lead（线索）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| customer_id | BIGINT FK→customer（可空） | 关联公司 |
| contact_id | BIGINT FK→contact（可空） | 关联联系人 |
| source_channel_id | BIGINT FK | 来源渠道账号 |
| source_content_id | VARCHAR(100) | 来源内容/视频/广告 ID |
| source_type | VARCHAR(50) | comment/dm/form/… |
| intent | VARCHAR(50) | quote/sample/selection/other |
| status | VARCHAR(50) | new/assigned/contacting/effective/quoted/opportunity/lost |
| score | INT | 线索评分 0-100 |
| owner_id | BIGINT FK→user | 归属坐席 |
| sla_deadline | TIMESTAMPTZ | 响应 SLA 截止 |
| extra | JSONB | 抽取关键字段（场景/数量/预算/交期） |
| created_at / updated_at | TIMESTAMPTZ | |

**contact（联系人）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| customer_id | BIGINT FK | 所属公司 |
| name | VARCHAR(100) | |
| mobile / email / position / department | | |
| wecom_id / whatsapp_id / social_ids | JSONB | 各渠道身份 |
| is_decision_maker | BOOLEAN | 是否决策人 |
| extra | JSONB | |

**customer（客户公司）** — 对应"获取客户公司人员架构"
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| name | VARCHAR(200) | |
| industry | VARCHAR(100) | |
| scale / region | VARCHAR | |
| org_structure | JSONB | 人员架构（授权/公开数据） |
| source | VARCHAR(50) | enrichment 来源（公开数据/客户提供） |
| enrichment_status | SMALLINT | 数据补全状态 |

**identity + identity_mapping（身份图谱）** — 跨渠道合并核心
- `identity`: `id, tenant_id, identity_type(mobile/email/social/wecom/whatsapp/domain), identity_value, status, consent SMALLINT(0未同意/1已同意), consent_time TIMESTAMPTZ`
- `identity_mapping`: `id, tenant_id, identity_id FK, entity_type(lead/contact/customer), entity_id, confidence, source, created_at`

**lead_log（线索日志）**
`id, lead_id FK, action, operator_type(system/user), detail JSONB, created_at` — 全生命周期留痕

#### 7.1.4 会话与消息

**conversation（会话）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| channel_account_id | BIGINT FK | 承接阵地（企微/WhatsApp） |
| contact_id | BIGINT FK | |
| lead_id | BIGINT FK | |
| conversation_type | VARCHAR(20) | dm/wecom_chat/whatsapp |
| status | VARCHAR(20) | active/transferred/closed |
| assigned_to | BIGINT FK→user | 当前人工处理人 |
| last_message_at | TIMESTAMPTZ | |

**message（消息）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| conversation_id | BIGINT FK | |
| sender_type | VARCHAR(20) | customer/ai/human/system |
| content | TEXT | |
| msg_type | VARCHAR(20) | text/image/file/card |
| attachments | JSONB | 附件 ID 列表 |
| ai_generated | BOOLEAN | 是否 AI 生成 |
| quoted_doc_ids | JSONB | 引用知识文档 ID（溯源） |
| raw | JSONB | 原始消息 |

#### 7.1.5 意向与 AI 结果（可审计）

**intent_analysis**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| conversation_id | BIGINT FK | |
| intent | VARCHAR(50) | quote/sample/selection/other |
| confidence | NUMERIC(5,4) | 置信度 |
| model_version | VARCHAR(50) | |
| evidence | TEXT | 判定依据片段 |
| created_at | TIMESTAMPTZ | |

**extracted_field（抽取字段）**
`id, tenant_id, lead_id FK, field_key(scene/qty/budget/lead_time/model), field_value, confidence, source_conversation_id, created_at`

**ai_generation_log（AI 生成日志）**
`id, tenant_id, service, prompt_hash, model, input_tokens, output_tokens, latency_ms, status, created_at` — 成本核算与审计基础（service 标记 Python AI 服务）

#### 7.1.6 内容资产（产品/竞品/知识库）

**product（产品）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| name | VARCHAR(200) | |
| category | VARCHAR(100) | |
| status | SMALLINT | 上架状态 |
| intro | TEXT | 简介 |
| params | JSONB | 参数（规格/选型要素） |
| scenes | JSONB | 适配场景 |
| doc_ids | JSONB | 关联资料包 ID |

**competitor（竞品公司）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| name | VARCHAR(200) | |
| industry | VARCHAR(100) | |
| strength / weakness | TEXT | 优劣势 |
| position | VARCHAR(100) | 市场定位 |

**competitor_comparison（竞品对比/battle card）**
`id, tenant_id, product_id FK, competitor_id FK, dimension, our_point, their_point, rebuttal TEXT, status`

**document（资料/文档）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| title | VARCHAR(200) | |
| doc_type | VARCHAR(50) | product_brochure/case/whitepaper/selection_table |
| file_url | VARCHAR(500) | 对象存储地址 |
| version | INT | 版本号 |
| status | SMALLINT | 发布状态 |
| tags | JSONB | |

**faq / knowledge_doc / knowledge_chunk**
- `faq`: `id, tenant_id, question, answer, keywords JSONB, category, status`
- `knowledge_doc`: `id, tenant_id, title, content TEXT, source_type, status`
- `knowledge_chunk`: `id, knowledge_doc_id FK, chunk_index, content TEXT, embedding vector(1536)`，索引：HNSW (pgvector) — RAG 检索

#### 7.1.7 工作流与任务

**workflow（SOP 定义）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| name | VARCHAR(200) | |
| trigger_type | VARCHAR(50) | lead_created/intent_hit/stage_changed |
| trigger_config | JSONB | 触发条件 |
| version | INT | |
| status | SMALLINT | 启用/停用 |

**workflow_node（流程节点）**
`id, workflow_id FK, node_type(condition/send_doc/ask_field/delay/assign/notify_human/end), config JSONB, prev_node_id, next_node_id`

**workflow_instance（实例）**
`id, tenant_id, workflow_id FK, lead_id FK, current_node_id, status, context JSONB, created_at`

**task（任务）**
`id, tenant_id, lead_id FK, assignee_id FK, task_type, due_at, status, priority`

**action_log（动作执行日志）**
`id, tenant_id, instance_id FK, node_id, action, result JSONB, status, retry_count, created_at`

#### 7.1.8 风控与质检

**risk_rule（风控规则）**
| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGSERIAL PK | |
| tenant_id | BIGINT FK | |
| rule_type | VARCHAR(50) | frequency/sensitive_word/blacklist/compliance |
| name | VARCHAR(100) | |
| config | JSONB | 频控阈值/敏感词列表/时间窗 |
| action | VARCHAR(50) | block/review/warn |
| enabled | BOOLEAN | |

**risk_hit_log（命中日志）**
`id, tenant_id, rule_id FK, target_type(contact/account/lead), target_id, triggered_payload JSONB, action_taken, created_at`

**qa_review（质检记录）**
`id, tenant_id, conversation_id FK, reviewer_id FK, score, issues JSONB, ai_checked, created_at`

**audit_log（审计日志）**
`id, tenant_id, operator_id, action, target_type, target_id, before JSONB, after JSONB, ip, created_at` — 索引 (tenant_id, created_at, target_type)

#### 7.1.9 归因与报表

**attribution（归因记录）**
`id, tenant_id, lead_id FK, channel_account_id, content_id, campaign_id, touch_type(first/last), touch_time, created_at`

**metric_daily（日聚合）**
`id, tenant_id, stat_date, metric_key(lead_count/response_time/effective_rate/intent_accuracy/conversion_rate), dimension JSONB, value NUMERIC, UNIQUE(tenant_id, stat_date, metric_key, dimension)` — 报表预聚合

### 7.2 表关联关系（ER 要点）

```
tenant ─┬─ user ───┬─ lead.owner_id
        │          └─ task.assignee_id
        ├─ channel ── channel_account ── channel_event
        ├─ product ── document ── competitor_comparison ── competitor
        ├─ workflow ── workflow_node ── workflow_instance ── action_log
        ├─ risk_rule ── risk_hit_log
        └─ audit_log

lead ─┬─ customer ── contact ── identity_mapping ── identity
      ├─ conversation ── message ── intent_analysis
      ├─ lead_log
      ├─ extracted_field
      └─ task

conversation ── qa_review
lead ── attribution ── channel_account
lead ── workflow_instance
knowledge_doc ── knowledge_chunk (向量)
```

**核心关系说明**
- `lead` 是业务主节点，向外挂接：客户/联系人、会话、任务、意向、工作流实例、归因、日志
- `identity_mapping` 实现多对多"身份→实体"合并（跨企微/WhatsApp/社媒）
- `knowledge_chunk.embedding` 用 pgvector 向量索引做 RAG 检索，命中后通过 `quoted_doc_ids` 在 message 中留痕溯源

### 7.3 数据存储方案

| 数据 | 存储 | 说明 |
|---|---|---|
| 业务数据（线索/会话/产品等） | PostgreSQL | 关系表 + JSONB 扩展字段，多租户隔离 |
| 知识向量 | PostgreSQL pgvector | HNSW 索引，与业务同库便于事务一致性 |
| 热点/限流/锁 | Redis | 频控计数器(INCR+EXPIRE)、分布式锁、会话缓存 |
| 延时任务/SOP | Redis + RabbitMQ | 延时节点（如 24h 后追问）可靠投递 + 重试 |
| 文件（资料包/附件/录音） | S3 兼容对象存储 | 预签名 URL 分发，存 file_url 引用 |
| 原始渠道事件 | 主库 channel_event（保留期 30 天） | 超出归档到冷存储 |
| 冷数据归档 | 对象存储/归档区 | 历史会话、审计日志按保留策略（如 2 年） |
| 备份 | PG PITR + 对象存储版本 | 每日全量 + WAL 归档 |

### 7.4 数据保留与合规

- 个人信息最小化：contact 敏感字段加密存储；按租户提供删除/导出
- identity_mapping 带 consent 字段（同意记录），满足个保法/GDPR
- 审计日志只增不改，禁止物理删除（逻辑标记）
- 数据保留策略：会话/审计日志按租户配置保留期，到期自动归档

---

## 8. 项目进度与里程碑

推进方式：按里程碑 M0-M5 交付（不按日历排期），每个节点产出可演示、可验收、可试点的版本。三渠道同时上但"深度闭环优先"：渠道先最小接入稳定化，闭环能力优先可复制。

### M0｜立项与方案冻结
**产出：可开工的 PRD 与架构**
- 需求清单（角色×场景×痛点）+ 范围边界（明确哪些"自动化"默认关闭/需审批）
- 核心闭环指标（POC 验收口径）
- 数据模型草案（本文档第 7 章）
- 架构方案（本文档第 6 章）
- 试点客户画像与试点流程（POC 计划）

### M1｜闭环 MVP
**产出：能跑通"入池→承接→分层→转人工"的最小闭环**
- 统一线索池：线索创建/状态流转、SLA、分配/回收/公海（最小集）
- 双阵地承接最小能力：企微欢迎语/资料发送基础链路；WhatsApp 基础会话承接（官方通道优先架构预留）
- 资料包中心：资料上传、版本、可配置发送
- AI 最小能力（可控 RAG）：FAQ 问答 + 对话摘要（带引用/置信度门槛）
- 意向三分类：报价/样本/选型（先规则 + 轻模型）
- 转人工交接包：摘要、推荐回复、缺失信息提示
- 基础看板：响应时效、有效对话率、意向分布
- 交付物：端到端演示脚本 + POC 验收表

### M2｜三渠道"最小接入稳定化"
**产出：抖音+视频号+TikTok 全部可入池、可归因**
- 渠道事件统一模型：评论/私信/表单/点击咨询映射成统一事件
- 三渠道适配器（插件化）：事件采集→去重→落库→重试与限流；字段映射配置
- 归因最小闭环：来源渠道/内容/活动/触点 → 线索
- 风控最小策略：频控、黑白名单、敏感词规则框架
- 交付物：三渠道各自"接入验收清单"（覆盖率、延迟、失败重试、字段完整率）

### M3｜深度闭环增强
**产出：可复制的"报价/样本/选型"SOP 产线**
- 工作流编排引擎（SOP）：条件分支、触发器、延时任务、人工审批节点
- 报价前信息收集：自动追问缺失字段（场景/数量/预算/交期/型号等）
- 产品&竞品中台：产品库、参数、适配场景、资料包绑定；竞品公司库、对比要点、battle card、异议处理
- 质检与复盘：违规承诺提示、抽检、优秀对话沉淀、话术/知识迭代闭环
- 双阵地统一客户视图：企微与 WhatsApp 会话归并展示（身份合并规则落地）

### M4｜企业化与集成
**产出：可卖给中大客户的"治理 + 集成"能力**
- 权限与审计：数据分级、操作审计、敏感字段脱敏、导出/删除留痕
- 生态集成：CRM（商机/合同/回款）双向同步最小集；外呼/联络中心（通话转写/质检/结构化意向）可选接入
- 运维与稳定性：告警、任务补偿、渠道失败自动降级策略

### M5｜商业化与规模化交付
**产出：可订阅售卖、可控毛利、可复制实施**
- 订阅与权限模型：坐席/主管/管理员角色，套餐开关
- AI 成本控制：额度（Fair Use）、超额加购、低置信度降级、缓存复用
- 行业包与实施包：标准知识库模板、话术模板、SOP 模板、验收模板
- 售前 POC 工具箱：演示数据、验收看板、对比报告模板

### 验收方式（统一三类输出）
- **可演示**：一条端到端脚本能跑通
- **可度量**：指标看板能出数（口径固定）
- **可交付**：有配置方式、有风控开关、有审计留痕（不依赖研发盯）

---

## 9. 附录

### 9.1 POC 验收指标（对外承诺的硬指标）

- 响应时效：线索到首次有效回复
- 有效对话率：进入有效沟通的比例
- 意向分层准确率：报价/样本/选型
- 线索→报价转化率（或线索→有效商机率）

### 9.2 关键风险与对冲清单

| 风险 | 对冲 |
|---|---|
| AI 调用成本被重度客户拉爆 | 额度 + 质量评测 + 缓存/复用 + 低置信度转人工 |
| 私信/加人等动作触发平台风控 | 默认保守 + 频控策略 + 合规话术 + 动作可审计可回滚 |
| 三渠道同时上导致闭环不深 | 闭环指标作为产品北极星，渠道只做最小接入到位 |
| AI 乱答（报价/参数/竞品/合规） | 置信度门槛 + 引用溯源 + 敏感话题强制转人工 + 评测集 |
| 个人信息合规（采集/跨境/删除） | 最小化 + 加密 + consent 记录 + 删除导出能力 |
