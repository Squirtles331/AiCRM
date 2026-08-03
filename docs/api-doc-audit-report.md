# API 接口文档巡检报告（4.1 全量接口文档巡检）

- **巡检日期**：2026-08-03
- **巡检范围**：`aicrm-api/src/main/java/com/aicrm/module/` 下全部 28 个业务 Controller（158 个接口方法）
- **依据规范**：`docs/api-annotation-guide.md`；错误码定义：`aicrm-common/.../ResultCode.java`
- **规则约束**：仅补充文档注解（@Operation/@Parameter/@Schema/@ApiResponse 及 import），未改动任何业务逻辑、方法签名、默认值、权限注解；未改动文件编码。

---

## 一、覆盖统计

| 指标 | 数值 |
| --- | --- |
| Controller 总数 | 28 |
| 接口总数（@Operation 方法数） | 158 |
| 本次补充 @ApiResponse 注解数 | 117 |
| 本次补充 @Schema 的类型数（类级） | 10（4 个 DTO 类 + IntentCount 内部类 + 5 个 record） |
| 本次补充 @Schema 字段级注解数 | 约 50 |
| 修改文件总数 | 28（22 个 Controller + 6 个 DTO/Service 文件） |
| 未达标（遗留问题）项 | 0 项硬性缺失（见第四节说明） |

> 注：@Operation(summary)、@PathVariable/@RequestParam 的 @Parameter 注解在既往巡检中已全部补齐，本次复查确认无遗漏；实体类（aicrm-dao 32 个）已全部含 @Schema，本次未改动。

---

## 二、巡检清单逐项结果

### 2.1 @Operation(summary) 完整性 —— ✅ 全部达标
158 个接口方法均有 `@Operation(summary = "…")`，summary 描述清晰（含操作语义与权限说明）。复查中未发现缺失。

### 2.2 参数注解 —— ✅ 全部达标
- 全部 `@PathVariable` 均带 `@Parameter(description = "…", required = true)`；
- 全部 `@RequestParam` 均带 `@Parameter(description = "…")`；
- 分页参数统一注明 `页码，默认 1` / `每页条数，默认 20`。
- 复查全量 156 处 @PathVariable/@RequestParam 参数行，无遗漏。

### 2.3 DTO / 返回实体 @Schema —— ✅ 本次补齐 6 个文件
- **本轮补齐**（此前缺 @Schema）：
  - `dashboard/dto/DashboardOverview.java`（类级 + 8 个字段 + 内部类 IntentCount）
  - `dashboard/dto/DashboardTrend.java`（类级 + 3 个字段）
  - `dashboard/dto/ChannelConversionStat.java`（类级 + 5 个字段）
  - `dashboard/dto/SalesWorkloadStat.java`（类级 + 5 个字段）
  - `conversation/service/AiChatService.java`：`AiReply`、`TransferEvaluation` 两个 record（类级 + 字段级，含枚举取值说明）
  - `wecom/service/WecomSidebarService.java`：`CustomerProfile`、`ConversationBrief`、`ReplySuggestion` 三个 record（类级 + 字段级，含枚举取值说明）
- 实体类（aicrm-dao）、LoginRequest/LoginResponse/TenantCreateRequest/FileUploadVO/TransferPackage 等既往已补，复查通过。

### 2.4 写操作接口 @ApiResponse 错误码 —— ✅ 本次重点补全（117 处）
按 Service 层实际抛出的 `BusinessException(ResultCode.XXX)` 失败分支，为存在明确失败场景的写操作接口补充 @ApiResponse；读操作与无明确失败分支的接口（如渠道事件接收 receive、只读列表/详情）不强行凑数。按模块明细如下：

| Controller | 接口 | 补充错误码 |
| --- | --- | --- |
| AuthController | login | 400 / 1001 / 1002 / 1003 |
| UserController | create / update / delete / resetPassword / updateStatus / setRoles | 400、1101 |
| TenantController | create / update / delete / updateStatus | 400 / 1001 / 1004 / 1005 |
| MenuController | create / update / delete | 400 |
| RoleController | create / update / delete / setMenus / updateStatus | 400 |
| DictController | createType / updateType / deleteType / createData / updateData / deleteData | 400 |
| ConfigController | delete | 400 |
| JobController | run | 404 |
| FileController | upload / delete | 400、1701 |
| DocumentController | create / publish / upload | 400、404、1701 |
| ChannelAccountController | create / update / delete / refreshToken / updateHealth | 400、1302、1304 |
| ChannelQrCodeController | create / update / delete / updateStatus / scan | 400、404、1302 |
| LeadController | create | 400 |
| LeadAssignController | assign / createRule / updateRule / deleteRule / updateRuleStatus | 400、404、1201 |
| CustomerController | create / update / delete / updateStage / updateScore / enrich | 400、404 |
| IdentityController | bind / removeMapping / mergeLeads | 400、404、1201 |
| TagController | createTag / updateTag / deleteTag / updateTagStatus / tagCustomers / untagCustomer / createRule / updateRule / deleteRule / applyRule | 400、404 |
| FollowUpController | create | 400 |
| ConversationController | create / appendMessage / close / archive / aiReply / transfer | 400、1401 |
| ProductController | createCategory / updateCategory / deleteCategory / updateCategoryStatus / create / update / delete / updateStatus | 400、404 |
| CompetitorController | create / update / delete / updateStatus / createProduct / updateProduct / deleteProduct | 400、404 |
| SpeechLibraryController | create / update / delete / updateStatus | 400、404 |

