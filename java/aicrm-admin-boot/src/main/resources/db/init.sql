-- ============================================================
-- AI获客销售系统（AiCRM）核心表初始化脚本
-- 数据库：PostgreSQL 16+
-- 说明：本脚本仅包含 Java 服务框架阶段的核心表（M0/M1 起步）
--       customer/contact/identity/conversation/product/competitor/
--       knowledge/workflow/risk/audit 等表在对应模块实施时补充
-- 用法：在 aicrm 库内执行（docker compose 启动时自动执行本脚本）
-- ============================================================

-- ---------- 套餐定义 ----------
CREATE TABLE IF NOT EXISTS sys_plan (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50)   NOT NULL UNIQUE,
    name            VARCHAR(100)  NOT NULL,
    seat_count      INT           NOT NULL DEFAULT 5,
    ai_quota_month  BIGINT        NOT NULL DEFAULT 100000,
    monthly_price   NUMERIC(12,2),
    description     VARCHAR(500),
    status          SMALLINT      NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    deleted         SMALLINT      NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_plan IS '套餐定义（平台级，无租户隔离）';
COMMENT ON COLUMN sys_plan.code IS '套餐编码：starter/pro/enterprise';
COMMENT ON COLUMN sys_plan.seat_count IS '坐席数上限';
COMMENT ON COLUMN sys_plan.ai_quota_month IS '月度 AI 调用额度';

-- 默认套餐（平台初始化数据）
INSERT INTO sys_plan (code, name, seat_count, ai_quota_month, monthly_price, description) VALUES
('starter',    '初创版', 5,   100000,  199,  '适合初创团队的基础套餐'),
('pro',        '专业版', 20,  500000,  999,  '适合成长型团队的标准套餐'),
('enterprise', '企业版', 100, 2000000, 3999, '适合规模化团队的高级套餐')
ON CONFLICT (code) DO NOTHING;

-- ---------- 租户 ----------
CREATE TABLE IF NOT EXISTS tenant (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    plan_code       VARCHAR(50)  NOT NULL DEFAULT 'starter',
    seat_count      INT          NOT NULL DEFAULT 5,
    ai_quota_month  BIGINT       NOT NULL DEFAULT 100000,
    expire_at       TIMESTAMPTZ,
    contact_name    VARCHAR(100),
    contact_mobile  VARCHAR(20),
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
-- 兼容已初始化库：增量补充新列（幂等）
ALTER TABLE tenant ADD COLUMN IF NOT EXISTS expire_at TIMESTAMPTZ;
ALTER TABLE tenant ADD COLUMN IF NOT EXISTS contact_name VARCHAR(100);
ALTER TABLE tenant ADD COLUMN IF NOT EXISTS contact_mobile VARCHAR(20);
COMMENT ON TABLE tenant IS '租户（企业）';
COMMENT ON COLUMN tenant.plan_code IS '套餐：starter/pro/enterprise';
COMMENT ON COLUMN tenant.expire_at IS '到期时间，为空表示长期有效';
COMMENT ON COLUMN tenant.status IS '1启用/0停用';
COMMENT ON COLUMN tenant.deleted IS '逻辑删除：0正常/1已删';

-- ---------- 坐席/员工 ----------
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    name            VARCHAR(100) NOT NULL,
    mobile          VARCHAR(20),
    email           VARCHAR(100),
    password_hash   VARCHAR(100),
    role_code       VARCHAR(50)  NOT NULL DEFAULT 'sales',
    status          SMALLINT     NOT NULL DEFAULT 1,
    last_active_at  TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE users IS '坐席/员工';
COMMENT ON COLUMN users.role_code IS 'sales/supervisor/admin';
COMMENT ON COLUMN users.password_hash IS '登录密码（BCrypt 哈希）';
CREATE INDEX idx_users_tenant ON users(tenant_id);

-- ---------- 渠道定义 ----------
CREATE TABLE IF NOT EXISTS channel (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20)  NOT NULL,
    config_schema   JSONB,
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (code)
);
COMMENT ON TABLE channel IS '渠道定义';
COMMENT ON COLUMN channel.code IS 'douyin/video_channel/tiktok/wecom/whatsapp';
COMMENT ON COLUMN channel.type IS 'short_video/social/im';

-- 预置渠道定义（幂等）
INSERT INTO channel (code, name, type, config_schema) VALUES
('douyin',        '抖音',     'short_video', '{}'),
('video_channel', '视频号',   'short_video', '{}'),
('tiktok',        'TikTok',   'short_video', '{}'),
('wecom',         '企业微信', 'im',          '{}'),
('whatsapp',      'WhatsApp', 'im',          '{}')
ON CONFLICT (code) DO NOTHING;

