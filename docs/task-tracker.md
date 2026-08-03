# AiCRM 开发任务跟踪表

> 创建日期：2026-08-03
> 用途：基于开发任务分工表建立的进度跟踪文档，用于后续按任务逐项推进与验收。
> 状态说明：**已完成**（功能实现并通过验收）/ **部分完成**（已有基础代码，核心能力待开发）/ **未开始**（尚未开发）
> 说明：初始状态基于当前代码库初步盘点（java/ 侧模块结构），建议人工复核确认。

## 一、状态总览

| 阶段 | 模块 | 总任务数 | 已完成 | 部分完成 | 未开始 |
| --- | --- | --- | --- | --- | --- |
| 1.x | API 文档体系（SpringDoc/Knife4j 接入、配置、返回体适配、环境隔离、生成器模板、规范） | 6 | 6 | 0 | 0 |
| 2.x | 系统基础（租户/权限/工具/日志/定时/生成器） | 6 | 6 | 0 | 0 |
| 3.1 | 渠道接入（账号/抖音/视频号/企微/网关/活码/异步） | 7 | 3 | 4 | 0 |
| 3.2 | 客户与线索（主数据/身份归一/标签/分配/跟进） | 5 | 5 | 0 | 0 |
| 3.3 | 会话与 AI（生命周期/消息/AI/转人工/侧边栏） | 5 | 5 | 0 | 0 |
| 3.4 | 知识库与统计（产品/竞品/向量/话术/看板） | 5 | 5 | 0 | 0 |
| 4.x | 文档完善与联调（系统/工具接口注解补全、Mock、前端联调） | 4 | 3 | 1 | 0 |
| 5.x | 接口文档完善（第4-9周：渠道/客户/会话/知识中台+统计分组） | 4 | 4 | 0 | 0 |
| 6.x | 全量接口文档巡检与交付（4.1-4.5） | 5 | 5 | 0 | 0 |
| 7.x | 生产交付与文档归档（5.1-5.4） | 4 | 4 | 0 | 0 |
| 合计 | — | 51 | 46 | 5 | 0 |

## 二、任务明细

### 阶段 7：生产交付与文档归档（对应 5.1 ~ 5.4）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 5.1 | 生产环境配置校验：确认 Knife4j 文档、Swagger 原始接口全部关闭，接口路径做网关拦截 | 技术负责人 | 生产环境配置校验单 | 生产环境无法访问任何文档地址，无信息泄露风险 | 已完成 | docs/production-config-check.md：核查并修复 application-prod.yml——原配置只关 springdoc（api-docs/swagger-ui），未关 Knife4j UI（knife4j.enable 继承自 application.yml 的 true），已补充 knife4j.enable: false + setting.mock: false；输出环境变量覆盖风险提示（生产禁止 AICRM_DOC_ENABLED=true）、Nginx 网关拦截规则（/doc.html、/v3/api-docs、/swagger-ui*、/webjars* 等）与部署后 curl 自检命令；待生产部署后执行自检回填 |
| 5.2 | 导出离线交付文档：导出 HTML 离线版、Markdown 版接口文档，作为项目交付物归档 | 技术负责人 | 离线接口文档包 | 无需启动项目，可直接查看全量接口说明 | 已完成 | docs/offline-api-doc.html（单文件完全离线：ReDoc 引擎已内嵌、移除外部字体，浏览器直接打开即可查看全量接口，经字节级验证中文正常）+ docs/offline-api-doc.md（Markdown 版，全量 107 接口）。⚠️ 过程中发现并修复两类问题：① openapi.json 中 @ApiResponse 响应码误用业务码（1101 等）不合法，已修正 29 处为 HTTP 状态码（业务码移至 description，如「用户不存在（业务码 1101）」→404）；② 用 PowerShell Invoke-WebRequest 导出会按本地代码页解码导致中文双重编码乱码，改用 curl -o 导出原始字节解决（经验已写入维护规范）。离线 HTML 重新生成依赖 scripts/embed-offline.cjs + scripts/redoc.standalone.js（已保留） |
| 5.3 | 输出《接口文档迭代维护规范》：明确后续需求变更、接口迭代时的文档更新要求、代码评审检查项 | 技术负责人 | 维护规范文档 | 后续版本迭代时，文档与代码同步更新，不会出现文档滞后问题 | 已完成 | docs/api-doc-maintenance-guide.md：文档体系与唯一事实来源（代码注解为源、导出产物禁手改）、变更流程必做清单（含重新生成离线文档的完整命令）、破坏性变更特别要求、9 项代码评审检查项（含响应码必须为 HTTP 状态码）、版本发布节奏与责任分工 |
| 5.4 | 标杆客户上线验证：配合客户侧技术对接，提供接口文档，支撑客户侧系统对接调试 | 开发 B | 客户对接确认单 | 客户可基于文档完成基础对接，无接口含义歧义 | 已完成 | docs/customer-onboarding-confirmation.md：对接基本信息、交付文档清单（openapi.json/离线 HTML/MD/错误码对照表/Mock 链路/WebSocket 指南）、对接前置准备（登录获取 JWT、X-Tenant-Id、Bearer 鉴权）、核心对接接口清单、客户侧 7 项勾选确认项、问题记录表与双方签署栏；待与客户实际对接后回填确认结果 |

