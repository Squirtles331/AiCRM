package com.aicrm.integration.messaging;

import com.aicrm.platform.application.OutboxEventPublisher;
import com.aicrm.platform.application.OutboxMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/** RabbitMQ transport adapter for versioned outbox envelopes. */
@Component
public class RabbitOutboxEventPublisher implements OutboxEventPublisher {
    private static final long CONFIRM_TIMEOUT_MILLIS = 5_000;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitOutboxEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxMessage message) {
        Boolean confirmed = rabbitTemplate.invoke(operations -> {
            operations.convertAndSend(DomainEventTopology.EVENT_EXCHANGE, routingKey(message), envelope(message), amqpMessage -> {
                amqpMessage.getMessageProperties().setMessageId(String.valueOf(message.id()));
                amqpMessage.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                amqpMessage.getMessageProperties().setHeader("tenantId", String.valueOf(message.tenantId()));
                amqpMessage.getMessageProperties().setHeader("eventType", message.eventType());
                return amqpMessage;
            });
            return operations.waitForConfirms(CONFIRM_TIMEOUT_MILLIS);
        });
        if (!Boolean.TRUE.equals(confirmed)) {
            throw new AmqpException("RabbitMQ did not confirm outbox event " + message.id());
        }
    }

    private String routingKey(OutboxMessage message) {
        return "crm." + message.aggregateType().toLowerCase() + "." + message.eventType().toLowerCase();
    }

    private Map<String, Object> envelope(OutboxMessage message) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("eventId", String.valueOf(message.id()));
        envelope.put("eventType", message.eventType());
        envelope.put("eventVersion", message.eventVersion());
        envelope.put("tenantId", String.valueOf(message.tenantId()));
        envelope.put("aggregateType", message.aggregateType());
        envelope.put("aggregateId", String.valueOf(message.aggregateId()));
        envelope.put("operationId", message.operationId());
        envelope.put("occurredAt", message.occurredAt());
        envelope.put("traceId", message.traceId());
        try {
            envelope.put("payload", objectMapper.readTree(message.payload()));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Outbox payload is not valid JSON for event " + message.id(), exception);
        }
        return envelope;
    }
}
