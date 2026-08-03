package com.aicrm.module.channel.service.impl;

import com.aicrm.common.PageResult;
import com.aicrm.common.ResultCode;
import com.aicrm.common.context.TenantContext;
import com.aicrm.common.exception.BusinessException;
import com.aicrm.module.channel.entity.ChannelAccount;
import com.aicrm.module.channel.entity.ChannelEvent;
import com.aicrm.module.channel.entity.ChannelQrCode;
import com.aicrm.module.channel.mapper.ChannelAccountMapper;
import com.aicrm.module.channel.mapper.ChannelQrCodeMapper;
import com.aicrm.module.channel.service.ChannelEventService;
import com.aicrm.module.channel.service.ChannelQrCodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * 渠道活码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelQrCodeServiceImpl extends ServiceImpl<ChannelQrCodeMapper, ChannelQrCode>
        implements ChannelQrCodeService {

    private final ChannelAccountMapper channelAccountMapper;
    private final ChannelEventService channelEventService;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<ChannelQrCode> page(String keyword, Long channelAccountId, long page, long size) {
        LambdaQueryWrapper<ChannelQrCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), ChannelQrCode::getName, keyword)
                .eq(channelAccountId != null, ChannelQrCode::getChannelAccountId, channelAccountId)
                .orderByDesc(ChannelQrCode::getId);
        return PageResult.of(baseMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @Override
    public ChannelQrCode create(ChannelQrCode qrCode) {
        if (!StringUtils.hasText(qrCode.getName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "活码名称不能为空");
        }
        if (qrCode.getChannelAccountId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "引流渠道账号不能为空");
        }
        ChannelAccount account = channelAccountMapper.selectById(qrCode.getChannelAccountId());
        if (account == null) {
            throw new BusinessException(ResultCode.CHANNEL_ACCOUNT_NOT_FOUND, "引流渠道账号不存在");
        }
        if (qrCode.getStatus() == null) {
            qrCode.setStatus(1);
        }
        if (qrCode.getScanCount() == null) {
            qrCode.setScanCount(0);
        }
        if (qrCode.getConvertedCount() == null) {
            qrCode.setConvertedCount(0);
        }
        // 来源标记：未填则默认 渠道编码:账号ID
        if (!StringUtils.hasText(qrCode.getScene())) {
            qrCode.setScene(account.getChannelId() + ":" + account.getId());
        }
        qrCode.setTenantId(TenantContext.getTenantId());
        baseMapper.insert(qrCode);
        return qrCode;
    }

    @Override
    public ChannelQrCode update(ChannelQrCode qrCode) {
        if (qrCode.getId() == null || baseMapper.selectById(qrCode.getId()) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活码不存在");
        }
        baseMapper.updateById(qrCode);
        return baseMapper.selectById(qrCode.getId());
    }

    @Override
    public void delete(Long id) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活码不存在");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (baseMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活码不存在");
        }
        this.lambdaUpdate()
                .eq(ChannelQrCode::getId, id)
                .set(ChannelQrCode::getStatus, status)
                .update();
    }

    @Override
    public String recordScan(Long id, String scene) {
        ChannelQrCode qrCode = requireQrCode(id);
        if (qrCode.getStatus() == null || qrCode.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "活码已停用");
        }
        // 扫码次数自增
        this.lambdaUpdate()
                .eq(ChannelQrCode::getId, id)
                .setSql("scan_count = scan_count + 1")
                .update();
        // 记录扫码事件：携带 scene 来源标记（3.2 客户身份归一按 scene 归因）
        try {
            ChannelEvent event = new ChannelEvent();
            event.setTenantId(qrCode.getTenantId());
            event.setChannelAccountId(qrCode.getChannelAccountId());
            event.setEventType("qr");
            event.setExternalEventId(UUID.randomUUID().toString());
            event.setExternalUserId(scene);
            event.setRawPayload(objectMapper.writeValueAsString(
                    Map.of("qrCodeId", id, "scene", scene == null ? qrCode.getScene() : scene)));
            event.setMapped(0);
            channelEventService.receiveEvent(event);
        } catch (Exception e) {
            log.warn("记录活码扫码事件失败 qrCodeId={}, err={}", id, e.getMessage());
        }
        return qrCode.getQrUrl();
    }

    private ChannelQrCode requireQrCode(Long id) {
        ChannelQrCode qrCode = baseMapper.selectById(id);
        if (qrCode == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活码不存在");
        }
        return qrCode;
    }
}