**关键错误码对照**（取自语料库 ResultCode.java）：
- USER_NOT_FOUND=1101、CHANNEL_ACCOUNT_NOT_FOUND=1302、CHANNEL_API_ERROR=1304、LEAD_NOT_FOUND=1201、CONVERSATION_NOT_FOUND=1401、FILE_UPLOAD_ERROR=1701、TENANT_NOT_FOUND=1001、TENANT_DISABLED=1002、TENANT_EXPIRED=1003、PLAN_NOT_FOUND=1004、PLAN_DISABLED=1005、BAD_REQUEST=400、NOT_FOUND=404。

---

## 三、修改文件清单（28 个）

### Controller（22 个）
1. `java/aicrm-api/src/main/java/com/aicrm/module/user/controller/AuthController.java`
2. `java/aicrm-api/src/main/java/com/aicrm/module/user/controller/UserController.java`
3. `java/aicrm-api/src/main/java/com/aicrm/module/tenant/controller/TenantController.java`
4. `java/aicrm-api/src/main/java/com/aicrm/module/system/controller/MenuController.java`
5. `java/aicrm-api/src/main/java/com/aicrm/module/system/controller/RoleController.java`
6. `java/aicrm-api/src/main/java/com/aicrm/module/system/controller/DictController.java`
7. `java/aicrm-api/src/main/java/com/aicrm/module/system/controller/ConfigController.java`
8. `java/aicrm-api/src/main/java/com/aicrm/module/job/controller/JobController.java`
9. `java/aicrm-api/src/main/java/com/aicrm/module/file/controller/FileController.java`
10. `java/aicrm-api/src/main/java/com/aicrm/module/document/controller/DocumentController.java`
11. `java/aicrm-api/src/main/java/com/aicrm/module/channel/controller/ChannelAccountController.java`
12. `java/aicrm-api/src/main/java/com/aicrm/module/channel/controller/ChannelQrCodeController.java`
13. `java/aicrm-api/src/main/java/com/aicrm/module/lead/controller/LeadController.java`
14. `java/aicrm-api/src/main/java/com/aicrm/module/lead/controller/LeadAssignController.java`
15. `java/aicrm-api/src/main/java/com/aicrm/module/customer/controller/CustomerController.java`
16. `java/aicrm-api/src/main/java/com/aicrm/module/identity/controller/IdentityController.java`
17. `java/aicrm-api/src/main/java/com/aicrm/module/tag/controller/TagController.java`
18. `java/aicrm-api/src/main/java/com/aicrm/module/followup/controller/FollowUpController.java`
19. `java/aicrm-api/src/main/java/com/aicrm/module/conversation/controller/ConversationController.java`
20. `java/aicrm-api/src/main/java/com/aicrm/module/product/controller/ProductController.java`
21. `java/aicrm-api/src/main/java/com/aicrm/module/competitor/controller/CompetitorController.java`
22. `java/aicrm-api/src/main/java/com/aicrm/module/speech/controller/SpeechLibraryController.java`

### DTO / Service 接口（6 个）
23. `java/aicrm-service/src/main/java/com/aicrm/module/dashboard/dto/DashboardOverview.java`
24. `java/aicrm-service/src/main/java/com/aicrm/module/dashboard/dto/DashboardTrend.java`
25. `java/aicrm-service/src/main/java/com/aicrm/module/dashboard/dto/ChannelConversionStat.java`
26. `java/aicrm-service/src/main/java/com/aicrm/module/dashboard/dto/SalesWorkloadStat.java`
27. `java/aicrm-service/src/main/java/com/aicrm/module/conversation/service/AiChatService.java`
28. `java/aicrm-service/src/main/java/com/aicrm/module/wecom/service/WecomSidebarService.java`

---

## 四、未达标清单与遗留问题

### 未达标清单
- 无。28 个 Controller 全部通过 4 项巡检项；GetDiagnostics 对全部改动文件检查无诊断错误（改动均为纯注解，不影响编译）。

### 遗留问题（非阻断，供后续迭代参考）
1. **ChannelEventController.receive（事件上报）未补 @ApiResponse**：Service 对重复事件返回 `false` 而非抛出 `CHANNEL_EVENT_DUPLICATED`（该错误码在 ResultCode 中定义但未被业务使用），且为回调型公开接口，按"不强行凑数"原则未补。
2. **DashboardController / WecomSidebarController / ChannelController / LogController / PlanController 未补 @ApiResponse**：均为只读接口（读操作不在本次写操作补码范围内）；Dashboard 的参数校验失败（tenantId 为空 / from 晚于 to）已在 description 中说明。
3. **错误码 403（FORBIDDEN）与 401 未在任意接口补充**：全局由登录拦截器/权限切面统一处理（GlobalExceptionHandler 统一响应），接口层无需逐一声明；如需在文档中全局展示，可后续在全局 OpenAPI 配置中补充。
4. **AI_SERVICE_ERROR(1601)/AI_TIMEOUT(1602) 未在任何接口声明**：AiChatService 对 AI 不可用采用降级为规则话术的策略（不抛异常），因此 aiReply/transfer 接口无该错误码分支。
5. **枚举取值说明**：各枚举字段（lead.status、intent、conversation.status、healthStatus、riskLevel 等）已在字段级 @Schema 的 description 中注明取值，未使用 @Schema(allowableValues) 强约束，后续如需可在实体层升级。

---

## 五、结论

本轮 4.1 全量接口文档巡检完成：158 个接口全部具备 @Operation(summary) 与参数 @Parameter 注解；4 个 Dashboard DTO 与 5 个 Service record 补齐 @Schema（约 50 个字段级说明）；22 个 Controller 的写操作接口按 Service 层真实失败分支补充 117 处 @ApiResponse 错误码。所有改动均为纯注解（含 import），未触碰业务逻辑/方法签名/默认值/权限注解，GetDiagnostics 无诊断错误，待统一 mvn 编译验证。
