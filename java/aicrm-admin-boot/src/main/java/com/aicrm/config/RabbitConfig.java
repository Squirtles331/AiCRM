package com.aicrm.config;

import com.aicrm.integration.messaging.DomainEventTopology;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置：事件总线 + 死信队列
 * <p>
 * 业务队列绑定 DLQ，消费重试耗尽后等待人工介入。
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(DomainEventTopology.EVENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange dlqExchange() {
        return new TopicExchange(DomainEventTopology.DLQ_EXCHANGE, true, false);
    }

    @Bean
    public Queue dlqQueue() {
        return new Queue(DomainEventTopology.DEAD_LETTER_QUEUE, true);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(DomainEventTopology.DEAD_LETTER_ROUTING);
    }

    @Bean
    public Queue domainEventQueue() {
        return buildQueue(DomainEventTopology.DOMAIN_EVENT_QUEUE);
    }

    @Bean
    public Binding domainEventBinding() {
        return BindingBuilder.bind(domainEventQueue()).to(eventExchange()).with(DomainEventTopology.DOMAIN_EVENT_ROUTING);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setMandatory(true);
        return template;
    }

    /** 业务队列：持久化 + 绑定死信（重试耗尽后投递 DLQ，routing key: dlq.{queueName}） */
    private Queue buildQueue(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DomainEventTopology.DLQ_EXCHANGE);
        args.put("x-dead-letter-routing-key", "dead." + name);
        return new Queue(name, true, false, false, args);
    }
}
