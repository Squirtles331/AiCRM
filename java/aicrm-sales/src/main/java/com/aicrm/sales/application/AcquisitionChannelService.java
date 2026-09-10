package com.aicrm.sales.application;

import com.aicrm.kernel.error.DomainException;
import com.aicrm.kernel.error.ErrorCode;
import com.aicrm.kernel.event.DomainEvent;
import com.aicrm.kernel.id.IdGenerator;
import com.aicrm.kernel.security.Actor;
import com.aicrm.kernel.security.TraceContext;
import com.aicrm.platform.application.AuditLogService;
import com.aicrm.platform.application.IdempotencyService;
import com.aicrm.platform.application.OutboxService;
import com.aicrm.sales.domain.channel.AcquisitionChannel;
import com.aicrm.sales.domain.channel.AcquisitionChannelRepository;
import com.aicrm.sales.domain.channel.AcquisitionChannelStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Map;

@Service
public class AcquisitionChannelService {
    private final AcquisitionChannelRepository repository; private final IdGenerator ids; private final IdempotencyService idempotency;
    private final AuditLogService audit; private final OutboxService outbox; private final ObjectMapper json;
    public AcquisitionChannelService(AcquisitionChannelRepository repository, IdGenerator ids, IdempotencyService idempotency, AuditLogService audit, OutboxService outbox, ObjectMapper json) {
        this.repository=repository; this.ids=ids; this.idempotency=idempotency; this.audit=audit; this.outbox=outbox; this.json=json;
    }
    @Transactional public AcquisitionChannel create(Actor actor, AcquisitionChannelCommands.Create command, String key) {
        require(actor,"channel:manage");
        return idempotency.execute(actor,"channel:create",key,command,AcquisitionChannel.class,()->{
            Instant now=Instant.now(); AcquisitionChannel channel=new AcquisitionChannel(ids.nextId(),actor.tenantId(),limited(command.code(),"渠道编码",64),limited(command.name(),"渠道名称",200),limited(command.sourceType(),"来源类型",64),AcquisitionChannelStatus.DRAFT,0,now,now);
            channel=repository.insert(channel,actor.userId()); journal(actor,"CREATE",null,channel,"创建获客渠道","AcquisitionChannelCreated"); return channel;
        });
    }
    public AcquisitionChannel get(Actor actor,long id) { requireRead(actor); return channel(actor,id); }
    @Transactional public AcquisitionChannel activate(Actor actor,long id,AcquisitionChannelCommands.Versioned command) { return transition(actor,id,command,AcquisitionChannelStatus.ACTIVE,"启用获客渠道","AcquisitionChannelActivated"); }
    @Transactional public AcquisitionChannel disable(Actor actor,long id,AcquisitionChannelCommands.Versioned command) { return transition(actor,id,command,AcquisitionChannelStatus.DISABLED,"停用获客渠道","AcquisitionChannelDisabled"); }
    private AcquisitionChannel transition(Actor actor,long id,AcquisitionChannelCommands.Versioned command,AcquisitionChannelStatus target,String reason,String event) {
        require(actor,"channel:manage"); AcquisitionChannel before=channel(actor,id);
        if (before.status()==target || !repository.changeStatus(actor.tenantId(),id,command.version(),before.status(),target,actor.userId())) throw new DomainException(ErrorCode.CONFLICT,"渠道状态已变化，请刷新后重试");
        AcquisitionChannel after=channel(actor,id); journal(actor,"STATUS_CHANGE",before,after,reason,event); return after;
    }
    private AcquisitionChannel channel(Actor actor,long id) { return repository.find(actor.tenantId(),id).orElseThrow(()->new DomainException(ErrorCode.NOT_FOUND,"获客渠道不存在")); }
    private void requireRead(Actor actor) { if (!actor.hasPermission("channel:read")&&!actor.hasPermission("channel:manage")) throw new DomainException(ErrorCode.FORBIDDEN,"缺少权限：channel:read"); }
    private void require(Actor actor,String permission) { if(!actor.hasPermission(permission)) throw new DomainException(ErrorCode.FORBIDDEN,"缺少权限："+permission); }
    private String required(String value,String name) { if(value==null||value.isBlank()) throw new DomainException(ErrorCode.VALIDATION_ERROR,name+"不能为空"); return value.trim(); }
    private String limited(String value,String name,int maximum) { String normalized=required(value,name); if(normalized.length()>maximum) throw new DomainException(ErrorCode.VALIDATION_ERROR,name+"长度超限"); return normalized; }
    private void journal(Actor actor,String action,Object before,AcquisitionChannel after,String reason,String event) {
        String operation="op-"+ids.nextId(); String beforeJson=write(before), afterJson=write(after); audit.record(actor,action,"ACQUISITION_CHANNEL",after.id(),operation,"API",null,beforeJson,afterJson);
        outbox.append(new DomainEvent(event,"ACQUISITION_CHANNEL",after.id(),actor.tenantId(),write(Map.of("channelId",String.valueOf(after.id()),"status",after.status().name())),Instant.now()),operation,actor.userId());
    }
    private String write(Object value) { try{return json.writeValueAsString(value);}catch(Exception e){throw new IllegalStateException(e);} }
}