-- ---------- 渠道账号 ----------
CREATE TABLE IF NOT EXISTS channel_account (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    channel_id      BIGINT       NOT NULL REFERENCES channel(id),
    account_name    VARCHAR(100) NOT NULL,
    external_id     VARCHAR(100),
    auth_config     JSONB,
    health_status   SMALLINT     NOT NULL DEFAULT 1,
    risk_level      SMALLINT     NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE channel_account IS '渠道账号（企业号/矩阵号）';
COMMENT ON COLUMN channel_account.health_status IS '1正常/2受限/3封禁';
COMMENT ON COLUMN channel_account.risk_level IS '0低/1中/2高';
CREATE INDEX idx_channel_account_tenant ON channel_account(tenant_id);
CREATE INDEX idx_channel_account_channel ON channel_account(channel_id);

-- ---------- 渠道原始事件（三渠道统一入口） ----------
CREATE TABLE IF NOT EXISTS channel_event (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    channel_account_id  BIGINT       NOT NULL REFERENCES channel_account(id),
    event_type          VARCHAR(50)  NOT NULL,
    external_event_id   VARCHAR(100) NOT NULL,
    external_user_id    VARCHAR(100),
    raw_payload         JSONB,
    mapped              SMALLINT     NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, channel_account_id, external_event_id)
);
COMMENT ON TABLE channel_event IS '渠道原始事件';
COMMENT ON COLUMN channel_event.event_type IS 'comment/dm/form/click/lead';
COMMENT ON COLUMN channel_event.mapped IS '0未处理/1已映射落线索';
CREATE INDEX idx_channel_event_time ON channel_event(created_at);

