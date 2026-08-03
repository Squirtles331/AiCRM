package com.aicrm.module.channel.client.video;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.client.video.dto.VideoChannelComment;
import com.aicrm.module.channel.client.video.dto.VideoChannelMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视频号开放平台 API 客户端
 * <p>
 * access_token 通过微信通用 stable_token 接口获取；评论拉取/私信收发依赖视频号开放平台业务接口，
 * 路径待按官方文档联调确认（接入时凭真实 appId/secret 验证后调整路径与字段）。
 * 统一约定：响应 errcode == 0 视为成功，否则抛 CHANNEL_API_ERROR。
 */
@Slf4j
@Component
public class VideoChannelApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public VideoChannelApiClient(VideoChannelProperties props, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getTimeoutMs()));
        this.restClient = RestClient.builder()
                .baseUrl(props.getApiUrl())
                .requestFactory(factory)
                .build();
    }

    /**
     * 获取 access_token（appid + secret，stable_token 接口）
     */
    public String getAccessToken(String appId, String secret) {
        try {
            String body = restClient.post()
                    .uri("/cgi-bin/stable_token")
                    .body(Map.of("grant_type", "client_credential", "appid", appId, "secret", secret))
                    .retrieve().body(String.class);
            JsonNode root = objectMapper.readTree(body);
            int errCode = root.path("errcode").asInt(0);
            if (errCode != 0) {
                throw new BusinessException(ResultCode.CHANNEL_API_ERROR,
                        root.path("errmsg").asText("视频号 token 获取失败"));
            }
            return root.path("access_token").asText(null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("视频号 token 获取异常: {}", e.getMessage());
            throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "视频号 token 获取异常: " + e.getMessage());
        }
    }

    /**
     * 评论拉取
     *
     * @param accessToken 视频号 access_token
     * @param videoId     作品 ID
     */
    public List<VideoChannelComment> fetchComments(String accessToken, String videoId) {
        // TODO 联调：填写视频号开放平台评论列表接口路径与参数（以官方文档为准）
        JsonNode data = request("/channels/video/comment/list", Map.of(
                "access_token", accessToken,
                "video_id", videoId));
        List<VideoChannelComment> list = new ArrayList<>();
        for (JsonNode node : data.path("list")) {
            VideoChannelComment c = new VideoChannelComment();
            c.setCommentId(node.path("comment_id").asText(null));
            c.setOpenId(node.path("openid").asText(null));
            c.setContent(node.path("content").asText(null));
            c.setCreateTime(node.path("create_time").asLong(0));
            list.add(c);
        }
        return list;
    }

    /**
     * 私信发送
     */
    public void sendDirectMessage(String accessToken, String openId, String content) {
        // TODO 联调：填写视频号开放平台私信发送接口路径与参数（以官方文档为准）
        request("/channels/video/message/send", Map.of(
                "access_token", accessToken,
                "openid", openId,
                "content", content));
    }

    private JsonNode request(String path, Map<String, String> query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);
        if (query != null) {
            query.forEach((k, v) -> {
                if (v != null) {
                    builder.queryParam(k, v);
                }
            });
        }
        try {
            ResponseEntity<String> resp = restClient.post()
                    .uri(builder.build().toUriString())
                    .retrieve().toEntity(String.class);
            JsonNode root = objectMapper.readTree(resp.getBody());
            int errCode = root.path("errcode").asInt(0);
            if (errCode != 0) {
                throw new BusinessException(ResultCode.CHANNEL_API_ERROR, root.path("errmsg").asText("视频号 API 调用失败"));
            }
            return root;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("视频号 API 调用异常 path={}, err={}", path, e.getMessage());
            throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "视频号 API 调用异常: " + e.getMessage());
        }
    }
}
