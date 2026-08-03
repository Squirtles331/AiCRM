# AiCRM 生产环境配置校验单

> 任务编号：5.1 ｜ 责任人：技术负责人 ｜ 交付物：生产环境配置校验单
> 验收标准：生产环境无法访问任何文档地址，无信息泄露风险

## 1. 校验结论

| 校验项 | 预期 | 实际 | 结论 |
| --- | --- | --- | --- |
| springdoc api-docs（/v3/api-docs） | 关闭 | `application-prod.yml` 已配置 `enabled: false` | ✅ 通过 |
| springdoc swagger-ui（/swagger-ui.html、/swagger-ui/**） | 关闭 | `application-prod.yml` 已配置 `enabled: false` | ✅ 通过 |
| Knife4j UI（/doc.html、/webjars/**） | 关闭 | `application-prod.yml` 已配置 `knife4j.enable: false`（**本次校验补充**） | ✅ 通过 |
| Knife4j 字段级 Mock | 关闭 | `knife4j.setting.mock: false`（**本次校验补充**） | ✅ 通过 |
| 网关/反向代理层拦截文档路径 | 拦截 | 需按第 3 节配置网关规则 | ⚠️ 待部署执行 |
| 环境变量覆盖风险 | 隔离 | 生产环境禁止设置 `AICRM_DOC_ENABLED=true`（见第 2 节说明） | ⚠️ 部署注意 |

**总体结论**：应用层文档开关已全部关闭并固化到 `application-prod.yml`；网关层需按第 3 节规则配置后满足验收标准。

## 2. 配置核查详情

### 2.1 应用层配置（本次已修正）

`java/aicrm-admin-boot/src/main/resources/application-prod.yml`：

```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
knife4j:
  enable: false
  setting:
    mock: false
```

修复说明：原 prod 配置只关闭了 springdoc 的 api-docs 与 swagger-ui，未关闭 Knife4j 增强 UI（`knife4j.enable` 默认继承自 `application.yml` 的 `true`），生产环境存在 `/doc.html` 可访问面，已补充关闭。

### 2.2 环境变量覆盖风险（重要）

`application.yml` 中 springdoc 开关带环境变量默认值：

```yaml
springdoc:
  api-docs:
    enabled: ${AICRM_DOC_ENABLED:true}
```

外部化配置优先级高于 profile 配置文件，因此 **生产服务器若设置了 `AICRM_DOC_ENABLED=true` 环境变量，将覆盖 `application-prod.yml` 的关闭配置，导致文档重新开放**。

部署要求：
- 生产环境**不得**设置 `AICRM_DOC_ENABLED` 环境变量（或强制为 `false`）；
- 发布检查单中增加该环境变量核查项；
- 如项目需彻底杜绝此类覆盖，可在生产 jar 启动脚本中显式追加 `--springdoc.api-docs.enabled=false --springdoc.swagger-ui.enabled=false` 命令行参数（命令行参数优先级最高）。

## 3. 网关 / 反向代理拦截规则（Nginx 示例）

生产环境必须在前置网关（Nginx / 云 WAF / API 网关）拦截所有文档相关路径，作为纵深防御：

```nginx
# 禁止访问所有接口文档相关路径
location ~ ^/(doc\.html|v3/api-docs|v3/api-docs/.*|swagger-ui|swagger-ui\.html|swagger-ui/.*|webjars/.*|favicon\.ico|error) {
    deny all;
    return 403;
}
```

## 4. 部署后自检命令

应用部署到生产环境后，由运维/测试执行以下命令验证（预期全部返回 403/404/400，不应返回 200）：

```bash
curl -s -o /dev/null -w "%{http_code}" https://<prod-domain>/doc.html
curl -s -o /dev/null -w "%{http_code}" https://<prod-domain>/v3/api-docs
curl -s -o /dev/null -w "%{http_code}" https://<prod-domain>/swagger-ui.html
curl -s -o /dev/null -w "%{http_code}" https://<prod-domain>/webjars/springfox-swagger-ui/swagger-ui.html
```

任一返回 200 即视为校验不通过，需立即修复。

## 5. 遗留说明

- 本校验单基于代码配置核查完成；待生产环境真正部署时，需执行第 4 节自检命令并勾选确认，回填"部署后实际结果"。
- 渠道 Webhook 回调接口（`/api/channel/events`）为无鉴权入口，属业务设计（渠道平台回调），如生产暴露需在网关侧限制来源 IP 白名单。
