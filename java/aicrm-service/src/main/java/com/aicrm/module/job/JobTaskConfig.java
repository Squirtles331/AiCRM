package com.aicrm.module.job;

import com.aicrm.module.channel.service.ChannelAccountService;
import com.aicrm.module.channel.service.ChannelSyncService;
import com.aicrm.module.lead.service.LeadAssignService;
import com.aicrm.module.tag.service.CustomerTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定时任务定义（代码配置式注册）
 * <p>
 * 渠道接入/会话模块完成时，在此扩展真实的 token 刷新、评论拉取等任务。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobTaskConfig {

    private final ChannelAccountService channelAccountService;
    private final ChannelSyncService channelSyncService;
    private final CustomerTagService customerTagService;
    private final LeadAssignService leadAssignService;

    /** 渠道令牌刷新：每 30 分钟一次，批量刷新已过期账号 token */
    @Bean
    public JobDefinition channelTokenRefreshJob() {
        JobDefinition def = new JobDefinition();
        def.setCode("channelTokenRefresh");
        def.setName("渠道令牌刷新");
        def.setCron("0 */30 * * * ?");
        def.setTask(() -> {
            int count = channelAccountService.refreshExpiredTokens();
            log.info("[定时任务] 渠道令牌刷新完成，刷新账号数={}", count);
        });
        return def;
    }

    /** 评论/私信拉取：每 10 分钟一次，新互动落库并发布事件（3.1.7 异步链路起点） */
    @Bean
    public JobDefinition commentPullJob() {
        JobDefinition def = new JobDefinition();
        def.setCode("commentPull");
        def.setName("评论/私信拉取");
        def.setCron("0 */10 * * * ?");
        def.setTask(() -> {
            int comments = channelSyncService.syncComments();
            int dms = channelSyncService.syncDirectMessages();
            log.info("[定时任务] 渠道拉取完成：新评论事件={}, 新私信事件={}", comments, dms);
        });
        return def;
    }

    /** 自动标签规则：每 30 分钟一次，跨租户执行启用规则为客户打标（3.2.3） */
    @Bean
    public JobDefinition autoTagApplyJob() {
        JobDefinition def = new JobDefinition();
        def.setCode("autoTagApply");
        def.setName("自动标签规则执行");
        def.setCron("0 */30 * * * ?");
        def.setTask(() -> {
            int total = customerTagService.applyAllRules();
            log.info("[定时任务] 自动标签规则执行完成，累计命中客户数={}", total);
        });
        return def;
    }

    /** 线索回收重分配：每 30 分钟一次，回收超过 SLA 未跟进的线索重新分配（3.2.4） */
    @Bean
    public JobDefinition leadReassignJob() {
        JobDefinition def = new JobDefinition();
        def.setCode("leadReassign");
        def.setName("线索回收重分配");
        def.setCron("0 */30 * * * ?");
        def.setTask(() -> {
            int count = leadAssignService.reassignExpiredLeads();
            log.info("[定时任务] 线索回收重分配完成，回收条数={}", count);
        });
        return def;
    }
}
