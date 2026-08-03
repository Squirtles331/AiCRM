package com.aicrm.module.channel.client.douyin;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.client.douyin.dto.DouyinComment;
import com.aicrm.module.channel.client.douyin.dto.DouyinMessage;
import com.aicrm.module.channel.client.douyin.dto.DouyinTokenResponse;
import com.aicrm.module.channel.client.douyin.dto.DouyinUserInfo;
import com.aicrm.module.channel.client.douyin.dto.DouyinVideo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抖音开放平台 API 客户端
 * <p>
 * 能力：token 刷新、作品列表、评论拉取、评论回复、私信发送、用户信息。
 * 端点与字段以抖音开放平台当前文档为准，接入时需凭真实 appId/secret 联调确认。
 * 统一约定：响应 data.error_code == 0 视为成功，否则抛 CHANNEL_API_ERROR。
 */
@Slf4j
@Component
public class DouyinApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DouyinApiClient(DouyinProperties props, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getTimeoutMs()));
        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(factory)
                .build();
    }

    // ---------- token ----------

    /**
     * 刷新 access_token
     */
    public DouyinTokenResponse refreshAccessToken(String appId, String secret, String refreshToken) {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_key", appId);
        params.put("client_secret", secret);
        params.put("refresh_token", refreshToken);
        JsonNode data = request("POST", "/oauth/refresh_token/", null, params);
        DouyinTokenResponse resp = new DouyinTokenResponse();
        resp.setAccessToken(data.path("access_token").asText(null));
        resp.setRefreshToken(data.path("refresh_token").asText(null));
        resp.setExpiresIn(data.path("expires_in").asLong(0));
        resp.setOpenId(data.path("open_id").asText(null));
        return resp;
    }

    // ---------- 作品 ----------

    /**
     * 作品列表拉取
     */
    public List<DouyinVideo> fetchVideos(String accessToken, String openId, long cursor, int count) {
        JsonNode data = request("GET", "/api/douyin/v1/video/list/", null, Map.of(
                "access_token", accessToken,
                "open_id", openId,
                "cursor", String.valueOf(cursor),
                "count", String.valueOf(count)));
        List<DouyinVideo> list = new ArrayList<>();
        for (JsonNode node : data.path("list")) {
            DouyinVideo v = new DouyinVideo();
            v.setVideoId(node.path("item_id").asText(null));
            v.setTitle(node.path("title").asText(null));
            v.setCoverUrl(node.path("cover").asText(null));
            v.setCreateTime(node.path("create_time").asLong(0));
            v.setLikeCount(node.path("statistics").path("digg_count").asLong(0));
            v.setCommentCount(node.path("statistics").path("comment_count").asLong(0));
            v.setShareCount(node.path("statistics").path("share_count").asLong(0));
            list.add(v);
        }
        return list;
    }

    // ---------- 评论 ----------

    /**
     * 评论列表拉取
     */
    public List<DouyinComment> fetchComments(String accessToken, String videoId, long cursor, int count) {
        JsonNode data = request("GET", "/api/douyin/v1/video/comment/list/", null, Map.of(
                "access_token", accessToken,
                "video_id", videoId,
                "cursor", String.valueOf(cursor),
                "count", String.valueOf(count)));
        List<DouyinComment> list = new ArrayList<>();
        for (JsonNode node : data.path("list")) {
            DouyinComment c = new DouyinComment();
            c.setCommentId(node.path("comment_id").asText(null));
            c.setOpenId(node.path("open_id").asText(null));
            c.setContent(node.path("text").asText(null));
            c.setCreateTime(node.path("create_time").asLong(0));
            c.setReplyCommentTotal(node.path("reply_comment_total").asLong(0));
            list.add(c);
        }
        return list;
    }

    /**
     * 评论回复
     */
    public void replyComment(String accessToken, String openId, String commentId, String content) {
        request("POST", "/api/douyin/v1/video/comment/reply/", Map.of(
                "open_id", openId,
                "comment_id", commentId,
                "content", content), Map.of("access_token", accessToken));
    }

    // ---------- 私信 ----------

    /**
     * 私信发送（文本）
     */
    public void sendDirectMessage(String accessToken, String openId, String content) {
        request("POST", "/im/message/send/", Map.of(
                "open_id", openId,
                "conversation_type", "im",
                "msg_type", "text",
                "content", content), Map.of("access_token", accessToken));
    }

    // ---------- 用户 ----------

    /**
     * 用户信息获取
     */
    public DouyinUserInfo fetchUserInfo(String accessToken, String openId) {
        JsonNode data = request("GET", "/api/douyin/v1/user/info/", null, Map.of(
                "access_token", accessToken,
                "open_id", openId));
        DouyinUserInfo u = new DouyinUserInfo();
        u.setOpenId(openId);
        u.setNickname(data.path("nickname").asText(null));
        u.setAvatarUrl(data.path("avatar").asText(null));
        u.setGender(data.path("gender").asInt(0));
        u.setCity(data.path("city").asText(null));
        return u;
    }

    // ---------- 基础 ----------

    private JsonNode request(String method, String path, Object body, Map<String, String> query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(path);
        if (query != null) {
            query.forEach((k, v) -> {
                if (v != null) {
                    builder.queryParam(k, v);
                }
            });
        }
        String uri = builder.build().toUriString();
        try {
            ResponseEntity<String> resp;
            if ("GET".equalsIgnoreCase(method)) {
                resp = restClient.get().uri(uri).retrieve().toEntity(String.class);
            } else {
                resp = restClient.post().uri(uri).body(body).retrieve().toEntity(String.class);
            }
            JsonNode root = objectMapper.readTree(resp.getBody());
            JsonNode data = root.path("data");
            int errorCode = data.path("error_code").asInt(0);
            if (errorCode != 0) {
                String msg = data.path("description").asText(root.path("message").asText("抖音 API 调用失败"));
                log.warn("抖音 API 错误 code={}, msg={}", errorCode, msg);
                throw new BusinessException(ResultCode.CHANNEL_API_ERROR, msg);
            }
            return data;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("抖音 API 调用异常 path={}, err={}", path, e.getMessage());
            throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "抖音 API 调用异常: " + e.getMessage());
        }
    }
}