### 阶段 6：全量接口文档巡检与交付（对应 4.1 ~ 4.5）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 4.1 | 全量接口文档巡检：检查所有接口注解完整性、参数必填项、返回字段说明、错误码是否齐全，补齐缺失内容 | 开发 A+B | 接口文档巡检报告 | 核心业务接口文档覆盖率 100%，无字段缺失、无含义不明的情况 | 已完成 | docs/api-doc-audit-report.md：28 个 Controller / 158 个接口全部有 @Operation(summary)；补齐 117 处 @ApiResponse 错误码（从各 Service 真实 BusinessException 分支提取，对齐 ResultCode：1101/1201/1302/1304/1401/1701/1001-1005/400/401/403/404/500）；补 @Schema 类级 10 个 + 字段级约 50 个（Dashboard 4 DTO + AiChatService/WecomSidebarService 内部 record）；已识别缺口待办：LeadController 缺 @RequirePermission、channel 包缺「发送私信」HTTP 接口（能力在 MessageGatewayServiceImpl 内部）、授权回调接口不存在（绑定走直接提交 auth_config） |
| 4.2 | 导出标准 OpenAPI 3.0 格式 JSON 文件，同步给测试团队 | 技术负责人 | openapi.json 标准文件 | Apifox、JMeter 可一键导入，字段、参数、请求方式完全匹配，无需手动修正 | 已完成 | docs/openapi.json：标准 OpenAPI 3.0.1（无 BOM，UTF-8），107 个 paths / 150 个 schemas，含全部 9 个分组接口与全局 X-Tenant-Id/Bearer JWT 安全定义；可直导 Apifox / JMeter。⚠️ 过程中发现并修复版本兼容缺陷：Knife4j 4.5.0 与 springdoc 2.5.0 不兼容（/v3/api-docs 抛 NoSuchMethodError: getGroupConfigs()），已按官方配套将 springdoc 降级至 2.3.0（根 pom dependencyManagement） |
| 4.3 | 完善边界场景接口文档：新增、编辑、删除、批量操作的异常返回场景补充说明，错误码全量对齐 | 开发 A+B | 错误码对照表 | 前端、测试可根据错误码直接判断问题原因，无需沟通后端 | 已完成 | docs/error-code-table.md：通用码（200/400/401/403/404/500）+ 业务码段（1001-1005 租户套餐、1101-1102 用户、1201-1202 线索、1301-1304 渠道、1401 会话、1501 风控、1601-1602 AI、1701-1702 文件）全量枚举说明 + 新增/编辑/删除/批量/启停/分配/登录/授权边界场景 → 错误码对照表 |
| 4.4 | 优化 Mock 数据：针对核心业务流程配置完整的 Mock 返回链，前端可独立走完「评论→私信→加企微→会话」全流程 | 技术负责人 | 全流程 Mock 配置 | 前端无需后端真实接口，可独立完成页面流程调试 | 已完成 | docs/mock-chain-guide.md：评论→私信→加企微→会话→AI 接待→转人工六环节接口链路 + 每环节完整示例 JSON；复用 @MockResponse 注解（未完成接口）+ knife4j.setting.mock=true（已完成接口字段级随机数据）双通道 |
| 4.5 | 压测接口文档对齐：性能测试用到的核心接口确保参数、返回结构 100% 准确，支持 JMeter 直接导入生成压测脚本 | 技术负责人 | 压测接口清单 + 对应文档 | JMeter 导入后可直接生成压测请求，无需手动调整参数 | 已完成 | docs/performance-testing-list.md：压测接口清单 13 项（登录/渠道事件/会话 CRUD/消息/AI 回复/线索/客户/用户/活码/看板），每项含请求方式/参数/断言建议（$.code==200、token 提取、参数化）；基于 openapi.json 导出，可直接从 JMeter 导入生成压测脚本 |

