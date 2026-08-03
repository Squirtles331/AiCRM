package com.aicrm.module.channel.client.wecom;

import com.aicrm.common.ResultCode;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.client.wecom.dto.WecomCorpTag;
import com.aicrm.module.channel.client.wecom.dto.WecomExternalContact;
import com.aicrm.module.channel.client.wecom.dto.WecomTokenResponse;
import com.aicrm.module.channel.client.wecom.dto.WecomUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业微信 API 客户端
 * <p>
 * 能力：access_token、客户信息、客户标签、消息推送、侧边栏鉴权。
 * 端点为企业微信官方公开接口，接入时凭真实 corpid/secret 联调。
 * 统一约定：响应 errcode == 0 视为成功，否则抛 CHANNEL_API_ERROR。
 */
@Slf4j
@Component
public class WecomApiClient {

    /** 侧边栏 OAuth 授权地址 */
    private static final String OAUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public WecomApiClient(WecomProperties props, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getTimeoutMs()));
        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(factory)
                .build();
    }

    /**
     * 获取 access_token（corpid + secret）
     */
    public WecomTokenResponse getAccessToken(String corpId, String secret) {
        JsonNode data = request("GET", "/cgi-bin/gettoken", null, Map.of(
                "corpid", corpId,
                "corpsecret", secret));
        WecomTokenResponse resp = new WecomTokenResponse();
        resp.setAccessToken(data.path("access_token").asText(null));
        resp.setExpiresIn(data.path("expires_in").asLong(7200));
        return resp;
    }

    /**
     * 客户信息获取（客户添加/消息事件后同步画像）
     */
    public WecomExternalContact getExternalContact(String accessToken, String externalUserId) {
        JsonNode data = request("GET", "/cgi-bin/externalcontact/get", null, Map.of(
                "access_token", accessToken,
                "external_userid", externalUserId));
        JsonNode contact = data.path("external_contact");
        WecomExternalContact c = new WecomExternalContact();
        c.setExternalUserId(externalUserId);
        c.setName(contact.path("name").asText(null));
        c.setAvatarUrl(contact.path("avatar").asText(null));
        c.setCorpName(contact.path("corp_name").asText(null));
        List<String> tags = new ArrayList<>();
        for (JsonNode tag : contact.path("tag_id")) {
            tags.add(tag.asText());
        }
        c.setTags(tags);
        return c;
    }

    /**
     * 企业客户标签列表
     */
    public List<WecomCorpTag> getCorpTagList(String accessToken) {
        JsonNode data = request("GET", "/cgi-bin/externalcontact/get_corp_tag_list", null,
                Map.of("access_token", accessToken));
        List<WecomCorpTag> list = new ArrayList<>();
        for (JsonNode group : data.path("tag_group")) {
            for (JsonNode tag : group.path("tag")) {
                WecomCorpTag t = new WecomCorpTag();
                t.setTagId(tag.path("id").asText(null));
                t.setTagName(tag.path("name").asText(null));
                t.setGroupId(group.path("group_id").asText(null));
                t.setGroupName(group.path("group_name").asText(null));
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 文本消息推送（服务号/应用消息）
     */
    public void sendTextMessage(String accessToken, String agentId, String touser, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("touser", touser);
        body.put("msgtype", "text");
        body.put("agentid", agentId);
        body.put("text", Map.of("content", content));
        request("POST", "/cgi-bin/message/send", body, Map.of("access_token", accessToken));
    }

    /**
     * 侧边栏 OAuth 鉴权 URL（前端跳转授权）
     */
    public String buildSidebarAuthUrl(String corpId, String redirectUri, String state) {
        String encoded = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return OAUTH_URL + "?appid=" + corpId
                + "&redirect_uri=" + encoded
                + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
    }

    /**
     * 用 code 换取成员身份（侧边栏鉴权回调）
     */
    public WecomUserInfo getUserInfoByCode(String accessToken, String code) {
        JsonNode data = request("GET", "/cgi-bin/auth/getuserinfo", null, Map.of(
                "access_token", accessToken,
                "code", code));
        WecomUserInfo u = new WecomUserInfo();
        u.setUserId(data.path("userid").asText(null));
        u.setName(data.path("user_name").asText(null));
        u.setMobile(data.path("mobile").asText(null));
        return u;
    }

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
            int errCode = root.path("errcode").asInt(0);
            if (errCode != 0) {
                String msg = root.path("errmsg").asText("企微 API 调用失败");
                log.warn("企微 API 错误 code={}, msg={}", errCode, msg);
                throw new BusinessException(ResultCode.CHANNEL_API_ERROR, msg);
            }
            return root;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("企微 API 调用异常 path={}, err={}", path, e.getMessage());
            throw new BusinessException(ResultCode.CHANNEL_API_ERROR, "企微 API 调用异常: " + e.getMessage());
        }
    }
}
