package com.aicrm.module.wecom.service;

import com.aicrm.common.PageResult;
import com.aicrm.module.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企微侧边栏业务服务（3.3.5）：客户画像 / 历史会话 / 人工话术 / 产品资料
 */
public interface WecomSidebarService {

    /**
     * 客户画像：按企微外部用户 ID 聚合身份→线索→客户信息和标签
     */
    CustomerProfile profile(String externalUserId);

    /**
     * 历史会话：该企微客户关联会话列表（含最近一条消息）
     */
    List<ConversationBrief> history(String externalUserId);

    /**
     * 人工话术：按人工维护的线索意向返回固定话术
     */
    ReplySuggestion replySuggestions(String externalUserId, String intent);

    /**
     * 产品资料（快捷发送数据源）
     */
    PageResult<Product> products(String keyword, long page, long size);

    /** 客户画像 */
    @Schema(description = "客户画像")
    record CustomerProfile(
            @Schema(description = "线索 ID") Long leadId,
            @Schema(description = "线索状态：new新线索/assigned已分配/contacting跟进中/effective有效/quoted已报价/opportunity商机/lost流失") String leadStatus,
            @Schema(description = "线索意向：quote报价/sample样品/selection选型/other其他") String intent,
            @Schema(description = "客户综合评分（0-100）") Integer score,
            @Schema(description = "客户 ID") Long customerId,
            @Schema(description = "客户名称") String customerName,
            @Schema(description = "行业") String industry,
            @Schema(description = "地区") String region,
             @Schema(description = "标签列表") List<String> tags) {
    }

    /** 会话摘要 */
    @Schema(description = "会话摘要")
    record ConversationBrief(
            @Schema(description = "会话 ID") Long conversationId,
            @Schema(description = "会话类型：dm直接私信/wecom_chat企微聊天/whatsapp", example = "wecom_chat") String conversationType,
            @Schema(description = "会话状态：active进行中/transferred已转人工/closed已关闭/archived已归档") String status,
            @Schema(description = "最近一条消息时间", example = "2026-08-03 14:30:00") LocalDateTime lastMessageAt,
            @Schema(description = "最近一条消息内容") String lastMessage,
             @Schema(description = "最近一条消息发送方：customer客户/human人工/system系统") String lastSender) {
    }

    /** 人工话术 */
    @Schema(description = "按人工维护意向匹配的固定话术")
    record ReplySuggestion(
             @Schema(description = "意向：quote报价/sample样品/selection选型/other其他", example = "quote") String intent,
             @Schema(description = "回复话术列表") List<String> replies) {
    }
}