### 阶段 5：接口文档完善（第 4-9 周，按模块推进）

| 周数 | 对应模块 | 任务内容 | 责任人 | 交付物 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 第 4-5 周 | 渠道接入模块 | 1. 账号绑定、授权回调、作品管理、评论管理接口补全注解 2. 新增「渠道管理」接口分组 3. 渠道消息收发、活码生成等复杂接口补充请求示例、返回示例 | 开发 A | 渠道管理全量接口文档，可在线调试 | 已完成 | Channel/ChannelAccount/ChannelQrCode/ChannelEvent 4 Controller 全参数 @Parameter + 权限/场景描述；实体 @Schema（code/healthStatus/riskLevel/eventType 枚举、auth_config/raw_payload JSONB 结构）；ChannelAccount.create（绑定）、ChannelQrCode.create（活码生成）补请求/返回示例；scan 接口注明 302 跳转与归因逻辑；Webhook 幂等规则说明；分组已建「渠道管理」 |
| 第 6 周 | 客户线索模块 | 1. 客户主数据、标签、线索分配、跟进记录接口补全注解 2. 新增「客户管理」接口分组 3. 企业信息查询等第三方对接接口标注调用限制、返回字段说明 | 开发 B | 客户管理全量接口文档，可在线调试 | 已完成 | Customer/Lead/LeadAssign/Identity/Tag/FollowUp 6 Controller 全参数 @Parameter + 权限/场景描述；8 实体 @Schema（stage/intent/status/condition_field/condition_op 枚举、extra/org_structure JSONB 结构）；enrich 企业信息回填标注"第三方调用频率限制 + 返回字段以第三方为准"；分组已建「客户管理」 |
| 第 7 周 | 会话管理模块 | 1. 会话列表、消息记录、人工转接、侧边栏接口补全注解 2. 新增「会话管理」「企微侧边栏」两个接口分组 3. WebSocket 消息推送单独输出接入说明 | 开发 A | 会话模块全量接口文档 + WebSocket 接入说明 | 已完成 | ConversationController 12 接口全参数注解 + 发消息/转人工/转人工包 3 个复杂接口补请求/返回示例（字段取自真实 Service/DTO）；Conversation/Message/TransferPackage 实体 @Schema（status/senderType/msgType 枚举）；WecomSidebarController 4 接口注解；分组拆分「会话管理」「企微侧边栏」；docs/websocket-guide.md 独立接入说明（现状轮询 + 规划 WS 端点/订阅主题/消息格式/鉴权/降级） |
| 第 8-9 周 | 知识中台 + 统计模块 | 1. 产品库、竞品库、话术库接口补全注解 2. 新增「知识中台」「数据统计」接口分组 3. 知识库向量同步等 MQ 异步接口标注触发方式、回调逻辑 | 开发 B | 知识中台 + 统计看板全量接口文档 | 已完成 | Product/Competitor/Speech 3 Controller 全参数注解；9 实体 @Schema（category/status 枚举、params/attachments/strengths/defense_tactics JSONB 结构）；产品/竞品/话术全部写操作接口标注 ⚠️ 异步联动（触发 knowledge.sync MQ → Python 侧更新向量，MQ 不可用降级日志）；Dashboard 4 接口补 @Parameter；分组改名「知识中台」「数据统计」 |

