package com.aicrm.config;

import com.aicrm.common.constant.MqConstants;
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
 * 业务队列绑定 DLQ：消费重试耗尽后消息进入死信队列（aicrm.queue.dlq）等待人工介入。
 * 队列/交换器常量见 {@link MqConstants}。
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(MqConstants.EVENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange dlqExchange() {
        return new TopicExchange(MqConstants.DLQ_EXCHANGE, true, false);
    }

    @Bean
    public Queue dlqQueue() {
        return new Queue(MqConstants.DLQ, true);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(MqConstants.DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue channelEventQueue() {
        return buildQueue(MqConstants.QUEUE_CHANNEL_EVENT);
    }

    @Bean
    public Binding channelEventBinding() {
        return BindingBuilder.bind(channelEventQueue()).to(eventExchange()).with(MqConstants.ROUTING_CHANNEL_EVENT);
    }

    @Bean
    public Queue aiAnalysisQueue() {
        return buildQueue(MqConstants.QUEUE_AI_ANALYSIS);
    }

    @Bean
    public Binding aiAnalysisBinding() {
        return BindingBuilder.bind(aiAnalysisQueue()).to(eventExchange()).with(MqConstants.ROUTING_AI_ANALYSIS);
    }

    @Bean
    public Queue workflowQueue() {
        return buildQueue(MqConstants.QUEUE_WORKFLOW);
    }

    @Bean
    public Binding workflowBinding() {
        return BindingBuilder.bind(workflowQueue()).to(eventExchange()).with(MqConstants.ROUTING_WORKFLOW);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /** 业务队列：持久化 + 绑定死信（重试耗尽后投递 DLQ，routing key: dlq.{queueName}） */
    private Queue buildQueue(String name) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", MqConstants.DLQ_EXCHANGE);
        args.put("x-dead-letter-routing-key", "dlq." + name);
        return new Queue(name, true, false, false, args);
    }
}
