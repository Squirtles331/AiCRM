package com.aicrm.stagegate;

import com.aicrm.platform.api.AccessTokenService;
import com.aicrm.platform.application.InboxService;
import com.aicrm.platform.application.OutboxDispatchService;
import com.aicrm.platform.application.OutboxEventPublisher;
import com.aicrm.platform.application.PlatformPrincipalService;
import com.aicrm.sales.application.SalesCommandService;
import com.aicrm.sales.domain.SalesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SalesApiV1IntegrationTest {
    private static final long TENANT_ID = 51L;
    private static final long ROOT_DEPARTMENT_ID = 5101L;
    private static final long SALES_DEPARTMENT_ID = 5102L;
    private static final long USER_ONE_ID = 5111L;
    private static final long USER_TWO_ID = 5112L;
    private static final long ROLE_ONE_ID = 5121L;
    private static final long LEAD_POOL_ID = 5131L;
    private static final long ADMIN_USER_ID = 5113L;
    private static final long EXITING_USER_ID = 5114L;
    private static final long ADMIN_ROLE_ID = 5122L;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.rabbitmq.listener.simple.auto-startup", () -> "false");
        registry.add("spring.rabbitmq.listener.direct.auto-startup", () -> "false");
        registry.add("aicrm.sales.recycle.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private PlatformPrincipalService platformPrincipalService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private OutboxEventPublisher outboxEventPublisher;

    @Autowired
    private OutboxDispatchService outboxDispatchService;

    @Autowired
    private InboxService inboxService;

    @Autowired
    private SalesCommandService salesCommandService;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void publishesTheVersionedSalesContract() throws Exception {
        mockMvc.perform(get("/v3/api-docs/crm-v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/leads/private']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/leads/{id}/actions/convert']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/customers/public']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/customers/{id}/actions/merge']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/handovers/batch']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/contracts']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/contracts/{id}/actions/submit-signature']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/contracts/{id}/changes']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/orders']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/orders/{id}/actions/close']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/orders/{id}/cancellations/{cancellationId}/actions/approve']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/workbench/summary']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/reports/sales-funnel']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/reports/sales-performance']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-targets']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-targets/{id}/actions/confirm-result']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/performance-score-rules']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/performance-score-rules/{id}/actions/activate']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/connectors']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/connectors/{id}/events']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/connectors/{id}/monitoring']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/connectors/{id}/outbox/{outboxEventId}/actions/retry']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/acquisition-channels']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/acquisition-channels/{id}/actions/activate']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-conversations']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-conversations/{id}/entries']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-documents']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-documents/{id}/actions/publish']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-playbooks']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/sales-playbooks/{id}/actions/publish']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/competitors']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/competitors/{id}/actions/archive']").exists());
    }

    @Test
    void doesNotExposePredecessorApiRoutes() throws Exception {
        mockMvc.perform(get("/api/leads")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/customers")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void logsInThroughTheV1PlatformIdentityEndpoint() throws Exception {
        seedSalesTenant();
        jdbcTemplate.update("update crm_user set password_hash=? where tenant_id=? and id=?",
                passwordEncoder.encode("correct-password"), TENANT_ID, USER_ONE_ID);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":51,"username":"sales-one","password":"correct-password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantId":51,"username":"sales-one","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void servesTheV1ApiOnARealHttpPort() throws Exception {
        seedSalesTenant();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token(USER_ONE_ID));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Idempotency-Key", "real-http-lead");
        ResponseEntity<String> response = restTemplate.exchange("/api/v1/leads", HttpMethod.POST,
                new HttpEntity<>("{\"name\":\"真实端口线索\",\"sourceType\":\"MANUAL\"}", headers), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.path("code").asText()).isEqualTo("OK");
        assertThat(body.path("data").path("ownerUserId").asText()).isEqualTo(String.valueOf(USER_ONE_ID));
    }

    @Test
    void authenticatesFromJwtAndEnforcesTenantAndDataScope() throws Exception {
        seedSalesTenant();
        String userOneToken = token(USER_ONE_ID);
        String userTwoToken = token(USER_TWO_ID);

        mockMvc.perform(get("/api/v1/leads/private"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.traceId").isNotEmpty());

        mockMvc.perform(get("/api/v1/leads/private")
                        .header("Authorization", bearer(userOneToken))
                        .header("X-Tenant-Id", TENANT_ID + 1))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        String createResponse = mockMvc.perform(post("/api/v1/leads")
                        .header("Authorization", bearer(userOneToken))
                        .header("X-Tenant-Id", TENANT_ID)
                        .header("Idempotency-Key", "private-lead-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"私海线索","mobile":"13800000001","email":"seller@example.com","sourceType":"MANUAL"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.data.ownerUserId").value(String.valueOf(USER_ONE_ID)))
                .andExpect(jsonPath("$.data.ownerDeptId").value(String.valueOf(ROOT_DEPARTMENT_ID)))
                .andExpect(jsonPath("$.data.mobile").value("138****0001"))
                .andExpect(jsonPath("$.data.email").value("s***@example.com"))
                .andReturn().getResponse().getContentAsString();
        String privateLeadId = objectMapper.readTree(createResponse).path("data").path("id").asText();

        mockMvc.perform(get("/api/v1/leads/{id}", privateLeadId)
                        .header("Authorization", bearer(userOneToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mobile").value("138****0001"));

        jdbcTemplate.update("insert into crm_role_field_permission "
                        + "(tenant_id,role_id,resource_type,field_name,can_view,created_by,updated_by) values "
                        + "(?,?,'LEAD','mobile',true,?,?),(?,?,'LEAD','email',true,?,?) on conflict do nothing",
                TENANT_ID, ROLE_ONE_ID, USER_ONE_ID, USER_ONE_ID,
                TENANT_ID, ROLE_ONE_ID, USER_ONE_ID, USER_ONE_ID);
        mockMvc.perform(get("/api/v1/leads/{id}", privateLeadId)
                        .header("Authorization", bearer(userOneToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.mobile").value("13800000001"))
                .andExpect(jsonPath("$.data.email").value("seller@example.com"));

        mockMvc.perform(get("/api/v1/leads/{id}", privateLeadId)
                        .header("Authorization", bearer(userTwoToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        mockMvc.perform(get("/api/v1/leads/private")
                        .header("Authorization", bearer(userOneToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].mobile").value("138****0001"));
        String userTwoPrivate = mockMvc.perform(get("/api/v1/leads/private")
                        .header("Authorization", bearer(userTwoToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(objectMapper.readTree(userTwoPrivate).path("data").path("items").findValuesAsText("id"))
                .doesNotContain(privateLeadId);
    }

    @Test
    void managesSalesDocumentsWithIdempotencyAndLifecycle() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String admin = token(ADMIN_USER_ID);
        String body = "{\"title\":\"销售话术一\",\"category\":\"PLAYBOOK\",\"content\":\"先了解客户目标\"}";
        mockMvc.perform(post("/api/v1/sales-documents").header("Authorization", bearer(token(USER_ONE_ID)))
                        .header("Idempotency-Key", "document-forbidden").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        String created = mockMvc.perform(post("/api/v1/sales-documents").header("Authorization", bearer(admin))
                        .header("Idempotency-Key", "document-create").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String documentId = objectMapper.readTree(created).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/sales-documents").header("Authorization", bearer(admin))
                        .header("Idempotency-Key", "document-create").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(documentId));
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_sales_document where tenant_id=?", Long.class, TENANT_ID)).isEqualTo(1L);
        mockMvc.perform(get("/api/v1/sales-documents").header("Authorization", bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(documentId));
        mockMvc.perform(post("/api/v1/sales-documents/{id}/actions/archive", documentId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/sales-documents/{id}/actions/publish", documentId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PUBLISHED"));
        mockMvc.perform(post("/api/v1/sales-documents/{id}/actions/archive", documentId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ARCHIVED"));
        assertThat(outboxCount("SALES_DOCUMENT", Long.parseLong(documentId))).isEqualTo(3L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='SALES_DOCUMENT' and resource_id=?", Long.class, TENANT_ID, Long.parseLong(documentId))).isEqualTo(3L);
    }

    @Test
    void managesSalesPlaybooksWithIdempotencyAndLifecycle() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String admin = token(ADMIN_USER_ID);
        String body = "{\"title\":\"首次沟通开场\",\"salesStage\":\"QUALIFICATION\",\"scenario\":\"首次电话\",\"content\":\"先确认客户当前目标\"}";
        mockMvc.perform(post("/api/v1/sales-playbooks").header("Authorization", bearer(token(USER_ONE_ID)))
                        .header("Idempotency-Key", "playbook-forbidden").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        String created = mockMvc.perform(post("/api/v1/sales-playbooks").header("Authorization", bearer(admin))
                        .header("Idempotency-Key", "playbook-create").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String playbookId = objectMapper.readTree(created).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/sales-playbooks").header("Authorization", bearer(admin))
                        .header("Idempotency-Key", "playbook-create").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(playbookId));
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_sales_playbook where tenant_id=?", Long.class, TENANT_ID)).isEqualTo(1L);
        mockMvc.perform(get("/api/v1/sales-playbooks").header("Authorization", bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].salesStage").value("QUALIFICATION"));
        mockMvc.perform(post("/api/v1/sales-playbooks/{id}/actions/archive", playbookId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/sales-playbooks/{id}/actions/publish", playbookId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PUBLISHED"));
        mockMvc.perform(post("/api/v1/sales-playbooks/{id}/actions/archive", playbookId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ARCHIVED"));
        assertThat(outboxCount("SALES_PLAYBOOK", Long.parseLong(playbookId))).isEqualTo(3L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='SALES_PLAYBOOK' and resource_id=?", Long.class, TENANT_ID, Long.parseLong(playbookId))).isEqualTo(3L);
    }

    @Test
    void managesCompetitorRegistryWithIdempotencyAndArchive() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String admin = token(ADMIN_USER_ID);
        String body = "{\"name\":\"示例竞品\",\"positioning\":\"中型企业协作平台\",\"strengths\":\"渠道覆盖广\",\"weaknesses\":\"实施周期长\"}";
        mockMvc.perform(post("/api/v1/competitors").header("Authorization", bearer(token(USER_ONE_ID))).header("Idempotency-Key", "competitor-forbidden").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        String created = mockMvc.perform(post("/api/v1/competitors").header("Authorization", bearer(admin)).header("Idempotency-Key", "competitor-create").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("ACTIVE")).andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/competitors").header("Authorization", bearer(admin)).header("Idempotency-Key", "competitor-create").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(id));
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_competitor where tenant_id=?", Long.class, TENANT_ID)).isEqualTo(1L);
        mockMvc.perform(post("/api/v1/competitors/{id}/actions/archive", id).header("Authorization", bearer(admin)).contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ARCHIVED"));
        assertThat(outboxCount("COMPETITOR", Long.parseLong(id))).isEqualTo(2L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='COMPETITOR' and resource_id=?", Long.class, TENANT_ID, Long.parseLong(id))).isEqualTo(2L);
    }

    @Test
    void managesSalesConversationLifecycleWithOwnershipAndIdempotency() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String owner = token(USER_ONE_ID);
        String customerId = createCustomerId(owner, "conversation-customer", "会话客户");
        String created = mockMvc.perform(post("/api/v1/sales-conversations").header("Authorization", bearer(owner))
                        .header("Idempotency-Key", "conversation-create").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + ",\"subject\":\"首次沟通\",\"channel\":\"PHONE\",\"summary\":\"确认需求\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();
        String conversationId = objectMapper.readTree(created).path("data").path("id").asText();
        mockMvc.perform(get("/api/v1/sales-conversations/{id}", conversationId).header("Authorization", bearer(token(USER_TWO_ID))))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/sales-conversations/{id}", conversationId).header("Authorization", bearer(token(ADMIN_USER_ID))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.conversation.customerId").value(customerId));
        String entry = "{\"direction\":\"OUTBOUND\",\"content\":\"已介绍报价方案\"}";
        mockMvc.perform(post("/api/v1/sales-conversations/{id}/entries", conversationId).header("Authorization", bearer(owner))
                        .header("Idempotency-Key", "conversation-entry").contentType(MediaType.APPLICATION_JSON).content(entry))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/sales-conversations/{id}/entries", conversationId).header("Authorization", bearer(owner))
                        .header("Idempotency-Key", "conversation-entry").contentType(MediaType.APPLICATION_JSON).content(entry))
                .andExpect(status().isOk());
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_sales_conversation_entry where tenant_id=? and conversation_id=?", Long.class, TENANT_ID, Long.parseLong(conversationId))).isEqualTo(1L);
        mockMvc.perform(post("/api/v1/sales-conversations/{id}/actions/close", conversationId).header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CLOSED"));
        mockMvc.perform(post("/api/v1/sales-conversations/{id}/entries", conversationId).header("Authorization", bearer(owner))
                        .header("Idempotency-Key", "closed-entry").contentType(MediaType.APPLICATION_JSON).content(entry))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/sales-conversations/{id}/actions/reopen", conversationId).header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("OPEN"));
        assertThat(outboxCount("SALES_CONVERSATION", Long.parseLong(conversationId))).isEqualTo(4L);
    }

    @Test
    void managesAcquisitionChannelsAndWritesImmutableLeadAttribution() throws Exception {
        seedSalesTenant();
        String admin = token(ADMIN_USER_ID);
        String created = mockMvc.perform(post("/api/v1/acquisition-channels").header("Authorization", bearer(admin))
                        .header("Idempotency-Key", "channel-form-1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"FORM_2026\",\"name\":\"官网表单\",\"sourceType\":\"FORM\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String channelId = objectMapper.readTree(created).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/leads").header("Authorization", bearer(token(USER_ONE_ID))).header("Idempotency-Key", "draft-channel-lead")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"草稿渠道\",\"sourceType\":\"FORM\",\"acquisitionChannelId\":" + channelId + "}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/v1/acquisition-channels/{id}/actions/activate", channelId).header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ACTIVE"));
        String lead = createLead(token(USER_ONE_ID), "active-channel-lead", "{\"name\":\"有效渠道\",\"sourceType\":\"FORM\",\"acquisitionChannelId\":" + channelId + "}");
        assertThat(objectMapper.readTree(lead).path("data").path("acquisitionChannelCode").asText()).isEqualTo("FORM_2026");
        mockMvc.perform(post("/api/v1/leads").header("Authorization", bearer(token(USER_ONE_ID))).header("Idempotency-Key", "wrong-source-channel")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"来源不符\",\"sourceType\":\"REFERRAL\",\"acquisitionChannelId\":" + channelId + "}"))
                .andExpect(status().isBadRequest());
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='ACQUISITION_CHANNEL'", Long.class, TENANT_ID)).isEqualTo(2L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_outbox_event where tenant_id=? and aggregate_type='ACQUISITION_CHANNEL'", Long.class, TENANT_ID)).isEqualTo(2L);
    }

    @Test
    void replaysIdempotentCreateAndClaimsOnePublicLead() throws Exception {
        seedSalesTenant();
        String userOneToken = token(USER_ONE_ID);
        String userTwoToken = token(USER_TWO_ID);
        String body = """
                {"name":"公海线索","sourceType":"FORM","publicPoolId":%d}
                """.formatted(LEAD_POOL_ID);

        String firstResponse = createLead(userOneToken, "public-lead-1", body);
        String replayResponse = createLead(userOneToken, "public-lead-1", body);
        JsonNode first = objectMapper.readTree(firstResponse);
        JsonNode replay = objectMapper.readTree(replayResponse);
        String leadId = first.path("data").path("id").asText();

        assertThat(replay.path("data").path("id").asText()).isEqualTo(leadId);
        mockMvc.perform(post("/api/v1/leads")
                        .header("Authorization", bearer(userOneToken))
                        .header("Idempotency-Key", "public-lead-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"不同请求","sourceType":"FORM","publicPoolId":%d}
                                """.formatted(LEAD_POOL_ID)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("IDEMPOTENCY_KEY_REUSED"));
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from crm_lead where tenant_id=? and lead_no=?", Long.class,
                TENANT_ID, first.path("data").path("leadNo").asText())).isEqualTo(1L);

        mockMvc.perform(post("/api/v1/leads/{id}/actions/claim", leadId)
                        .header("Authorization", bearer(userTwoToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"version":0,"publicPoolId":%d,"reason":"主动认领"}
                                """.formatted(LEAD_POOL_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownershipType").value("PRIVATE"))
                .andExpect(jsonPath("$.data.ownerUserId").value(String.valueOf(USER_TWO_ID)))
                .andExpect(jsonPath("$.data.ownerDeptId").value(String.valueOf(SALES_DEPARTMENT_ID)))
                .andExpect(jsonPath("$.data.version").value(1));

        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from crm_ownership_history where tenant_id=? and resource_id=?",
                Long.class, TENANT_ID, Long.parseLong(leadId))).isEqualTo(2L);
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from crm_audit_log where tenant_id=? and resource_id=?",
                Long.class, TENANT_ID, Long.parseLong(leadId))).isEqualTo(2L);
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from crm_outbox_event where tenant_id=? and aggregate_id=?",
                Long.class, TENANT_ID, Long.parseLong(leadId))).isEqualTo(2L);
    }

    @Test
    void managesTenantCatalogAndPublishesOnlyCompleteDraftPriceLists() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String token = token(ADMIN_USER_ID);

        String categoryResponse = mockMvc.perform(post("/api/v1/catalog/categories")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "catalog-category-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"SOFTWARE\",\"name\":\"软件\",\"sortOrder\":10}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.code").value("OK"))
                .andReturn().getResponse().getContentAsString();
        String categoryId = objectMapper.readTree(categoryResponse).path("data").path("id").asText();

        String productResponse = mockMvc.perform(post("/api/v1/catalog/products")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "catalog-product-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":" + categoryId + ",\"sku\":\"CRM-STD\",\"name\":\"CRM 标准版\",\"unit\":\"套\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.sku").value("CRM-STD"))
                .andReturn().getResponse().getContentAsString();
        String productId = objectMapper.readTree(productResponse).path("data").path("id").asText();

        String priceListResponse = mockMvc.perform(post("/api/v1/catalog/price-lists")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "catalog-price-list-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"STANDARD-2026\",\"name\":\"2026 标准价\",\"currency\":\"CNY\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String priceListId = objectMapper.readTree(priceListResponse).path("data").path("id").asText();

        mockMvc.perform(post("/api/v1/catalog/price-lists/{id}/items", priceListId)
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "catalog-price-item-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + ",\"listPrice\":1000.00,\"minimumPrice\":800.00,\"taxRate\":0.06}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.listPrice").value(1000));

        mockMvc.perform(post("/api/v1/catalog/price-lists/{id}/actions/publish", priceListId)
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.version").value(1));

        mockMvc.perform(post("/api/v1/catalog/price-lists/{id}/items", priceListId)
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "catalog-price-item-after-publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + ",\"listPrice\":1000.00}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        assertThat(outboxCount("PRICE_LIST", Long.parseLong(priceListId))).isEqualTo(2L);
    }

    @Test
    void managesOpportunityStagesLossRestartAndWinWithImmutableHistory() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String adminToken = token(ADMIN_USER_ID);
        String customerId = createCustomerId(adminToken, "opportunity-customer-1", "商机测试客户");

        String created = mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "opportunity-create-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId":%s,"name":"年度 CRM 项目","expectedAmount":120000.00,
                                 "currency":"CNY","probability":10,"expectedCloseDate":"2026-12-31"}
                                """.formatted(customerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.stage").value("DISCOVERY"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();
        String opportunityId = objectMapper.readTree(created).path("data").path("id").asText();

        mockMvc.perform(post("/api/v1/opportunities/{id}/actions/stage", opportunityId)
                        .header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":0,\"stage\":\"QUALIFICATION\",\"probability\":25}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.version").value(1));
        mockMvc.perform(post("/api/v1/opportunities/{id}/actions/lose", opportunityId)
                        .header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":1,\"reason\":\"预算暂缓\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("LOST"));
        mockMvc.perform(post("/api/v1/opportunities/{id}/actions/restart", opportunityId)
                        .header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.stage").value("DISCOVERY"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
        mockMvc.perform(post("/api/v1/opportunities/{id}/actions/win", opportunityId)
                        .header("Authorization", bearer(adminToken)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\":3}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.stage").value("CLOSED_WON"))
                .andExpect(jsonPath("$.data.probability").value(100));

        mockMvc.perform(get("/api/v1/opportunities/{id}/stage-history", opportunityId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(5));
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='OPPORTUNITY' and resource_id=?",
                Long.class, TENANT_ID, Long.parseLong(opportunityId))).isEqualTo(5L);
        assertThat(outboxCount("OPPORTUNITY", Long.parseLong(opportunityId))).isEqualTo(5L);
    }

    @Test
    void createsSubmitsAndApprovesQuoteUsingConfiguredWorkflow() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String token = token(ADMIN_USER_ID);
        String customerId = createCustomerId(token, "quote-customer-1", "报价测试客户");
        String opportunityId = objectMapper.readTree(mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "quote-opportunity-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":" + customerId + ",\"name\":\"报价商机\",\"expectedAmount\":5000,\"currency\":\"CNY\",\"probability\":30}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        String categoryId = objectMapper.readTree(mockMvc.perform(post("/api/v1/catalog/categories")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "quote-category-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"QUOTE\",\"name\":\"报价分类\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        String productId = objectMapper.readTree(mockMvc.perform(post("/api/v1/catalog/products")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "quote-product-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"categoryId\":" + categoryId + ",\"sku\":\"QUOTE-STD\",\"name\":\"报价产品\",\"unit\":\"套\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        String listId = objectMapper.readTree(mockMvc.perform(post("/api/v1/catalog/price-lists")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "quote-list-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"QUOTE-2026\",\"name\":\"报价价目表\",\"currency\":\"CNY\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        String priceItemId = objectMapper.readTree(mockMvc.perform(post("/api/v1/catalog/price-lists/{id}/items", listId)
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "quote-price-item-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":" + productId + ",\"listPrice\":1000,\"minimumPrice\":800,\"taxRate\":0.06}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/catalog/price-lists/{id}/actions/publish", listId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk());
        String definitionId = objectMapper.readTree(mockMvc.perform(post("/api/v1/approval-definitions")
                        .header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON).content("""
                                {"code":"QUOTE_STANDARD","name":"报价标准审批","resourceType":"QUOTE","nodes":[{"name":"销售经理审批","decisionMode":"ALL","approverUserIds":[%d],"condition":{"field":"totalAmount","operator":"GTE","value":1000}}]}
                                """.formatted(USER_TWO_ID)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/approval-definitions/{id}/actions/activate", definitionId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ACTIVE"));

        String quoteId = objectMapper.readTree(mockMvc.perform(post("/api/v1/quotes")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", "quote-create-1")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"opportunityId":%s,"priceListId":%s,"lines":[{"productId":%s,"priceItemId":%s,"quantity":2,"unitPrice":900,"discountRate":0.1}]}
                                """.formatted(opportunityId, listId, productId, priceItemId)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        mockMvc.perform(get("/api/v1/quotes/{id}/versions/1/lines", quoteId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].lineAmount").value(1800));
        mockMvc.perform(post("/api/v1/contracts").header("Authorization", bearer(token)).header("Idempotency-Key", "contract-before-approval")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quoteId\":" + quoteId + ",\"name\":\"不应创建的合同\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(post("/api/v1/quotes/{id}/actions/submit", quoteId).header("Authorization", bearer(token))
                        .header("Idempotency-Key", "quote-submit-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"rootVersion\":0,\"version\":0,\"approvalDefinitionCode\":\"QUOTE_STANDARD\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("SUBMITTED"));
        mockMvc.perform(post("/api/v1/quotes/{id}/actions/submit", quoteId).header("Authorization", bearer(token))
                        .header("Idempotency-Key", "quote-submit-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"rootVersion\":0,\"version\":0,\"approvalDefinitionCode\":\"QUOTE_STANDARD\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("SUBMITTED"));
        String approverToken = token(USER_TWO_ID);
        String taskResponse = mockMvc.perform(get("/api/v1/approval-tasks/pending").header("Authorization", bearer(approverToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1)).andReturn().getResponse().getContentAsString();
        String taskId = objectMapper.readTree(taskResponse).path("data").get(0).path("id").asText();
        mockMvc.perform(post("/api/v1/approval-tasks/{id}/actions/approve", taskId).header("Authorization", bearer(approverToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0,\"comment\":\"审批通过\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.quoteStatus").value("APPROVED"));
        mockMvc.perform(get("/api/v1/quotes/{id}", quoteId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("APPROVED"));
        String contractResponse = mockMvc.perform(post("/api/v1/contracts").header("Authorization", bearer(token)).header("Idempotency-Key", "contract-create-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quoteId\":" + quoteId + ",\"name\":\"CRM 年度采购合同\",\"effectiveFrom\":\"2026-01-01\",\"effectiveTo\":\"2026-12-31\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.totalAmount").value(1908)).andReturn().getResponse().getContentAsString();
        String contractId = objectMapper.readTree(contractResponse).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/contracts").header("Authorization", bearer(token)).header("Idempotency-Key", "contract-create-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quoteId\":" + quoteId + ",\"name\":\"CRM 年度采购合同\",\"effectiveFrom\":\"2026-01-01\",\"effectiveTo\":\"2026-12-31\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(contractId));
        mockMvc.perform(get("/api/v1/contracts/{id}/lines", contractId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].lineAmount").value(1800));
        mockMvc.perform(post("/api/v1/contracts/{id}/actions/submit-signature", contractId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PENDING_SIGNATURE"))
                .andExpect(jsonPath("$.data.version").value(1));
        mockMvc.perform(post("/api/v1/contracts/{id}/actions/withdraw-signature", contractId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.version").value(2));
        mockMvc.perform(post("/api/v1/contracts/{id}/actions/submit-signature", contractId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PENDING_SIGNATURE"))
                .andExpect(jsonPath("$.data.version").value(3));
        mockMvc.perform(post("/api/v1/contracts/{id}/actions/sign", contractId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":3}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("SIGNED"))
                .andExpect(jsonPath("$.data.version").value(4));
        String changeResponse = mockMvc.perform(post("/api/v1/contracts/{id}/changes", contractId).header("Authorization", bearer(token)).header("Idempotency-Key", "contract-change-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contractVersion\":4,\"reason\":\"合同名称调整\",\"proposedName\":\"CRM 修订采购合同\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String changeId = objectMapper.readTree(changeResponse).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/contracts/{id}/changes/{changeId}/actions/submit", contractId, changeId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("SUBMITTED"));
        mockMvc.perform(post("/api/v1/contracts/{id}/changes/{changeId}/actions/approve", contractId, changeId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("APPROVED"));
        mockMvc.perform(get("/api/v1/contracts/{id}", contractId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("CRM 年度采购合同"))
                .andExpect(jsonPath("$.data.version").value(4));
        String cancelledChangeId = objectMapper.readTree(mockMvc.perform(post("/api/v1/contracts/{id}/changes", contractId).header("Authorization", bearer(token)).header("Idempotency-Key", "contract-change-2")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contractVersion\":4,\"reason\":\"有效期调整\",\"proposedEffectiveTo\":\"2027-01-31\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/contracts/{id}/changes/{changeId}/actions/cancel", contractId, cancelledChangeId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CANCELLED"));
        String rejectedChangeId = objectMapper.readTree(mockMvc.perform(post("/api/v1/contracts/{id}/changes", contractId).header("Authorization", bearer(token)).header("Idempotency-Key", "contract-change-3")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contractVersion\":4,\"reason\":\"名称调整\",\"proposedName\":\"不批准合同\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/contracts/{id}/changes/{changeId}/actions/submit", contractId, rejectedChangeId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("SUBMITTED"));
        mockMvc.perform(post("/api/v1/contracts/{id}/changes/{changeId}/actions/reject", contractId, rejectedChangeId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1,\"reason\":\"不满足合同约定\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("REJECTED"));
        mockMvc.perform(post("/api/v1/contracts").header("Authorization", bearer(token)).header("Idempotency-Key", "contract-create-2")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"quoteId\":" + quoteId + ",\"name\":\"重复合同\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
        String orderResponse = mockMvc.perform(post("/api/v1/orders").header("Authorization", bearer(token)).header("Idempotency-Key", "order-create-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contractId\":" + contractId + ",\"expectedDeliveryAt\":\"2026-03-01T00:00:00Z\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.totalAmount").value(1908)).andReturn().getResponse().getContentAsString();
        String orderId = objectMapper.readTree(orderResponse).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/orders").header("Authorization", bearer(token)).header("Idempotency-Key", "order-create-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contractId\":" + contractId + ",\"expectedDeliveryAt\":\"2026-03-01T00:00:00Z\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(orderId));
        mockMvc.perform(get("/api/v1/orders/{id}/lines", orderId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].lineAmount").value(1800));
        mockMvc.perform(post("/api/v1/orders").header("Authorization", bearer(token)).header("Idempotency-Key", "order-create-2")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contractId\":" + contractId + "}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
        mockMvc.perform(post("/api/v1/contracts/{id}/actions/void", contractId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":4,\"reason\":\"存在订单时不可作废\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(post("/api/v1/orders/{id}/actions/confirm", orderId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.version").value(1));
        String cancellationResponse = mockMvc.perform(post("/api/v1/orders/{id}/actions/request-cancel", orderId).header("Authorization", bearer(token)).header("Idempotency-Key", "order-cancel-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1,\"reason\":\"客户调整采购计划\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        String cancellationId = objectMapper.readTree(cancellationResponse).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/orders/{id}/actions/request-cancel", orderId).header("Authorization", bearer(token)).header("Idempotency-Key", "order-cancel-1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1,\"reason\":\"客户调整采购计划\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(cancellationId));
        mockMvc.perform(post("/api/v1/orders/{id}/cancellations/{cancellationId}/actions/reject", orderId, cancellationId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"orderVersion\":2,\"cancellationVersion\":0,\"reason\":\"继续跟进成交\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.version").value(3));
        mockMvc.perform(post("/api/v1/orders/{id}/actions/close", orderId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":3,\"reason\":\"销售流程内部结案\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CLOSED"))
                .andExpect(jsonPath("$.data.closeReason").value("销售流程内部结案"))
                .andExpect(jsonPath("$.data.version").value(4));
        mockMvc.perform(post("/api/v1/orders/{id}/actions/close", orderId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":3,\"reason\":\"重复关闭\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
        assertThat(outboxCount("ORDER", Long.parseLong(orderId))).isEqualTo(5L);
        assertThat(outboxCount("CONTRACT", Long.parseLong(contractId))).isEqualTo(5L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='CONTRACT' and resource_id=?",
                Long.class, TENANT_ID, Long.parseLong(contractId))).isEqualTo(5L);
        assertThat(outboxCount("QUOTE", Long.parseLong(quoteId))).isEqualTo(3L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_approval_instance where tenant_id=? and resource_type='QUOTE' and resource_id=?", Long.class, TENANT_ID, Long.parseLong(quoteId))).isEqualTo(1L);

        mockMvc.perform(get("/api/v1/workbench/summary").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.openOpportunities", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.quotesSubmitted", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.quotesApproved", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.contractsSigned", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.ordersConfirmed", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.ordersClosed", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.paymentReceived").doesNotExist())
                .andExpect(jsonPath("$.data.invoiceIssued").doesNotExist())
                .andExpect(jsonPath("$.data.fulfillmentCompleted").doesNotExist())
                .andExpect(jsonPath("$.data.aftersalesTickets").doesNotExist());
        mockMvc.perform(get("/api/v1/workbench/summary?from=2026-01-02&to=2026-01-01")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mockMvc.perform(get("/api/v1/reports/sales-funnel").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.asOf").exists())
                .andExpect(jsonPath("$.data.stages[0].opportunityCount", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.paymentReceived").doesNotExist())
                .andExpect(jsonPath("$.data.fulfillmentCompleted").doesNotExist());
        mockMvc.perform(get("/api/v1/reports/sales-performance").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.owners[?(@.contractsSigned >= 1)]").isNotEmpty())
                .andExpect(jsonPath("$.data.owners[?(@.ordersConfirmed >= 1)]").isNotEmpty())
                .andExpect(jsonPath("$.data.invoiceIssued").doesNotExist());
        mockMvc.perform(get("/api/v1/reports/sales-performance?from=2026-01-02&to=2026-01-01")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String rejectedQuoteId = createQuote(token, opportunityId, listId, productId, priceItemId, "quote-create-2");
        submitQuote(token, rejectedQuoteId, "quote-submit-2");
        String rejectedTaskId = pendingTask(approverToken).path("id").asText();
        String rejectionResponse = mockMvc.perform(post("/api/v1/approval-tasks/{id}/actions/reject", rejectedTaskId).header("Authorization", bearer(approverToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0,\"comment\":\"折扣不符合要求\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("REJECTED"))
                .andExpect(jsonPath("$.data.quoteStatus").value("REJECTED")).andReturn().getResponse().getContentAsString();
        long rejectedQuoteVersion = objectMapper.readTree(rejectionResponse).path("data").path("quoteVersion").asLong();
        mockMvc.perform(post("/api/v1/quotes/{id}/versions", rejectedQuoteId).header("Authorization", bearer(token))
                        .header("Idempotency-Key", "quote-add-version-2").contentType(MediaType.APPLICATION_JSON).content("""
                                {"expectedQuoteVersion":%d,"lines":[{"productId":%s,"priceItemId":%s,"quantity":2,"unitPrice":850,"discountRate":0.15}]}
                                """.formatted(rejectedQuoteVersion, productId, priceItemId)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.currentVersionNo").value(2));
        mockMvc.perform(get("/api/v1/quotes/{id}/versions/2/lines", rejectedQuoteId).header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].unitPrice").value(850));

        String withdrawnQuoteId = createQuote(token, opportunityId, listId, productId, priceItemId, "quote-create-3");
        submitQuote(token, withdrawnQuoteId, "quote-submit-3");
        JsonNode withdrawalTask = pendingTask(approverToken);
        mockMvc.perform(post("/api/v1/quotes/{id}/actions/withdraw-approval", withdrawnQuoteId).header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"approvalInstanceId\":" + withdrawalTask.path("instanceId").asText() + ",\"instanceVersion\":0,\"comment\":\"修订报价\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("DRAFT"));
        mockMvc.perform(get("/api/v1/approval-tasks/pending").header("Authorization", bearer(approverToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void approvesOrderCancellationAsAnAtomicCrmDecision() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        long orderId = 519901L;
        long cancellationId = 519902L;
        jdbcTemplate.update("insert into crm_sales_order (id,tenant_id,order_no,contract_id,customer_id,currency,total_amount,status,confirmed_at,version,created_by,updated_by) values (?,?,?,?,?,'CNY',?,'CANCELLING',now(),2,?,?)",
                orderId, TENANT_ID, "ORD-CANCEL-APPROVE", 519903L, 519904L, 1000, ADMIN_USER_ID, ADMIN_USER_ID);
        jdbcTemplate.update("insert into crm_order_cancel (id,tenant_id,order_id,cancel_no,status,reason,request_snapshot,version,created_by,updated_by) values (?,?,?,?, 'PENDING', ?, '{}'::jsonb, 0, ?, ?)",
                cancellationId, TENANT_ID, orderId, "CAN-APPROVE", "客户取消", ADMIN_USER_ID, ADMIN_USER_ID);

        mockMvc.perform(post("/api/v1/orders/{id}/actions/close", orderId).header("Authorization", bearer(token(USER_ONE_ID)))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":2,\"reason\":\"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/orders/{id}/cancellations/{cancellationId}/actions/approve", orderId, cancellationId)
                        .header("Authorization", bearer(token(USER_ONE_ID))).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderVersion\":2,\"cancellationVersion\":0}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/orders/{id}/cancellations/{cancellationId}/actions/approve", orderId, cancellationId)
                        .header("Authorization", bearer(token(ADMIN_USER_ID))).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderVersion\":2,\"cancellationVersion\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.version").value(3));
        assertThat(jdbcTemplate.queryForObject("select status || ':' || version from crm_order_cancel where tenant_id=? and id=?", String.class, TENANT_ID, cancellationId))
                .isEqualTo("COMPLETED:1");
        assertThat(outboxCount("ORDER", orderId)).isEqualTo(1L);

        jdbcTemplate.update("insert into crm_tenant (id,name) values (?,?)", 52L, "foreign-order-tenant");
        jdbcTemplate.update("insert into crm_sales_order (id,tenant_id,order_no,contract_id,customer_id,currency,total_amount,status,version,created_by,updated_by) values (?,?,?,?,?,'CNY',?,'DRAFT',0,?,?)",
                529901L, 52L, "ORD-FOREIGN", 529902L, 529903L, 1000, ADMIN_USER_ID, ADMIN_USER_ID);
        mockMvc.perform(post("/api/v1/orders/{id}/actions/confirm", 529901L).header("Authorization", bearer(token(ADMIN_USER_ID)))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void managesCrmOnlySalesTargetsAndConfirmsImmutableResults() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String adminToken = token(ADMIN_USER_ID);
        String ruleResponse = mockMvc.perform(post("/api/v1/performance-score-rules").header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "score-rule-create-1").contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"合同签约达成率评分","metric":"SIGNED_CONTRACT_AMOUNT","bands":[
                                  {"minimumAchievementRate":0,"maximumAchievementRate":1,"score":20},
                                  {"minimumAchievementRate":1,"maximumAchievementRate":1.2,"score":80},
                                  {"minimumAchievementRate":1.2,"score":100}
                                ]}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String ruleId = objectMapper.readTree(ruleResponse).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/performance-score-rules").header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "score-rule-create-1").contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"合同签约达成率评分","metric":"SIGNED_CONTRACT_AMOUNT","bands":[
                                  {"minimumAchievementRate":0,"maximumAchievementRate":1,"score":20},
                                  {"minimumAchievementRate":1,"maximumAchievementRate":1.2,"score":80},
                                  {"minimumAchievementRate":1.2,"score":100}
                                ]}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(ruleId));
        mockMvc.perform(post("/api/v1/performance-score-rules/{id}/actions/activate", ruleId).header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ACTIVE"));
        mockMvc.perform(get("/api/v1/performance-score-rules/{id}", ruleId).header("Authorization", bearer(token(USER_ONE_ID))))
                .andExpect(status().isForbidden());
        String response = mockMvc.perform(post("/api/v1/sales-targets").header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "target-create-1").contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"2025 合同签约目标","targetUserId":5111,"metric":"SIGNED_CONTRACT_AMOUNT","periodFrom":"2025-01-01","periodTo":"2025-12-31","targetValue":100000,"scoreRuleId":%s}
                                """.formatted(ruleId)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String targetId = objectMapper.readTree(response).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/sales-targets").header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "target-create-1").contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"2025 合同签约目标","targetUserId":5111,"metric":"SIGNED_CONTRACT_AMOUNT","periodFrom":"2025-01-01","periodTo":"2025-12-31","targetValue":100000,"scoreRuleId":%s}
                                """.formatted(ruleId)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(targetId));
        mockMvc.perform(get("/api/v1/sales-targets/{id}", targetId).header("Authorization", bearer(token(USER_ONE_ID))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.targetUserId").value("5111"));
        mockMvc.perform(post("/api/v1/sales-targets/{id}/actions/activate", targetId).header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("ACTIVE"));
        mockMvc.perform(post("/api/v1/sales-targets/{id}/actions/confirm-result", targetId).header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":1}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("RESULT_CONFIRMED"))
                .andExpect(jsonPath("$.data.result.actualValue").value(0))
                .andExpect(jsonPath("$.data.result.achievementRate").value(0))
                .andExpect(jsonPath("$.data.result.score.score").value(20));
        mockMvc.perform(post("/api/v1/sales-targets/{id}/actions/confirm-result", targetId).header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"version\":2}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CONFLICT"));
        mockMvc.perform(post("/api/v1/sales-targets").header("Authorization", bearer(token(USER_ONE_ID)))
                        .header("Idempotency-Key", "target-forbidden").contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"越权目标","targetUserId":5111,"metric":"SIGNED_CONTRACT_AMOUNT","periodFrom":"2025-01-01","periodTo":"2025-12-31","targetValue":1}
                                """))
                .andExpect(status().isForbidden());
        assertThat(outboxCount("SALES_TARGET", Long.parseLong(targetId))).isEqualTo(3L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='SALES_TARGET' and resource_id=?",
                Long.class, TENANT_ID, Long.parseLong(targetId))).isEqualTo(3L);
        assertThat(outboxCount("PERFORMANCE_SCORE_RULE", Long.parseLong(ruleId))).isEqualTo(2L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_outbox_event where tenant_id=? and aggregate_type='SALES_TARGET_SCORE'", Long.class, TENANT_ID)).isEqualTo(1L);
    }

    @Test
    void publishesRetriesAndDeduplicatesReliableMessages() {
        seedSalesTenant();
        long successfulEventId = 519001L;
        long failedEventId = 519002L;
        insertOutbox(successfulEventId, "ReliabilitySucceeded");
        insertOutbox(failedEventId, "ReliabilityFailed");
        doThrow(new IllegalStateException("broker unavailable")).when(outboxEventPublisher)
                .publish(argThat(message -> message.id() == failedEventId));

        outboxDispatchService.dispatchBatch("integration-test", 100, Duration.ofMinutes(5));
        assertThat(outboxStatus(successfulEventId)).isEqualTo("PUBLISHED:0");
        assertThat(outboxStatus(failedEventId)).isEqualTo("FAILED:1");

        reset(outboxEventPublisher);
        jdbcTemplate.update("update crm_outbox_event set next_retry_at=now() where id=?", failedEventId);
        outboxDispatchService.dispatchBatch("integration-test", 100, Duration.ofMinutes(5));
        assertThat(outboxStatus(failedEventId)).isEqualTo("PUBLISHED:1");

        AtomicInteger handled = new AtomicInteger();
        assertThat(inboxService.consume(TENANT_ID, "report-projection", "message-1", "lead:1",
                "{\"leadId\":\"1\"}", handled::incrementAndGet)).isTrue();
        assertThat(inboxService.consume(TENANT_ID, "report-projection", "message-1", "lead:1",
                "{\"leadId\":\"1\"}", handled::incrementAndGet)).isFalse();
        assertThat(handled).hasValue(1);

        assertThatThrownBy(() -> inboxService.consume(TENANT_ID, "report-projection", "message-2", null,
                "{\"leadId\":\"2\"}", () -> {
                    throw new IllegalStateException("projection unavailable");
                })).isInstanceOf(IllegalStateException.class);
        assertThat(inboxService.consume(TENANT_ID, "report-projection", "message-2", null,
                "{\"leadId\":\"2\"}", handled::incrementAndGet)).isTrue();
        assertThat(jdbcTemplate.queryForObject("select status || ':' || attempt_count from crm_inbox_record "
                        + "where tenant_id=? and consumer='report-projection' and message_id='message-2'",
                String.class, TENANT_ID)).isEqualTo("COMPLETED:2");
    }

    @Test
    void allowsOnlyOneConcurrentHttpClaim() throws Exception {
        seedSalesTenant();
        String body = """
                {"name":"并发认领线索","sourceType":"FORM","publicPoolId":%d}
                """.formatted(LEAD_POOL_ID);
        String created = createLead(token(USER_ONE_ID), "concurrent-public-lead", body);
        String leadId = objectMapper.readTree(created).path("data").path("id").asText();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Integer> first = executor.submit(() -> claimStatus(token(USER_ONE_ID), leadId, ready, start));
            Future<Integer> second = executor.submit(() -> claimStatus(token(USER_TWO_ID), leadId, ready, start));
            ready.await();
            start.countDown();
            assertThat(List.of(first.get(), second.get())).containsExactlyInAnyOrder(200, 409);
        } finally {
            executor.shutdownNow();
        }
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_ownership_history "
                        + "where tenant_id=? and resource_id=? and action='CLAIM'", Long.class,
                TENANT_ID, Long.parseLong(leadId))).isEqualTo(1L);
    }

    @Test
    void convertsMergesAndHandsOverThroughApplicationApis() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String adminToken = token(ADMIN_USER_ID);
        String exitingToken = token(EXITING_USER_ID);

        String convertible = createLead(adminToken, "convertible-lead", """
                {"name":"待转换线索","sourceType":"MANUAL"}
                """);
        String leadId = objectMapper.readTree(convertible).path("data").path("id").asText();
        String converted = mockMvc.perform(post("/api/v1/leads/{id}/actions/convert", leadId)
                        .header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "convert-lead-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"version":0,"customerName":"转换生成客户","industry":"软件"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("转换生成客户"))
                .andReturn().getResponse().getContentAsString();
        String convertedCustomerId = objectMapper.readTree(converted).path("data").path("id").asText();
        mockMvc.perform(get("/api/v1/leads/{id}", leadId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONVERTED"))
                .andExpect(jsonPath("$.data.customerId").value(convertedCustomerId));

        String mergeSourceId = createCustomerId(adminToken, "merge-source", "合并源客户");
        String mergeTargetId = createCustomerId(adminToken, "merge-target", "合并目标客户");
        mockMvc.perform(post("/api/v1/customers/{id}/actions/merge", mergeSourceId)
                        .header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "merge-customers-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"version":0,"targetCustomerId":%s,"reason":"重复客户"}
                                """.formatted(mergeTargetId)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/customers/{id}", mergeSourceId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNotFound());

        String handoverLead = createLead(exitingToken, "handover-lead", """
                {"name":"待交接线索","sourceType":"MANUAL"}
                """);
        String handoverLeadId = objectMapper.readTree(handoverLead).path("data").path("id").asText();
        String handoverCustomerId = createCustomerId(exitingToken, "handover-customer", "待交接客户");
        mockMvc.perform(post("/api/v1/handovers/batch")
                        .header("Authorization", bearer(adminToken))
                        .header("Idempotency-Key", "batch-handover-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"batchNo":"EXIT-001","fromUserId":%d,"toUserId":%d,"pageSize":100,"reason":"离职交接"}
                                """.formatted(EXITING_USER_ID, USER_TWO_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.leadCount").value(1))
                .andExpect(jsonPath("$.data.customerCount").value(1))
                .andExpect(jsonPath("$.data.remainingCount").value(0));
        assertThat(jdbcTemplate.queryForObject("select owner_user_id from crm_lead where tenant_id=? and id=?",
                Long.class, TENANT_ID, Long.parseLong(handoverLeadId))).isEqualTo(USER_TWO_ID);
        assertThat(jdbcTemplate.queryForObject("select owner_user_id from crm_customer where tenant_id=? and id=?",
                Long.class, TENANT_ID, Long.parseLong(handoverCustomerId))).isEqualTo(USER_TWO_ID);
    }

    @Test
    void recyclesExpiredPrivateResourcesUsingVersionedPoolRules() throws Exception {
        seedSalesTenant();
        long recyclePoolId = 5132L;
        jdbcTemplate.update("insert into crm_public_pool "
                        + "(id,tenant_id,resource_type,code,name,auto_recycle_enabled,recycle_after_days,created_by,updated_by) "
                        + "values (?,?,'LEAD','AUTO-30','30天未跟进线索',true,30,?,?) on conflict (id) do nothing",
                recyclePoolId, TENANT_ID, USER_ONE_ID, USER_ONE_ID);
        String created = createLead(token(USER_ONE_ID), "recyclable-private-lead", """
                {"name":"超期私海线索","sourceType":"MANUAL"}
                """);
        long leadId = objectMapper.readTree(created).path("data").path("id").asLong();
        jdbcTemplate.update("update crm_lead set created_at=now()-interval '31 days' where tenant_id=? and id=?",
                TENANT_ID, leadId);

        long recycled = salesCommandService.recycleExpiredPrivateResources(
                salesRepository.findPublicPool(TENANT_ID, recyclePoolId).orElseThrow(), 100);

        assertThat(recycled).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select ownership_type || ':' || public_pool_id from crm_lead "
                        + "where tenant_id=? and id=?", String.class, TENANT_ID, leadId))
                .isEqualTo("PUBLIC:" + recyclePoolId);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_ownership_history "
                        + "where tenant_id=? and resource_id=? and action='RECYCLE' and source='SCHEDULER'",
                Long.class, TENANT_ID, leadId)).isEqualTo(1L);
    }

    @Test
    void acceptsIdempotentMarketingAndOrganizationWebhookEventsWithoutExpandingCrmFacts() throws Exception {
        seedSalesTenant();
        seedAdminAndExitingUser();
        String adminToken = token(ADMIN_USER_ID);
        String marketing = mockMvc.perform(post("/api/v1/connectors")
                        .header("Authorization", bearer(adminToken)).header("Idempotency-Key", "connector-marketing-1")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"官网获客","type":"MARKETING_WEBHOOK","operatorUserId":5111,
                                 "publicPoolId":5131,"sharedSecret":"marketing-webhook-secret"}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.type").value("MARKETING_WEBHOOK"))
                .andReturn().getResponse().getContentAsString();
        String marketingId = objectMapper.readTree(marketing).path("data").path("id").asText();
        String event = """
                {"eventId":"form-20260910-001","eventType":"LEAD_CAPTURED",
                 "lead":{"name":"营销线索","mobile":"13800000000","companyName":"示例公司","intent":"预约演示"}}
                """;
        mockMvc.perform(post("/api/v1/connectors/{id}/events", marketingId)
                        .header("X-Connector-Secret", "marketing-webhook-secret")
                        .contentType(MediaType.APPLICATION_JSON).content(event))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.outcome").value("LEAD_CREATED"))
                .andExpect(jsonPath("$.data.replayed").value(false))
                .andExpect(jsonPath("$.data.leadId").isNotEmpty());
        mockMvc.perform(post("/api/v1/connectors/{id}/events", marketingId)
                        .header("X-Connector-Secret", "marketing-webhook-secret")
                        .contentType(MediaType.APPLICATION_JSON).content(event))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.replayed").value(true));
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_lead where tenant_id=? and source_type='MARKETING'",
                Long.class, TENANT_ID)).isEqualTo(1L);
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_connector_event where tenant_id=? and connector_id=?",
                Long.class, TENANT_ID, Long.parseLong(marketingId))).isEqualTo(1L);
        mockMvc.perform(post("/api/v1/connectors/{id}/events", marketingId)
                        .header("X-Connector-Secret", "incorrect-secret-value")
                        .contentType(MediaType.APPLICATION_JSON).content(event))
                .andExpect(status().isForbidden());

        String organization = mockMvc.perform(post("/api/v1/connectors")
                        .header("Authorization", bearer(adminToken)).header("Idempotency-Key", "connector-organization-1")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"name":"组织目录通知","type":"ORGANIZATION_WEBHOOK","operatorUserId":5113,
                                 "sharedSecret":"organization-webhook-secret"}
                                """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String organizationId = objectMapper.readTree(organization).path("data").path("id").asText();
        mockMvc.perform(post("/api/v1/connectors/{id}/events", organizationId)
                        .header("X-Connector-Secret", "organization-webhook-secret")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"eventId":"directory-20260910-001","eventType":"USER_CHANGED","user":{"externalId":"u-100"}}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.outcome").value("ACCEPTED"))
                .andExpect(jsonPath("$.data.leadId").doesNotExist());
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_connector_event where tenant_id=? and connector_id=? and lead_id is null",
                Long.class, TENANT_ID, Long.parseLong(organizationId))).isEqualTo(1L);

        long outboxEventId = jdbcTemplate.queryForObject("""
                select o.id from crm_outbox_event o join crm_connector_event e
                  on e.tenant_id=o.tenant_id and e.id=o.aggregate_id
                where o.tenant_id=? and e.connector_id=? and o.aggregate_type='CONNECTOR_EVENT'
                """, Long.class, TENANT_ID, Long.parseLong(marketingId));
        jdbcTemplate.update("update crm_outbox_event set status='DEAD', retry_count=6, last_error='broker unavailable', "
                        + "dead_lettered_at=now(), updated_at=now() where tenant_id=? and id=?", TENANT_ID, outboxEventId);
        mockMvc.perform(get("/api/v1/connectors/{id}/monitoring", marketingId).header("Authorization", bearer(token(USER_ONE_ID))))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/connectors/{id}/monitoring", marketingId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.receivedEvents").value(1))
                .andExpect(jsonPath("$.data.leadsCreated").value(1)).andExpect(jsonPath("$.data.deadPublications").value(1))
                .andExpect(jsonPath("$.data.latestIssue.outboxEventId").value(String.valueOf(outboxEventId)))
                .andExpect(jsonPath("$.data.latestIssue.lastError").value("broker unavailable"));
        mockMvc.perform(post("/api/v1/connectors/{id}/outbox/{outboxEventId}/actions/retry", marketingId, outboxEventId)
                        .header("Authorization", bearer(token(USER_ONE_ID))))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/connectors/{id}/outbox/{outboxEventId}/actions/retry", marketingId, outboxEventId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.retryCount").value(0));
        assertThat(jdbcTemplate.queryForObject("select status || ':' || retry_count from crm_outbox_event where tenant_id=? and id=?",
                String.class, TENANT_ID, outboxEventId)).isEqualTo("PENDING:0");
        outboxDispatchService.dispatchBatch("connector-retry-test", 100, Duration.ofMinutes(5));
        assertThat(jdbcTemplate.queryForObject("select status from crm_outbox_event where tenant_id=? and id=?",
                String.class, TENANT_ID, outboxEventId)).isEqualTo("PUBLISHED");
        assertThat(jdbcTemplate.queryForObject("select count(*) from crm_audit_log where tenant_id=? and resource_type='OUTBOX' and resource_id=? and action='RETRY'",
                Long.class, TENANT_ID, outboxEventId)).isEqualTo(1L);
    }

    private String createLead(String token, String idempotencyKey, String body) throws Exception {
        return mockMvc.perform(post("/api/v1/leads")
                        .header("Authorization", bearer(token))
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("OK"))
                .andReturn().getResponse().getContentAsString();
    }

    private String createCustomerId(String token, String idempotencyKey, String name) throws Exception {
        String response = mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", bearer(token))
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("data").path("id").asText();
    }

    private String createQuote(String token, String opportunityId, String priceListId, String productId, String priceItemId, String idempotencyKey) throws Exception {
        String response = mockMvc.perform(post("/api/v1/quotes")
                        .header("Authorization", bearer(token)).header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"opportunityId":%s,"priceListId":%s,"lines":[{"productId":%s,"priceItemId":%s,"quantity":2,"unitPrice":900,"discountRate":0.1}]}
                                """.formatted(opportunityId, priceListId, productId, priceItemId)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("data").path("id").asText();
    }

    private void submitQuote(String token, String quoteId, String idempotencyKey) throws Exception {
        mockMvc.perform(post("/api/v1/quotes/{id}/actions/submit", quoteId).header("Authorization", bearer(token))
                        .header("Idempotency-Key", idempotencyKey).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rootVersion\":0,\"version\":0,\"approvalDefinitionCode\":\"QUOTE_STANDARD\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("SUBMITTED"));
    }

    private JsonNode pendingTask(String token) throws Exception {
        String response = mockMvc.perform(get("/api/v1/approval-tasks/pending").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("data").get(0);
    }

    private int claimStatus(String token, String leadId, CountDownLatch ready, CountDownLatch start) throws Exception {
        ready.countDown();
        start.await();
        return mockMvc.perform(post("/api/v1/leads/{id}/actions/claim", leadId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"version":0,"publicPoolId":%d,"reason":"并发认领"}
                                """.formatted(LEAD_POOL_ID)))
                .andReturn().getResponse().getStatus();
    }

    private void seedSalesTenant() {
        jdbcTemplate.update("insert into crm_tenant (id,name) values (?,?) on conflict (id) do nothing",
                TENANT_ID, "api-test-tenant");
        jdbcTemplate.update("insert into crm_department (id,tenant_id,code,name,path) values "
                        + "(?,?,?,'根部门',?),(?,?,?,'销售部',?) on conflict (id) do nothing",
                ROOT_DEPARTMENT_ID, TENANT_ID, "ROOT", "/" + ROOT_DEPARTMENT_ID,
                SALES_DEPARTMENT_ID, TENANT_ID, "SALES", "/" + ROOT_DEPARTMENT_ID + "/" + SALES_DEPARTMENT_ID);
        jdbcTemplate.update("update crm_department set parent_id=? where tenant_id=? and id=?",
                ROOT_DEPARTMENT_ID, TENANT_ID, SALES_DEPARTMENT_ID);
        jdbcTemplate.update("insert into crm_user (id,tenant_id,department_id,username,name) values "
                        + "(?,?,?,'sales-one','销售一'),(?,?,?,'sales-two','销售二') on conflict (id) do nothing",
                USER_ONE_ID, TENANT_ID, ROOT_DEPARTMENT_ID, USER_TWO_ID, TENANT_ID, SALES_DEPARTMENT_ID);
        jdbcTemplate.update("insert into crm_role (id,tenant_id,code,name,data_scope) values "
                        + "(?,?,?,'销售本人','SELF') on conflict (id) do nothing",
                ROLE_ONE_ID, TENANT_ID, "sales");
        jdbcTemplate.update("insert into crm_user_role (tenant_id,user_id,role_id) values "
                        + "(?,?,?),(?,?,?) on conflict do nothing",
                TENANT_ID, USER_ONE_ID, ROLE_ONE_ID, TENANT_ID, USER_TWO_ID, ROLE_ONE_ID);
        jdbcTemplate.update("insert into crm_public_pool "
                        + "(id,tenant_id,resource_type,code,name,created_by,updated_by) values "
                        + "(?,?,'LEAD','DEFAULT','默认线索公海',?,?) on conflict (id) do nothing",
                LEAD_POOL_ID, TENANT_ID, USER_ONE_ID, USER_ONE_ID);
    }

    private void seedAdminAndExitingUser() {
        jdbcTemplate.update("insert into crm_user (id,tenant_id,department_id,username,name) values "
                        + "(?,?,?,'api-admin','接口管理员'),(?,?,?,'exiting-sales','离职销售') on conflict (id) do nothing",
                ADMIN_USER_ID, TENANT_ID, ROOT_DEPARTMENT_ID,
                EXITING_USER_ID, TENANT_ID, SALES_DEPARTMENT_ID);
        jdbcTemplate.update("insert into crm_role (id,tenant_id,code,name,data_scope) values "
                        + "(?,?,?,'管理员','ALL') on conflict (id) do nothing",
                ADMIN_ROLE_ID, TENANT_ID, "admin");
        jdbcTemplate.update("insert into crm_user_role (tenant_id,user_id,role_id) values "
                        + "(?,?,?),(?,?,?) on conflict do nothing",
                TENANT_ID, ADMIN_USER_ID, ADMIN_ROLE_ID,
                TENANT_ID, EXITING_USER_ID, ROLE_ONE_ID);
    }

    private void insertOutbox(long id, String eventType) {
        jdbcTemplate.update("insert into crm_outbox_event "
                        + "(id,tenant_id,aggregate_type,aggregate_id,event_type,payload,occurred_at) "
                        + "values (?,?,'Reliability',?,?, '{}'::jsonb,now()) on conflict (id) do nothing",
                id, TENANT_ID, id, eventType);
    }

    /**
     * Outbox assertions must be scoped to the aggregate created by the test.
     * The integration container is shared by test methods, so tenant-wide
     * fixed counts are inherently order-dependent and can hide regressions.
     */
    private long outboxCount(String aggregateType, long aggregateId) {
        Long count = jdbcTemplate.queryForObject(
                "select count(*) from crm_outbox_event where tenant_id=? and aggregate_type=? and aggregate_id=?",
                Long.class, TENANT_ID, aggregateType, aggregateId);
        return count == null ? 0L : count;
    }

    private String outboxStatus(long eventId) {
        return jdbcTemplate.queryForObject("select status || ':' || retry_count from crm_outbox_event where id=?",
                String.class, eventId);
    }

    private String token(long userId) {
        return accessTokenService.issue(platformPrincipalService.resolve(TENANT_ID, userId));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