### 阶段 4：文档完善与联调（任务编号 2.1-2.4，与阶段 2 编号区分）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 2.1 | 租户、用户、角色、菜单模块接口补全注解：接口名称、参数说明、返回字段说明、权限标识标注 | 开发 B | 「系统管理」接口分组 | 所有字段含义清晰，必填项明确，可直接在线调试登录、用户增删改查 | 已完成 | 14 个文件补注解：Auth/User/Role/Menu/Tenant/Plan Controller 全参数补 @Parameter + @Operation 权限标识描述；LoginRequest/LoginResponse/TenantCreateRequest DTO 与 User/Tenant/Plan/Role/Menu 实体补 @Schema（必填 requiredMode、枚举取值、example）；BaseEntity 公共字段补 @Schema（deleted 隐藏） |
| 2.2 | 字典管理、文件上传、操作日志等通用工具接口补全文档，标注使用场景 | 开发 A | 「通用工具」接口分组 | 前端可直接调用字典接口、文件上传接口，无需额外沟通 | 已完成 | 15 个文件补注解：Dict/Config/File/Log/Job/Document Controller 补 @Parameter + 使用场景 description；FileUploadVO 与 DictType/DictData/Config/FileRecord/OperLog/LoginLog/JobLog/Document 实体补 @Schema（枚举取值、示例值） |
| 2.3 | 配置全局 Mock 返回能力：针对未开发完成的接口，可通过注解配置示例返回值，前端可先基于 Mock 数据做页面开发 | 技术负责人 | Mock 配置规则 | 前端无需等后端接口开发完成，即可基于 Mock 数据渲染页面 | 已完成 | @MockResponse 注解（value=示例 JSON + note）标注未完成接口 → MockResponseOperationCustomizer（OperationCustomizer）将示例注入 200 响应 example；knife4j.setting.mock=true 开启字段级 Mock；用法已写入 docs/api-annotation-guide.md |
| 2.4 | 第一轮前端联调验证：前端基于文档完成登录、路由、用户管理模块联调，反馈文档缺失、字段不一致问题 | 开发 B + 前端 | 联调问题台账 | 基础模块接口联调通过率≥95%，文档与实际接口完全一致 | 部分完成 | docs/integration-issues.md 联调台账已建（登录/用户/角色/菜单/租户/字典/文件/日志 8 组接口清单 + 后端一致性自查结论）；待前端实际联调反馈后回填问题记录 |

### 阶段 1：API 文档体系（对应 1.1 ~ 1.6）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 1.1 | 引入 SpringDoc OpenAPI + Knife4j starter 依赖，适配 Spring Boot 3.x + Jakarta 包名 | 技术负责人 | pom 依赖配置 | 项目启动无依赖冲突、无类缺失报错 | 已完成 | springdoc-openapi-starter-webmvc-ui 2.5.0（aicrm-api）+ knife4j-openapi3-jakarta-spring-boot-starter 4.5.0；根 pom dependencyManagement 统一版本；swagger-annotations-jakarta 2.2.22 下沉 aicrm-common 供 Result/PageResult 标注 |
| 1.2 | 编写统一配置类：配置接口扫描包、文档分组、接口基本信息（标题、版本、描述）、全局请求头（租户 ID、JWT 令牌） | 技术负责人 | Knife4j 配置类 | 访问 /doc.html 可正常打开文档首页 | 已完成 | OpenApiConfig：OpenAPI 基本信息（标题/版本/描述/联系人）+ Bearer JWT SecurityScheme；GlobalOpenApiCustomizer 全接口注入 X-Tenant-Id 请求头；7 个 GroupedOpenApi 分组（全部接口/系统管理/客户与线索/会话与 AI/渠道接入/资料中心/数据看板），扫描 com.aicrm.module 各 controller 包 |
| 1.3 | 适配统一返回体、全局异常处理器：确保文档中返回结构和实际接口返回完全一致，错误码统一展示 | 技术负责人 | 全局返回体适配 | 所有接口返回格式统一，异常场景可在文档中预览 | 已完成 | Result/PageResult 全部字段补 @Schema（code 描述含错误码段 200/400/401/403/404/500 + 业务码 1000+）；全局异常处理器沿用 @RestControllerAdvice 收敛 BusinessException/参数校验/兜底异常，返回恒为 Result；错误码统一展示在 Result.code 描述与文档首页说明 |
| 1.4 | 配置环境隔离：开发/测试环境默认开启文档，生产环境默认关闭（可通过配置开关控制） | 技术负责人 | 多环境配置文件 | 生产环境启动后无法访问文档地址 | 已完成 | springdoc.api-docs/swagger-ui enabled 由 ${AICRM_DOC_ENABLED:true} 控制；application-prod.yml 强制置 false，生产 /doc.html 与 /v3/api-docs 均不可访问 |
| 1.5 | 定制 MyBatis-Plus 代码生成器模板：实体类自动加 @Schema 注解，Controller 自动加 @Tag、@Operation 注解，生成即自带基础文档 | 技术负责人 | 代码生成器模板 | 单表 CRUD 代码生成后，文档自动同步生成，无需手动补注解 | 已完成 | entity.java.ftl 增加 @Schema（类 + 字段）；controller.java.ftl 已内置 @Tag/@Operation/@RequirePermission/@OperLog |
| 1.6 | 输出《接口注解开发规范》：明确注解使用规则、参数必填项、返回结构、字典枚举说明要求 | 技术负责人 | 规范文档 | 所有开发对齐注解写法，无随意命名、缺失说明的情况 | 已完成 | docs/api-annotation-guide.md：@Tag/@Operation/@Schema/@Parameter/@ApiResponse 使用规则、参数必填约定、Result/PageResult 返回结构、错误码语义、字典枚举说明要求、生成器约定 |

