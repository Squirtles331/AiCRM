package com.aicrm.stagegate;

import com.aicrm.config.RabbitConfig;
import com.aicrm.integration.messaging.DomainEventTopology;
import com.aicrm.integration.messaging.RabbitOutboxEventPublisher;
import com.aicrm.platform.application.OutboxMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.GetResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@EnabledIfSystemProperty(named = "aicrm.rabbitmq.integration.enabled", matches = "true")
class RabbitOutboxEventPublisherIntegrationTest {
    @Container
    private static final RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    private static CachingConnectionFactory connectionFactory;
    private static RabbitOutboxEventPublisher publisher;
    private static final ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

    @BeforeAll
    static void startPublisherAgainstBroker() {
        connectionFactory = new CachingConnectionFactory(RABBIT.getHost(), RABBIT.getAmqpPort());
        connectionFactory.setUsername(RABBIT.getAdminUsername());
        connectionFactory.setPassword(RABBIT.getAdminPassword());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);

        RabbitConfig topology = new RabbitConfig();
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareExchange(topology.eventExchange());
        admin.declareExchange(topology.dlqExchange());
        admin.declareQueue(topology.dlqQueue());
        admin.declareBinding(topology.dlqBinding());
        admin.declareQueue(topology.domainEventQueue());
        admin.declareBinding(topology.domainEventBinding());

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setMessageConverter(new Jackson2JsonMessageConverter(JSON));
        publisher = new RabbitOutboxEventPublisher(template, JSON);
    }

    @AfterAll
    static void closePublisherConnection() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void publishesConfirmedPersistentEnvelopeAndDeadLettersRejectedDelivery() throws Exception {
        OutboxMessage event = new OutboxMessage(90001L, 51L, "LEAD", 90002L, "LeadCreated", 1,
                "lead:CREATE:90003", "trace-rabbit-1", "{\"leadId\":\"90002\"}", Instant.parse("2026-09-11T01:00:00Z"), 0);

        publisher.publish(event);

        try (Connection connection = connectionFactory.createConnection();
             com.rabbitmq.client.Channel channel = connection.createChannel(false)) {
            GetResponse delivery = channel.basicGet(DomainEventTopology.DOMAIN_EVENT_QUEUE, false);
            assertThat(delivery).isNotNull();
            assertThat(delivery.getProps().getMessageId()).isEqualTo("90001");
            assertThat(delivery.getProps().getDeliveryMode()).isEqualTo(2);
            assertThat(delivery.getProps().getHeaders()).containsKeys("tenantId", "eventType");
            assertThat(String.valueOf(delivery.getProps().getHeaders().get("tenantId"))).isEqualTo("51");
            assertThat(String.valueOf(delivery.getProps().getHeaders().get("eventType"))).isEqualTo("LeadCreated");
            JsonNode envelope = JSON.readTree(new String(delivery.getBody(), StandardCharsets.UTF_8));
            assertThat(envelope.path("eventId").asText()).isEqualTo("90001");
            assertThat(envelope.path("tenantId").asText()).isEqualTo("51");
            assertThat(envelope.path("aggregateId").asText()).isEqualTo("90002");
            assertThat(envelope.path("payload").path("leadId").asText()).isEqualTo("90002");

            channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            GetResponse deadLetter = await().atMost(Duration.ofSeconds(5))
                    .until(() -> channel.basicGet(DomainEventTopology.DEAD_LETTER_QUEUE, true), Objects::nonNull);
            assertThat(deadLetter).isNotNull();
            assertThat(deadLetter.getProps().getMessageId()).isEqualTo("90001");
            assertThat(JSON.readTree(new String(deadLetter.getBody(), StandardCharsets.UTF_8)).path("eventType").asText())
                    .isEqualTo("LeadCreated");
        }
    }
}
