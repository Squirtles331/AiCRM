package com.aicrm.module.channel.service.impl;

import com.aicrm.module.channel.entity.Channel;
import com.aicrm.module.channel.mapper.ChannelMapper;
import com.aicrm.module.channel.service.ChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠道定义服务实现
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel>
        implements ChannelService {

    @Override
    public List<Channel> listEnabled() {
        return this.lambdaQuery()
                .eq(Channel::getStatus, 1)
                .orderByAsc(Channel::getId)
                .list();
    }
}