-- ---------- 线索 ----------
CREATE TABLE IF NOT EXISTS lead (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    customer_id         BIGINT,
    contact_id          BIGINT,
    source_channel_id   BIGINT       REFERENCES channel_account(id),
    source_content_id   VARCHAR(100),
    source_type         VARCHAR(50),
    intent              VARCHAR(50),
    status              VARCHAR(50)  NOT NULL DEFAULT 'new',
    score               INT          NOT NULL DEFAULT 0,
    owner_id            BIGINT,
    sla_deadline        TIMESTAMPTZ,
    extra               JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE lead IS '线索（业务主节点）';
COMMENT ON COLUMN lead.intent IS 'quote/sample/selection/other';
COMMENT ON COLUMN lead.status IS 'new/assigned/contacting/effective/quoted/opportunity/lost';
COMMENT ON COLUMN lead.extra IS '抽取关键字段：场景/数量/预算/交期等';
CREATE INDEX idx_lead_tenant_status ON lead(tenant_id, status);
CREATE INDEX idx_lead_owner ON lead(owner_id);
CREATE INDEX idx_lead_created ON lead(created_at);

-- ---------- 客户公司 ----------
CREATE TABLE IF NOT EXISTS customer (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    name                VARCHAR(200) NOT NULL,
    industry            VARCHAR(100),
    scale               VARCHAR(50),
    region              VARCHAR(100),
    org_structure       JSONB,
    source              VARCHAR(50),
    enrichment_status   SMALLINT     NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE customer IS '客户公司';
COMMENT ON COLUMN customer.org_structure IS '人员架构（授权/公开数据）';
COMMENT ON COLUMN customer.source IS 'enrichment来源：公开数据/客户提供';
COMMENT ON COLUMN customer.enrichment_status IS '0未补全/1已补全';
-- 3.2.1 客户主数据：阶段 / 意向等级 / 评分（幂等增量补充）
ALTER TABLE customer ADD COLUMN IF NOT EXISTS stage VARCHAR(50) NOT NULL DEFAULT 'new';
ALTER TABLE customer ADD COLUMN IF NOT EXISTS intent_level SMALLINT NOT NULL DEFAULT 0;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS score INT NOT NULL DEFAULT 0;
COMMENT ON COLUMN customer.stage IS '客户阶段：new潜在/potential有意向/intention报价/negotiating谈判/won成交/lost流失';
COMMENT ON COLUMN customer.intent_level IS '意向等级：0-5';
COMMENT ON COLUMN customer.score IS '综合评分：0-100';
CREATE INDEX idx_customer_tenant ON customer(tenant_id);

-- ---------- 联系人 ----------
CREATE TABLE IF NOT EXISTS contact (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    customer_id         BIGINT       REFERENCES customer(id),
    name                VARCHAR(100),
    mobile              VARCHAR(20),
    email               VARCHAR(100),
    position            VARCHAR(100),
    department          VARCHAR(100),
    wecom_id            VARCHAR(100),
    whatsapp_id         VARCHAR(100),
    social_ids          JSONB,
    is_decision_maker   BOOLEAN      NOT NULL DEFAULT FALSE,
    extra               JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE contact IS '联系人';
CREATE INDEX idx_contact_tenant ON contact(tenant_id);
CREATE INDEX idx_contact_customer ON contact(customer_id);
CREATE INDEX idx_contact_mobile ON contact(mobile);

-- ---------- 身份图谱 ----------
CREATE TABLE IF NOT EXISTS identity (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    identity_type       VARCHAR(20)  NOT NULL,
    identity_value      VARCHAR(200) NOT NULL,
    status              SMALLINT     NOT NULL DEFAULT 1,
    consent             SMALLINT     NOT NULL DEFAULT 0,
    consent_time        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, identity_type, identity_value)
);
COMMENT ON TABLE identity IS '身份（手机号/邮箱/社媒/企微/WhatsApp/企业域名）';
COMMENT ON COLUMN identity.consent IS '0未同意/1已同意';

CREATE TABLE IF NOT EXISTS identity_mapping (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    identity_id         BIGINT       NOT NULL REFERENCES identity(id),
    entity_type         VARCHAR(20)  NOT NULL,
    entity_id           BIGINT       NOT NULL,
    confidence          NUMERIC(5,4) NOT NULL DEFAULT 1,
    source              VARCHAR(50),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE identity_mapping IS '身份到实体映射（lead/contact/customer）';
CREATE INDEX idx_identity_mapping_identity ON identity_mapping(identity_id);
CREATE INDEX idx_identity_mapping_entity ON identity_mapping(entity_type, entity_id);

-- ---------- 标签体系（3.2.3） ----------
CREATE TABLE IF NOT EXISTS customer_tag (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    name            VARCHAR(100) NOT NULL,
    color           VARCHAR(20),
    remark          VARCHAR(500),
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, name)
);
COMMENT ON TABLE customer_tag IS '客户标签定义';
COMMENT ON COLUMN customer_tag.status IS '1启用/0停用';
CREATE INDEX idx_customer_tag_tenant ON customer_tag(tenant_id);

CREATE TABLE IF NOT EXISTS customer_tag_rel (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    customer_id     BIGINT       NOT NULL REFERENCES customer(id),
    tag_id          BIGINT       NOT NULL REFERENCES customer_tag(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, customer_id, tag_id)
);
COMMENT ON TABLE customer_tag_rel IS '客户-标签关联';
CREATE INDEX idx_tag_rel_customer ON customer_tag_rel(customer_id);
CREATE INDEX idx_tag_rel_tag ON customer_tag_rel(tag_id);

-- 自动标签规则：condition_field 支持 score/intent_level/stage/industry/region/source
CREATE TABLE IF NOT EXISTS customer_tag_rule (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    rule_name           VARCHAR(100) NOT NULL,
    tag_id              BIGINT       NOT NULL REFERENCES customer_tag(id),
    condition_field     VARCHAR(50)  NOT NULL,
    condition_op        VARCHAR(20)  NOT NULL,
    condition_value     VARCHAR(200) NOT NULL,
    status              SMALLINT     NOT NULL DEFAULT 1,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE customer_tag_rule IS '自动标签规则';
COMMENT ON COLUMN customer_tag_rule.condition_field IS 'score/intent_level/stage/industry/region/source';
COMMENT ON COLUMN customer_tag_rule.condition_op IS 'gt/gte/lt/lte/eq/contains';
CREATE INDEX idx_tag_rule_tenant ON customer_tag_rule(tenant_id);
CREATE INDEX idx_tag_rule_tag ON customer_tag_rule(tag_id);

-- ---------- 线索分配规则（3.2.4） ----------
CREATE TABLE IF NOT EXISTS lead_assign_rule (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    rule_name           VARCHAR(100) NOT NULL,
    rule_type           VARCHAR(20)  NOT NULL,
    match_value         VARCHAR(200),
    target_user_id      BIGINT,
    target_group_ids    JSONB,
    sort                INT          NOT NULL DEFAULT 0,
    status              SMALLINT     NOT NULL DEFAULT 1,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE lead_assign_rule IS '线索自动分配规则';
COMMENT ON COLUMN lead_assign_rule.rule_type IS 'product按产品线/region按地域/round_robin轮询组';
COMMENT ON COLUMN lead_assign_rule.match_value IS '产品线或地域匹配值（product/region 规则）';
COMMENT ON COLUMN lead_assign_rule.target_user_id IS '指定销售（product/region 规则）';
COMMENT ON COLUMN lead_assign_rule.target_group_ids IS '轮询组销售 ID 数组（round_robin 规则）';
CREATE INDEX idx_assign_rule_tenant ON lead_assign_rule(tenant_id);

-- ---------- 客户跟进记录（3.2.5） ----------
CREATE TABLE IF NOT EXISTS follow_up (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    lead_id         BIGINT       REFERENCES lead(id),
    customer_id     BIGINT       REFERENCES customer(id),
    user_id         BIGINT,
    content         TEXT         NOT NULL,
    method          VARCHAR(20)  NOT NULL DEFAULT 'other',
    next_time       TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE follow_up IS '客户跟进记录';
COMMENT ON COLUMN follow_up.method IS 'phone电话/wechat微信/visit拜访/other其他';
COMMENT ON COLUMN follow_up.next_time IS '下次跟进时间';
CREATE INDEX idx_follow_up_lead ON follow_up(lead_id);
CREATE INDEX idx_follow_up_customer ON follow_up(customer_id);
CREATE INDEX idx_follow_up_time ON follow_up(created_at);

-- ---------- 产品资料（3.3.5 侧边栏快捷发送，3.4.1 产品库管理） ----------
CREATE TABLE IF NOT EXISTS product_category (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    name            VARCHAR(100) NOT NULL,
    parent_id       BIGINT       NOT NULL DEFAULT 0,
    sort            INT          NOT NULL DEFAULT 0,
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, name)
);
COMMENT ON TABLE product_category IS '产品分类';
CREATE INDEX idx_product_category_tenant ON product_category(tenant_id);

CREATE TABLE IF NOT EXISTS product (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    category_id     BIGINT       REFERENCES product_category(id),
    name            VARCHAR(200) NOT NULL,
    sku             VARCHAR(100),
    spec            VARCHAR(200),
    price           NUMERIC(12,2),
    params          JSONB,
    attachments     JSONB,
    description     TEXT,
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE product IS '产品资料';
COMMENT ON COLUMN product.params IS '产品参数（结构化键值，JSONB）';
COMMENT ON COLUMN product.attachments IS '附件列表（URL/名称数组，JSONB）';
COMMENT ON COLUMN product.status IS '1上架/0下架';
CREATE INDEX idx_product_tenant ON product(tenant_id);
-- 兼容已初始化库：增量补充新列（幂等）
ALTER TABLE product ADD COLUMN IF NOT EXISTS category_id BIGINT REFERENCES product_category(id);
ALTER TABLE product ADD COLUMN IF NOT EXISTS params JSONB;
ALTER TABLE product ADD COLUMN IF NOT EXISTS attachments JSONB;

-- ---------- 竞品库（3.4.2） ----------
CREATE TABLE IF NOT EXISTS competitor (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    name                VARCHAR(200) NOT NULL,
    category            VARCHAR(100),
    official_url        VARCHAR(500),
    description         TEXT,
    strengths           JSONB,
    weaknesses          JSONB,
    defense_tactics     JSONB,
    status              SMALLINT     NOT NULL DEFAULT 1,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE competitor IS '竞品主体档案';
COMMENT ON COLUMN competitor.strengths IS '优势列表（JSONB 数组）';
COMMENT ON COLUMN competitor.weaknesses IS '劣势列表（JSONB 数组）';
COMMENT ON COLUMN competitor.defense_tactics IS '攻防话术列表 [{scenario,tactic}]（JSONB）';
COMMENT ON COLUMN competitor.status IS '1启用/0停用';
CREATE INDEX idx_competitor_tenant ON competitor(tenant_id);

CREATE TABLE IF NOT EXISTS competitor_product (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    competitor_id       BIGINT       NOT NULL REFERENCES competitor(id),
    product_name        VARCHAR(200) NOT NULL,
    spec                VARCHAR(200),
    price               NUMERIC(12,2),
    params              JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE competitor_product IS '竞品产品参数';
COMMENT ON COLUMN competitor_product.params IS '竞品产品参数（JSONB）';
CREATE INDEX idx_competitor_product_cmp ON competitor_product(competitor_id);

-- ---------- 话术库（3.4.4） ----------
CREATE TABLE IF NOT EXISTS speech_library (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    title           VARCHAR(200) NOT NULL,
    category        VARCHAR(50)  NOT NULL DEFAULT 'general',
    content         TEXT         NOT NULL,
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE speech_library IS '话术库';
COMMENT ON COLUMN speech_library.category IS '场景分类：general通用/quote报价/selection选型/objection异议/follow跟进/opening开场';
CREATE INDEX idx_speech_library_tenant ON speech_library(tenant_id);

-- ---------- 会话与消息 ----------
CREATE TABLE IF NOT EXISTS conversation (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    channel_account_id  BIGINT,
    contact_id          BIGINT,
    lead_id             BIGINT       REFERENCES lead(id),
    conversation_type   VARCHAR(20)  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'active',
    assigned_to         BIGINT,
    last_message_at     TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE conversation IS '会话（承接阵地：企微/WhatsApp/私信）';
COMMENT ON COLUMN conversation.conversation_type IS 'dm/wecom_chat/whatsapp';
COMMENT ON COLUMN conversation.status IS 'active接待中/transferred已转人工/closed已关闭/archived已归档';
CREATE INDEX idx_conversation_lead ON conversation(lead_id);
CREATE INDEX idx_conversation_contact ON conversation(contact_id);

CREATE TABLE IF NOT EXISTS message (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    conversation_id     BIGINT       NOT NULL REFERENCES conversation(id),
    sender_type         VARCHAR(20)  NOT NULL,
    content             TEXT,
    msg_type            VARCHAR(20)  NOT NULL DEFAULT 'text',
    attachments         JSONB,
    ai_generated        BOOLEAN      NOT NULL DEFAULT FALSE,
    quoted_doc_ids      JSONB,
    raw                 JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE message IS '消息';
COMMENT ON COLUMN message.sender_type IS 'customer/ai/human/system';
CREATE INDEX idx_message_conversation ON message(conversation_id, created_at);

-- ---------- 意向与 AI 结果 ----------
CREATE TABLE IF NOT EXISTS intent_analysis (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    conversation_id     BIGINT       NOT NULL REFERENCES conversation(id),
    intent              VARCHAR(50)  NOT NULL,
    confidence          NUMERIC(5,4) NOT NULL,
    model_version       VARCHAR(50),
    evidence            TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE intent_analysis IS '意向分析结果';
COMMENT ON COLUMN intent_analysis.intent IS 'quote/sample/selection/other';
CREATE INDEX idx_intent_analysis_conversation ON intent_analysis(conversation_id);

CREATE TABLE IF NOT EXISTS extracted_field (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    lead_id             BIGINT       NOT NULL REFERENCES lead(id),
    field_key           VARCHAR(50)  NOT NULL,
    field_value         VARCHAR(500),
    confidence          NUMERIC(5,4) NOT NULL,
    source_conversation_id BIGINT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE extracted_field IS '抽取字段（scene/qty/budget/lead_time/model）';
CREATE INDEX idx_extracted_field_lead ON extracted_field(lead_id);

CREATE TABLE IF NOT EXISTS ai_generation_log (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    service             VARCHAR(50),
    prompt_hash         VARCHAR(64),
    model               VARCHAR(50),
    input_tokens        INT,
    output_tokens       INT,
    latency_ms          INT,
    status              VARCHAR(20)  NOT NULL DEFAULT 'success',
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE ai_generation_log IS 'AI 生成日志（成本核算与审计）';
CREATE INDEX idx_ai_log_tenant_time ON ai_generation_log(tenant_id, created_at);

-- ---------- 资料/文档（资料包中心） ----------
CREATE TABLE IF NOT EXISTS document (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    title               VARCHAR(200) NOT NULL,
    doc_type            VARCHAR(50)  NOT NULL,
    file_url            VARCHAR(500),
    version             INT          NOT NULL DEFAULT 1,
    status              SMALLINT     NOT NULL DEFAULT 0,
    tags                JSONB,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE document IS '资料/文档（产品手册/案例/白皮书/选型表）';
COMMENT ON COLUMN document.doc_type IS 'product_brochure/case/whitepaper/selection_table';
COMMENT ON COLUMN document.status IS '0草稿/1已发布';
CREATE INDEX idx_document_tenant_type ON document(tenant_id, doc_type);

-- ---------- 看板日聚合（V2 起预聚合使用，M1 实时统计） ----------
CREATE TABLE IF NOT EXISTS metric_daily (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    stat_date           DATE         NOT NULL,
    metric_key          VARCHAR(50)  NOT NULL,
    dimension           JSONB,
    value               NUMERIC,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, stat_date, metric_key, dimension)
);
COMMENT ON TABLE metric_daily IS '看板日聚合指标';
COMMENT ON COLUMN metric_daily.metric_key IS 'lead_count/response_time/effective_rate/intent_accuracy/conversion_rate';

-- ---------- 菜单（平台级，无租户隔离） ----------
CREATE TABLE IF NOT EXISTS sys_menu (
    id              BIGSERIAL PRIMARY KEY,
    parent_id       BIGINT       NOT NULL DEFAULT 0,
    menu_name       VARCHAR(100) NOT NULL,
    menu_type       VARCHAR(20)  NOT NULL DEFAULT 'menu',
    path            VARCHAR(200),
    component       VARCHAR(200),
    perms           VARCHAR(100),
    icon            VARCHAR(100),
    sort            INT          NOT NULL DEFAULT 0,
    visible         SMALLINT     NOT NULL DEFAULT 1,
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_menu IS '菜单/权限（平台级）';
COMMENT ON COLUMN sys_menu.menu_type IS 'dir目录/menu菜单/button按钮';
COMMENT ON COLUMN sys_menu.perms IS '按钮权限码，如 user:add';
COMMENT ON COLUMN sys_menu.visible IS '1显示/0隐藏';

-- 平台预置菜单（含按钮级权限码）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort) VALUES
(1,  0,  '工作台',   'menu',   '/dashboard',        'dashboard/index',        '',              'Dashboard', 1),
(2,  0,  '线索管理', 'menu',   '/leads',            'lead/index',             '',              'Lead',      2),
(3,  2,  '线索列表', 'menu',   '',                  '',                       'lead:list',     '',          1),
(4,  2,  '线索分配', 'button', '',                  '',                       'lead:assign',   '',          2),
(5,  0,  '客户管理', 'menu',   '/customers',        'customer/index',         '',              'Customer',  3),
(6,  0,  '会话管理', 'menu',   '/conversations',    'conversation/index',     '',              'Chat',      4),
(7,  0,  '渠道管理', 'menu',   '/channels',         'channel/index',          '',              'Channel',   5),
(8,  0,  '资料中心', 'menu',   '/documents',        'document/index',         '',              'Document',  6),
(9,  0,  '系统管理', 'dir',    '/system',           '',                       '',              'Setting',   7),
(10, 9,  '用户管理', 'menu',   '/system/users',     'system/user/index',      'user:list',     'User',      1),
(11, 9,  '角色管理', 'menu',   '/system/roles',     'system/role/index',      'role:list',     'Role',      2),
(12, 9,  '菜单管理', 'menu',   '/system/menus',     'system/menu/index',      'menu:list',     'Menu',      3),
(13, 9,  '租户管理', 'menu',   '/system/tenants',   'system/tenant/index',    'tenant:list',   'Tenant',    4),
(14, 9,  '用户新增', 'button', '',                  '',                       'user:add',      '',          1),
(15, 9,  '用户编辑', 'button', '',                  '',                       'user:edit',     '',          2),
(16, 9,  '用户删除', 'button', '',                  '',                       'user:delete',   '',          3),
(17, 9,  '重置密码', 'button', '',                  '',                       'user:reset-password', '',   4),
(18, 9,  '角色新增', 'button', '',                  '',                       'role:add',      '',          1),
(19, 9,  '角色编辑', 'button', '',                  '',                       'role:edit',     '',          2),
(20, 9,  '角色删除', 'button', '',                  '',                       'role:delete',   '',          3),
(21, 9,  '角色分配菜单', 'button', '',              '',                       'role:assign-menu', '',     4),
(22, 9,  '菜单新增', 'button', '',                  '',                       'menu:add',      '',          1),
(23, 9,  '菜单编辑', 'button', '',                  '',                       'menu:edit',     '',          2),
(24, 9,  '菜单删除', 'button', '',                  '',                       'menu:delete',   '',          3),
(25, 9,  '租户新增', 'button', '',                  '',                       'tenant:add',    '',          1),
(26, 9,  '租户编辑', 'button', '',                  '',                       'tenant:edit',   '',          2),
(27, 9,  '租户删除', 'button', '',                  '',                       'tenant:delete', '',          3),
(28, 9,  '租户启停', 'button', '',                  '',                       'tenant:status', '',          4),
(29, 9,  '字典管理', 'menu',   '/system/dicts',     'system/dict/index',      'dict:list',     'Dict',      5),
(30, 9,  '参数配置', 'menu',   '/system/configs',   'system/config/index',    'config:list',   'Config',    6),
(31, 9,  '文件管理', 'menu',   '/system/files',     'system/file/index',      'file:list',     'File',      7),
(32, 9,  '字典新增', 'button', '',                  '',                       'dict:add',      '',          1),
(33, 9,  '字典编辑', 'button', '',                  '',                       'dict:edit',     '',          2),
(34, 9,  '字典删除', 'button', '',                  '',                       'dict:delete',   '',          3),
(35, 9,  '参数编辑', 'button', '',                  '',                       'config:edit',   '',          1),
(36, 9,  '文件删除', 'button', '',                  '',                       'file:delete',   '',          1),
(37, 9,  '日志管理', 'menu',   '/system/logs',      'system/log/index',       'log:list',      'Log',       8),
(38, 9,  '定时任务', 'menu',   '/system/jobs',      'system/job/index',       'job:list',      'Job',       9),
(39, 9,  '手动执行', 'button', '',                  '',                       'job:run',       '',          1),
(40, 7,  '渠道列表', 'button', '',                  '',                       'channel:list',  '',          1),
(41, 7,  '账号新增', 'button', '',                  '',                       'channel:add',   '',          2),
(42, 7,  '账号编辑', 'button', '',                  '',                       'channel:edit',  '',          3),
(43, 7,  '账号删除', 'button', '',                  '',                       'channel:delete','',          4),
(44, 7,  '令牌刷新', 'button', '',                  '',                       'channel:token', '',          5),
(45, 5,  '客户列表', 'button', '',                  '',                       'customer:list', '',          1),
(46, 5,  '客户新增', 'button', '',                  '',                       'customer:add',  '',          2),
(47, 5,  '客户编辑', 'button', '',                  '',                       'customer:edit', '',          3),
(48, 5,  '客户删除', 'button', '',                  '',                       'customer:delete','',         4),
(49, 5,  '身份列表', 'button', '',                  '',                       'identity:list', '',          5),
(50, 5,  '身份绑定', 'button', '',                  '',                       'identity:edit', '',          6),
(51, 5,  '身份删除', 'button', '',                  '',                       'identity:delete','',         7),
(52, 5,  '线索合并', 'button', '',                  '',                       'identity:merge', '',          8),
(53, 5,  '标签列表', 'button', '',                  '',                       'tag:list',      '',          9),
(54, 5,  '标签新增', 'button', '',                  '',                       'tag:add',       '',          10),
(55, 5,  '标签编辑', 'button', '',                  '',                       'tag:edit',      '',          11),
(56, 5,  '标签删除', 'button', '',                  '',                       'tag:delete',    '',          12),
(57, 5,  '标签规则', 'button', '',                  '',                       'tag:rule',      '',          13),
(58, 5,  '跟进记录', 'button', '',                  '',                       'follow:list',   '',          14),
(59, 5,  '新增跟进', 'button', '',                  '',                       'follow:add',    '',          15),
(60, 6,  '会话列表', 'button', '',                  '',                       'conversation:list', '',       1),
(61, 6,  '会话操作', 'button', '',                  '',                       'conversation:edit', '',       2),
(62, 6,  '消息查询', 'button', '',                  '',                       'message:list',  '',          1),
(63, 6,  '消息发送', 'button', '',                  '',                       'message:add',   '',          2),
(64, 8,  '产品资料', 'menu',   '/documents/products', 'document/product/index', 'product:list', 'Product', 1),
(65, 7,  '企微侧边栏', 'button', '',                  '',                       'wecom:sidebar', '',          5),
(66, 8,  '产品新增', 'button', '',                  '',                       'product:add',   '',          1),
(67, 8,  '产品编辑', 'button', '',                  '',                       'product:edit',  '',          2),
(68, 8,  '产品删除', 'button', '',                  '',                       'product:delete','',         3),
(69, 8,  '竞品库',   'menu',   '/documents/competitors', 'document/competitor/index', 'competitor:list', 'Competitor', 1),
(70, 8,  '竞品新增', 'button', '',                  '',                       'competitor:add','',         2),
(71, 8,  '竞品编辑', 'button', '',                  '',                       'competitor:edit','',        3),
(72, 8,  '竞品删除', 'button', '',                  '',                       'competitor:delete','',      4),
(73, 8,  '话术库',   'menu',   '/documents/speeches','document/speech/index', 'speech:list', 'Speech',    1),
(74, 8,  '话术新增', 'button', '',                  '',                       'speech:add',  '',          2),
(75, 8,  '话术编辑', 'button', '',                  '',                       'speech:edit', '',          3),
(76, 8,  '话术删除', 'button', '',                  '',                       'speech:delete','',        4)
ON CONFLICT (id) DO NOTHING;

-- ---------- 角色 ----------
CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    status          SMALLINT     NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, code)
);
COMMENT ON TABLE sys_role IS '角色（租户内）';
COMMENT ON COLUMN sys_role.code IS '角色编码，如 admin/sales/supervisor';
CREATE INDEX idx_sys_role_tenant ON sys_role(tenant_id);

-- 存量租户默认角色兜底（新租户由代码初始化）
INSERT INTO sys_role (tenant_id, code, name, description)
SELECT t.id, 'admin',      '超级管理员', '拥有全部权限，由平台初始化'
FROM tenant t WHERE NOT EXISTS (SELECT 1 FROM sys_role r WHERE r.tenant_id = t.id AND r.code = 'admin');
INSERT INTO sys_role (tenant_id, code, name, description)
SELECT t.id, 'sales',      '销售',       '一线销售默认角色'
FROM tenant t WHERE NOT EXISTS (SELECT 1 FROM sys_role r WHERE r.tenant_id = t.id AND r.code = 'sales');
INSERT INTO sys_role (tenant_id, code, name, description)
SELECT t.id, 'supervisor', '主管',       '销售主管'
FROM tenant t WHERE NOT EXISTS (SELECT 1 FROM sys_role r WHERE r.tenant_id = t.id AND r.code = 'supervisor');

-- ---------- 角色-菜单关联 ----------
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    role_id         BIGINT       NOT NULL REFERENCES sys_role(id),
    menu_id         BIGINT       NOT NULL REFERENCES sys_menu(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, role_id, menu_id)
);
COMMENT ON TABLE sys_role_menu IS '角色-菜单关联';
CREATE INDEX idx_role_menu_role ON sys_role_menu(role_id);
CREATE INDEX idx_role_menu_menu ON sys_role_menu(menu_id);

-- 存量租户 admin 角色绑定全部菜单（新租户由代码初始化）
INSERT INTO sys_role_menu (tenant_id, role_id, menu_id)
SELECT r.tenant_id, r.id, m.id
FROM sys_role r, sys_menu m
WHERE r.code = 'admin'
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.tenant_id = r.tenant_id AND rm.role_id = r.id AND rm.menu_id = m.id);

-- ---------- 用户-角色关联 ----------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    user_id         BIGINT       NOT NULL REFERENCES users(id),
    role_id         BIGINT       NOT NULL REFERENCES sys_role(id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0,
    UNIQUE (tenant_id, user_id, role_id)
);
COMMENT ON TABLE sys_user_role IS '用户-角色关联';
CREATE INDEX idx_user_role_user ON sys_user_role(user_id);
CREATE INDEX idx_user_role_role ON sys_user_role(role_id);

-- ---------- 字典（平台级，无租户隔离） ----------
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id              BIGSERIAL PRIMARY KEY,
    dict_type       VARCHAR(100) NOT NULL UNIQUE,
    dict_name       VARCHAR(100) NOT NULL,
    status          SMALLINT     NOT NULL DEFAULT 1,
    remark          VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_type.dict_type IS '字典类型编码，如 lead_status';

CREATE TABLE IF NOT EXISTS sys_dict_data (
    id              BIGSERIAL PRIMARY KEY,
    dict_type       VARCHAR(100) NOT NULL,
    label           VARCHAR(100) NOT NULL,
    value           VARCHAR(100) NOT NULL,
    sort            INT          NOT NULL DEFAULT 0,
    status          SMALLINT     NOT NULL DEFAULT 1,
    remark          VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_dict_data IS '字典数据';
COMMENT ON COLUMN sys_dict_data.dict_type IS '所属字典类型编码';
COMMENT ON COLUMN sys_dict_data.label IS '字典显示文本';
COMMENT ON COLUMN sys_dict_data.value IS '字典值';
CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);

-- 默认字典（意向等级/线索状态，供前端下拉）
INSERT INTO sys_dict_type (dict_type, dict_name, remark) VALUES
('intent_level', '意向等级', '客户意向等级：高/中/低'),
('lead_status',  '线索状态', '线索生命周期状态')
ON CONFLICT (dict_type) DO NOTHING;
INSERT INTO sys_dict_data (dict_type, label, value, sort) VALUES
('intent_level', '高意向', 'high', 1),
('intent_level', '中意向', 'medium', 2),
('intent_level', '低意向', 'low', 3),
('lead_status',  '新线索', 'new', 1),
('lead_status',  '跟进中', 'following', 2),
('lead_status',  '已成交', 'won', 3),
('lead_status',  '已失效', 'lost', 4)
ON CONFLICT (id) DO NOTHING;

-- ---------- 系统参数（平台级，无租户隔离） ----------
CREATE TABLE IF NOT EXISTS sys_config (
    id              BIGSERIAL PRIMARY KEY,
    config_key      VARCHAR(100) NOT NULL UNIQUE,
    config_value    TEXT,
    config_name     VARCHAR(200),
    config_type     SMALLINT     NOT NULL DEFAULT 1,
    remark          VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_config IS '系统参数配置';
COMMENT ON COLUMN sys_config.config_type IS '1内置/2自定义';

INSERT INTO sys_config (config_key, config_value, config_name, remark) VALUES
('system.lead.autoAssign', 'true', '线索自动分配开关', 'true/false'),
('system.conversation.aiReply', 'true', 'AI 自动接待开关', 'true/false'),
('system.ai.defaultModel', 'gpt-4o-mini', '默认 AI 模型', 'AI 对话默认模型')
ON CONFLICT (config_key) DO NOTHING;

-- ---------- 文件记录（租户级） ----------
CREATE TABLE IF NOT EXISTS sys_file (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    file_name       VARCHAR(255) NOT NULL,
    file_path       VARCHAR(500) NOT NULL,
    file_size       BIGINT,
    content_type    VARCHAR(100),
    storage_type    VARCHAR(20)  NOT NULL DEFAULT 'local',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_file IS '文件记录';
COMMENT ON COLUMN sys_file.file_path IS '存储 key（本地为相对路径，MinIO 为对象名）';
COMMENT ON COLUMN sys_file.storage_type IS 'local/minio';
CREATE INDEX idx_sys_file_tenant ON sys_file(tenant_id);

-- ---------- 操作日志（租户级，审计用） ----------
CREATE TABLE IF NOT EXISTS sys_oper_log (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL REFERENCES tenant(id),
    user_id         BIGINT,
    user_name       VARCHAR(100),
    module          VARCHAR(100),
    operation       VARCHAR(200),
    method          VARCHAR(200),
    request_url     VARCHAR(300),
    http_method     VARCHAR(10),
    request_params  TEXT,
    result          SMALLINT     NOT NULL DEFAULT 1,
    error_msg       VARCHAR(1000),
    ip              VARCHAR(50),
    duration_ms     BIGINT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_oper_log IS '操作日志';
COMMENT ON COLUMN sys_oper_log.result IS '1成功/0失败';
CREATE INDEX idx_oper_log_tenant_time ON sys_oper_log(tenant_id, created_at);

-- ---------- 登录日志（租户级） ----------
CREATE TABLE IF NOT EXISTS sys_login_log (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT,
    user_id         BIGINT,
    mobile          VARCHAR(20),
    ip              VARCHAR(50),
    user_agent      VARCHAR(300),
    status          SMALLINT     NOT NULL DEFAULT 1,
    message         VARCHAR(500),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_login_log IS '登录日志';
COMMENT ON COLUMN sys_login_log.status IS '1成功/0失败';
CREATE INDEX idx_login_log_tenant_time ON sys_login_log(tenant_id, created_at);

-- ---------- 定时任务执行记录（平台级，无租户隔离） ----------
CREATE TABLE IF NOT EXISTS sys_job_log (
    id              BIGSERIAL PRIMARY KEY,
    job_code        VARCHAR(100) NOT NULL,
    job_name        VARCHAR(200),
    trigger_type    VARCHAR(20)  NOT NULL DEFAULT 'cron',
    result          SMALLINT     NOT NULL DEFAULT 1,
    error_msg       VARCHAR(1000),
    duration_ms     BIGINT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted         SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE sys_job_log IS '定时任务执行记录';
COMMENT ON COLUMN sys_job_log.trigger_type IS 'cron 定时触发 / manual 手动触发';
COMMENT ON COLUMN sys_job_log.result IS '1成功/0失败';
CREATE INDEX idx_job_log_code_time ON sys_job_log(job_code, created_at);

-- ---------- 渠道活码（租户级） ----------
CREATE TABLE IF NOT EXISTS channel_qr_code (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL REFERENCES tenant(id),
    name                VARCHAR(100) NOT NULL,
    channel_account_id  BIGINT       NOT NULL REFERENCES channel_account(id),
    scene               VARCHAR(50),
    qr_url              TEXT,
    status              SMALLINT     NOT NULL DEFAULT 1,
    scan_count          INT          NOT NULL DEFAULT 0,
    converted_count     INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted             SMALLINT     NOT NULL DEFAULT 0
);
COMMENT ON TABLE channel_qr_code IS '渠道活码：扫码引流 + 来源标记 + 统计归因';
COMMENT ON COLUMN channel_qr_code.scene IS '渠道来源标记（扫码事件携带，用于线索归因）';
COMMENT ON COLUMN channel_qr_code.status IS '1启用/0停用';
COMMENT ON COLUMN channel_qr_code.qr_url IS '扫码跳转落地地址';