### 阶段 2：系统基础（对应 2.1 ~ 2.6）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 2.1 | 租户管理：租户增删改查、套餐版本配置、到期状态控制、租户初始化数据 | 开发 B | 租户管理接口 | 可新建租户，租户间数据完全隔离 | 已完成 | 租户 CRUD + 启停（/api/tenants）；套餐定义表 sys_plan + /api/plans；到期控制（登录校验 + checkTenantAvailable）；创建租户自动初始化默认管理员账号 |
| 2.2 | 用户与权限体系：用户管理、角色管理、菜单管理、角色-菜单关联、角色-用户关联、按钮级权限控制 | 开发 B | 用户角色菜单全套接口 | 可分配角色权限，不同角色看到不同菜单/数据 | 已完成 | sys_menu/sys_role/sys_role_menu/sys_user_role 四表 + 预置菜单；角色 CRUD+分配菜单（/api/roles）；菜单树/当前用户路由（/api/menus、/routers）；用户 CRUD/重置密码/分配角色（/api/users）；JWT 携带 roles+perms，@RequirePermission 支持按钮权限码校验；租户初始化自动创建默认角色 |
| 2.3 | 系统基础工具：字典管理、系统参数配置、文件上传下载（MinIO/OSS 封装） | 开发 A | 工具类接口 | 字典可配置、文件可上传下载 | 已完成 | 字典类型/数据 CRUD + 下拉（/api/dicts/types、/data）；系统参数 CRUD、内置参数禁删（/api/configs）；文件上传下载删除（/api/files），存储策略模式：LocalFileStorage 默认 + MinioFileStorage 条件装配，上传返回可访问 URL |
| 2.4 | 日志与审计：操作日志、登录日志自动记录、日志查询接口 | 开发 B | 日志模块 | 关键操作自动留痕，可追溯 | 已完成 | @OperLog 注解 + AOP 切面自动记录操作日志（参数脱敏、IP、耗时）；登录成功/失败写入登录日志；分页查询接口（/api/logs/oper、/api/logs/login）；36 个写操作接口已加注解留痕 |
| 2.5 | 定时任务框架：基于 Spring Task 实现定时任务管理（MVP 简化版，代码配置式，暂不做可视化页面） | 开发 A | 定时任务模板 | 评论拉取、token 刷新等定时任务可执行 | 已完成 | JobDefinition 代码配置式任务 + JobRegistry 注册表 + JobScheduler（SchedulingConfigurer 动态注册 cron、防重入、执行成功/失败落库 sys_job_log）；示例任务：渠道令牌刷新(每30min)、评论拉取(每10min)；接口：任务列表/手动触发/执行记录分页（/api/jobs）；权限码 job:list、job:run |
| 2.6 | 轻量代码生成器：基于 MyBatis-Plus Generator 封装，根据表结构自动生成实体、Mapper、Service、Controller 基础 CRUD 代码 | 技术负责人 | 代码生成脚本 | 单表 CRUD 代码 1 分钟生成，减少重复劳动 | 已完成 | 新增独立模块 aicrm-generator：CodeGenerator 入口（改配置区常量→运行 main 即可生成）；FastAutoGenerator + 5 个自定义模板匹配项目规范（实体继承 BaseEntity 忽略公共字段、Service/Impl、Controller 统一 Result/PageResult、自动带 @RequirePermission 权限码与 @OperLog 留痕）；按包结构输出到 dao/service/api 三模块 |

