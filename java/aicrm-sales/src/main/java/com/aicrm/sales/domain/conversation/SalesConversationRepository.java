package com.aicrm.sales.domain.conversation;
import java.util.List; import java.util.Optional;
public interface SalesConversationRepository { SalesConversation insert(SalesConversation value,long actorId); Optional<SalesConversation> find(long tenantId,long id); List<SalesConversationEntry> entries(long tenantId,long id); boolean append(SalesConversationEntry entry); boolean transition(long tenantId,long id,long version,SalesConversation.Status from,SalesConversation.Status to,long actorId); }
