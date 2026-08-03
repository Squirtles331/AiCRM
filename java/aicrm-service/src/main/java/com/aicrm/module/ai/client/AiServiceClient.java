package com.aicrm.module.ai.client;

import com.aicrm.module.ai.dto.AiAnswerRequest;
import com.aicrm.module.ai.dto.AiAnswerResponse;
import com.aicrm.module.ai.dto.AiChatMessage;
import com.aicrm.module.ai.dto.AiExtractRequest;
import com.aicrm.module.ai.dto.AiExtractResponse;
import com.aicrm.module.ai.dto.AiIntentRequest;
import com.aicrm.module.ai.dto.AiIntentResponse;
import com.aicrm.module.ai.dto.AiSummaryRequest;
import com.aicrm.module.ai.dto.AiSummaryResponse;
import com.aicrm.module.intent.entity.AiGenerationLog;
import com.aicrm.module.intent.mapper.AiGenerationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

/**
 * AI 能力服务客户端（调用 Python FastAPI 服务）
 * <p>
 * 降级策略：AI 服务不可用/超时/异常时返回 null 并记录 ai_generation_log（status=failed），
 * 由调用方决定降级行为（规则回复 / 转人工）。
 */
@Slf4j
@Component
public class AiServiceClient {

    private final RestClient restClient;
    private final AiGenerationLogMapper generationLogMapper;

    public AiServiceClient(AiServiceProperties props, AiGenerationLogMapper generationLogMapper) {
        this.generationLogMapper = generationLogMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getTimeoutMs()));
        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(factory)
                .build();
    }

    /** 意向分类：quote/sample/selection/other */
    public AiIntentResponse classifyIntent(long tenantId, long conversationId, List<AiChatMessage> messages) {
        AiIntentRequest req = new AiIntentRequest(tenantId, conversationId, messages);
        return post("/ai/intent", req, AiIntentResponse.class, tenantId);
    }

    /** RAG 问答 */
    public AiAnswerResponse answer(AiAnswerRequest req) {
        return post("/ai/answer", req, AiAnswerResponse.class, req.tenantId());
    }

    /** 实体抽取（报价前信息收集） */
    public AiExtractResponse extract(AiExtractRequest req) {
        return post("/ai/extract", req, AiExtractResponse.class, req.tenantId());
    }

    /** 对话摘要（转人工交接包） */
    public AiSummaryResponse summary(AiSummaryRequest req) {
        return post("/ai/summary", req, AiSummaryResponse.class, req.tenantId());
    }

    private <T> T post(String path, Object body, Class<T> responseType, long tenantId) {
        long start = System.currentTimeMillis();
        try {
            T resp = restClient.post().uri(path).body(body).retrieve().body(responseType);
            saveLog(tenantId, path, start, "success");
            return resp;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.warn("AI 服务调用失败 path={}, cost={}ms, err={}", path, cost, e.getMessage());
            saveLog(tenantId, path, start, "failed");
            return null;
        }
    }

    private void saveLog(long tenantId, String path, long start, String status) {
        try {
            AiGenerationLog log = new AiGenerationLog();
            log.setTenantId(tenantId);
            log.setService(path);
            log.setLatencyMs((int) (System.currentTimeMillis() - start));
            log.setStatus(status);
            generationLogMapper.insert(log);
        } catch (Exception ex) {
            log.warn("AI 调用日志落库失败: {}", ex.getMessage());
        }
    }
}