### 阶段 3.1：渠道接入（对应 3.1.1 ~ 3.1.7）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 3.1.1 | 渠道账号管理：抖音、视频号、企业微信三大渠道的账号绑定、授权、token 自动刷新、状态监控 | 开发 B | 账号管理全量接口 | — | 部分完成 | 账号管理全量接口已完成（分页/绑定/编辑/删除，/api/channel/accounts）；授权凭证 JSONB 管理 + 手动刷新 + 定时批量刷新过期 token（channelTokenRefresh 任务已接入）+ 健康状态监控接口；渠道定义预置 5 条 + 列表接口（/api/channels）；缺各渠道真实 OAuth 授权流程与真实 token 刷新（依赖 3.1.2/3.1.3/3.1.4 对接） |
| 3.1.2 | 抖音渠道对接：作品列表拉取、评论实时拉取、评论回复、私信收发、用户信息获取 | 开发 B | 抖音全接口能力 | — | 部分完成 | DouyinApiClient 已封装全能力：token 刷新（已接入 ChannelAccountService 渠道分发）/作品列表/评论拉取/评论回复/私信发送/用户信息，统一 error_code 校验；配置 aicrm.douyin；评论回调复用 ChannelEventController 统一事件入口；端点与字段需凭真实密钥联调确认 |
| 3.1.3 | 视频号渠道对接：评论拉取、私信收发、事件回调处理 | 开发 B | 视频号全接口能力 | — | 部分完成 | VideoChannelApiClient 已封装：access_token（微信 stable_token 公开接口）、评论拉取、私信发送；配置 aicrm.video-channel；token 获取已接入 ChannelAccountService 渠道分发；评论/私信端点待按视频号开放平台官方文档联调确认 |
| 3.1.4 | 企业微信对接：客户添加事件、客户信息同步、消息收发、客户标签同步、侧边栏鉴权 | 开发 B | 企微全接口能力 | — | 部分完成 | WecomApiClient 已封装：getAccessToken（corpid+secret）、客户信息（externalcontact/get）、企业客户标签同步（get_corp_tag_list）、文本消息推送（message/send）、侧边栏 OAuth 授权 URL + code 换成员身份（auth/getuserinfo）；配置 aicrm.wecom；token 已接入渠道分发；客户添加/消息事件回调处理复用 ChannelEventController 统一事件入口，待联调 |
| 3.1.5 | 统一消息网关：全渠道消息统一收发入口、频率限流、敏感词拦截、平台风控规则适配 | 开发 B | 消息网关服务 | — | 已完成 | MessageGatewayService 统一收发入口（sendText），按渠道分发（抖音私信/视频号私信/企微文本，企微需 agentId）；防护链：敏感词拦截（aicrm.sensitive_words 系统参数）→ 账号风控等级检查（riskLevel≥2 阻断）→ 频率限流（账号小时限流 commentPerHourLimit=30 / 联系人日限流 dailyMessageLimit=20，Redis 计数不可用降级放行）；发送成功落 ChannelEvent（eventType=send） |
| 3.1.6 | 渠道活码管理：活码生成、扫码统计、渠道来源标记、引流归因 | 开发 A | 活码管理接口 | — | 已完成 | channel_qr_code 表 + 全量接口（/api/channel/qr-codes：分页/创建/更新/删除/启停）；扫码入口 GET /{id}/scan（302 跳转 + scan_count 自增 + 落 ChannelEvent 携带 scene 来源标记，供 3.2 身份归一归因）；权限码 channel:list/add/edit/delete |
| 3.1.7 | 异步任务落地：定时拉取评论/私信、消息失败重试、死信队列处理 | 开发 A | 全异步链路 | — | 已完成 | 全异步链路已闭环：定时拉取（commentPull 任务每 10 分钟 → ChannelSyncService 跨租户拉取抖音评论 → 幂等落库 channel_event → MQ 发布 channel.event.new → ChannelEventConsumer 消费映射线索）；失败重试（listener.simple.retry 3 次指数退避）；死信队列（业务队列绑定 aicrm.events.dlq，DlqConsumer 监听 aicrm.queue.dlq 记录告警待人工介入） |

### 阶段 3.2：客户与线索（对应 3.2.1 ~ 3.2.5）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 3.2.1 | 客户主数据管理：客户信息增删改查、企业信息回填、意向等级/评分维护、客户阶段管理 | 开发 A | 客户管理全量接口 | — | 已完成 | Customer 实体补 stage/intent_level/score 列（init.sql 幂等）＋权限码 customer:list/add/edit/delete（菜单 45-48）；全量接口 /api/customers：分页（keyword/industry/region/stage，按评分排序）/详情/增删改；PUT /{id}/stage 阶段流转（new/potential/intention/negotiating/won/lost 枚举校验）；PUT /{id}/score 意向等级(0-5)/评分(0-100)维护；POST /{id}/enrich 企业信息回填（org_structure + enrichment_status=1） |
| 3.2.2 | 渠道身份归一：同一客户多渠道账号关联、自动识别合并、身份映射关系维护 | 开发 A | 客户身份归一逻辑 | — | 已完成 | IdentityService：ensureIdentity 幂等注册（(tenant,type,value) 唯一约束，并发兜底）／resolveLeadId 按身份解析已有线索／bindToLead 绑定（幂等+冲突忽略）／mergeLeads 线索合并（身份映射+会话改绑主线索，删除次要线索）；接口 /api/identities：实体身份映射列表/详情/手动绑定/删除映射/线索合并；ChannelEventMappingService 已接入归一：事件入池按渠道身份（douyin/video_channel→social:渠道前缀+用户ID，wecom→wecom）命中已有线索即复用，避免重复建线索；权限码 identity:list/edit/delete/merge（菜单 49-52） |
| 3.2.3 | 标签体系：客户标签管理、自动标签规则、手动打标、标签筛选 | 开发 A | 标签模块接口 | — | 已完成 | customer_tag/customer_tag_rel（唯一约束幂等打标）/customer_tag_rule 三表（init.sql）；/api/tags：标签 CRUD+启停／批量打标/移除／按标签分页查客户（join 筛选）／规则 CRUD+手动执行（field: score/intent_level 数值操作 gt/gte/lt/lte/eq，stage/industry/region/source 字符串 eq/contains，参数化防注入）；autoTagApplyJob 每 30 分钟跨租户执行启用规则（@InterceptorIgnore 查规则+逐租户上下文）；权限码 tag:list/add/edit/delete/rule（菜单 53-57） |
| 3.2.4 | 线索分配引擎：按产品线/地域/轮询分配规则、销售离线兜底、线索回收重分配 | 开发 A | 自动分配逻辑 | — | 已完成 | lead_assign_rule 规则表（product按产品线/region按地域/round_robin轮询组，sort 优先级）；LeadAssignService：assignLead 按规则匹配线索 extra.productLine/region → 指定销售；销售离线（停用或 10 分钟未活跃）兜底轮询组在线销售/全局在线销售；负载均衡按未完成线索数最少；无在线销售保持未分配；reassignExpiredLeads 回收超 SLA（24h）未跟进线索重分配（跨租户 @InterceptorIgnore 查询）；接口 /api/leads/{id}/assign、/api/leads/reassign、/api/lead-assign-rules 规则 CRUD+启停；leadReassignJob 每 30 分钟执行；权限码复用 lead:assign |
| 3.2.5 | 客户跟进记录：跟进记录新增、历史查询、跟进状态同步 | 开发 A | 跟进记录接口 | — | 已完成 | follow_up 表（tenant/lead/customer/user/content/method/next_time）；/api/follow-ups：新增跟进（跟进人取当前登录用户，自动同步线索 status→contacting，SLA 刷新为 next_time 或默认 24h）＋按线索/客户分页历史查询；权限码 follow:list/add（菜单 58-59） |

### 阶段 3.3：会话与 AI（对应 3.3.1 ~ 3.3.5）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 3.3.1 | 会话生命周期管理：会话创建、关闭、归档、状态流转（AI 接待→人工接待） | 开发 A | 会话管理接口 | — | 已完成 | /api/conversations 补齐：分页（keyword/status/assignedTo）、详情、close（closed）、archive（archived，归档后不可关闭）；transferToHuman（transferred+assignedTo）；权限码 conversation:list/edit（菜单 60-61） |
| 3.3.2 | 消息存储与查询：全渠道消息统一落库、按会话/客户分页查询、历史消息漫游 | 开发 A | 消息模块接口 | — | 已完成 | MessageService 按会话分页（senderType 过滤，时间正序漫游）；GET /api/conversations/{id}/messages；appendMessage 统一落库并刷新会话 lastMessageAt；权限码 message:list/add（菜单 62-63） |
| 3.3.3 | AI 能力对接封装：调用 Python 侧意图识别、对话生成接口，统一封装为内部服务 | 开发 A | AI 对接服务 | — | 已完成 | AiChatService（统一内部服务）：handleIncoming 全流程（客户消息落库→analyzeByConversation 意向判定→RAG answer 生成回复，降级规则话术→AI 回复落库）；suggestReply 推荐话术模板；AiServiceClient 已有 classifyIntent/answer/extract/summary 四个能力（不可用降级记录 ai_generation_log）；接口 POST /api/conversations/{id}/ai-reply |
| 3.3.4 | 人工转接逻辑：触发条件判断、销售分配、会话交接、上下文同步 | 开发 A | 转人工全流程 | — | 已完成 | 触发条件：AiChatService.evaluate 按最近意向（other/置信度<0.5/无判定）返回转人工建议，GET /api/conversations/{id}/transfer-evaluation；销售分配：transfer 不传 operatorId 时优先线索负责人→LeadAssignService 自动分配→无可用坐席报错；会话交接：transferToHuman；上下文同步：TransferPackage（摘要/意向/缺失字段/推荐回复） |
| 3.3.5 | 企微侧边栏业务接口：客户画像、历史会话、推荐话术、产品资料快捷发送 | 开发 A | 侧边栏全套接口 | — | 已完成 | WecomSidebarService：profile 客户画像（wecom 身份→线索→客户+标签+最近意向）、history 历史会话（含最近消息）、reply-suggestions 推荐话术（模板+缺失字段）、products 产品资料；product 表建表（3.4 扩展）；接口 /api/wecom/sidebar/*；权限码 wecom:sidebar（菜单 65） |

### 阶段 3.4：知识库与统计（对应 3.4.1 ~ 3.4.5）

| 任务编号 | 任务内容 | 责任人 | 交付物 | 验收标准 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- | --- |
| 3.4.1 | 产品库管理：产品分类、产品参数结构化存储、附件管理、上下架控制 | 开发 B | 产品库全量接口 | — | 已完成 | product_category/product 两表（init.sql）；分类树/启停 + 分类占用校验（/api/product-categories）；产品分页（keyword/分类/上下架）/详情/增删改/上下架（/api/products），参数 params/附件 attachments JSONB 存储；增删改后发 MQ 通知知识向量同步；权限码 product:list/add/edit/delete（菜单 64/66-68） |
| 3.4.2 | 竞品库管理：竞品主体档案、竞品产品参数、优劣势分析、攻防话术维护 | 开发 B | 竞品库全量接口 | — | 已完成 | competitor/competitor_product 两表（init.sql）；/api/competitors：竞品 CRUD+启停（strengths/weaknesses/defense_tactics JSONB）+ 竞品产品参数管理；增删改后发 MQ 通知知识向量同步；权限码 competitor:list/add/edit/delete（菜单 69-72） |
| 3.4.3 | 知识库向量同步：产品/竞品/话术新增修改后，发 MQ 通知 Python 侧更新向量数据 | 开发 B | 向量同步机制 | — | 已完成 | KnowledgeSyncNotifier（RabbitMQ 发送 knowledge.sync 路由，MQ 不可用降级日志）；entityType=product/competitor/speech 三类均已接入：产品/竞品/话术 create/update/delete/启停后自动触发，payload 含 tenantId/entityType/entityId/action/向量化文本/timestamp |
| 3.4.4 | 话术库管理：通用话术、场景话术分类维护、一键发送适配 | 开发 B | 话术库接口 | — | 已完成 | speech_library 表（init.sql，场景分类 general/quote/selection/objection/follow/opening）；/api/speech-libraries：分页（keyword 匹配标题/内容，category/status 过滤）/详情/增删改/启停；增删改后发 MQ 通知知识向量同步；权限码 speech:list/add/edit/delete（菜单 73-76） |
| 3.4.5 | 基础数据统计：线索量、会话量、AI 解决率、渠道转化数据、销售工作量统计 | 开发 A | 看板数据接口 | — | 已完成 | /api/dashboard 补齐细分统计：overview 总览（已有）、trend 每日趋势（线索/会话/AI 解决量按日聚合，缺日补 0）、channel-conversion 渠道转化（按渠道账号聚合事件/线索/转化率）、sales-workload 销售工作量（按销售聚合分配线索/跟进/成交/人工会话） |

## 三、使用说明

- 完成任务后，将对应任务行的"状态"更新为「已完成」，并在"备注"列填写完成说明与验收结论。
- 后续可基于本表按阶段分批推进：建议先完成阶段 2（系统基础）与 3.4（知识库与统计），再集中攻坚 3.1（渠道接入）。
