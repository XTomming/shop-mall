package com.niit.shopmall.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 订单超时延迟队列配置（TTL + 死信队列）
 */
@Configuration
@ConditionalOnProperty(name = "mall.mq.enabled", havingValue = "true")
public class RabbitMQConfig {

    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.routing";

    public static final String ORDER_TIMEOUT_EXCHANGE = "order.timeout.exchange";
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout.routing";

    public static final String SECKILL_ORDER_QUEUE = "seckill.order.queue";
    public static final String ORDER_DELAY_SECKILL_QUEUE = "order.delay.seckill.queue";
    public static final String ORDER_DELAY_SECKILL_ROUTING_KEY = "order.delay.seckill.routing";

    @Value("${mall.order.timeout-minutes}")
    private long timeoutMinutes;

    @Value("${mall.order.seckill-timeout-minutes}")
    private long seckillTimeoutMinutes;

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange orderTimeoutExchange() {
        return new DirectExchange(ORDER_TIMEOUT_EXCHANGE, true, false);
    }

    /**
     * 延迟队列：消息过期后投递到死信交换机
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", timeoutMinutes * 60 * 1000);
        args.put("x-dead-letter-exchange", ORDER_TIMEOUT_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_TIMEOUT_ROUTING_KEY);
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
    }

    /**
     * 死信队列：真正处理超时取消
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return new Queue(ORDER_TIMEOUT_QUEUE, true, false, false);
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with(ORDER_DELAY_ROUTING_KEY);
    }

    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue()).to(orderTimeoutExchange()).with(ORDER_TIMEOUT_ROUTING_KEY);
    }

    /**
     * 秒杀订单异步建单队列（走默认交换机）
     */
    @Bean
    public Queue seckillOrderQueue() {
        return new Queue(SECKILL_ORDER_QUEUE, true, false, false);
    }

    /**
     * 秒杀单延迟队列：5 分钟 TTL，过期后投递到死信交换机
     */
    @Bean
    public Queue orderDelaySeckillQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", seckillTimeoutMinutes * 60 * 1000);
        args.put("x-dead-letter-exchange", ORDER_TIMEOUT_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_TIMEOUT_ROUTING_KEY);
        return new Queue(ORDER_DELAY_SECKILL_QUEUE, true, false, false, args);
    }

    @Bean
    public Binding orderDelaySeckillBinding() {
        return BindingBuilder.bind(orderDelaySeckillQueue()).to(orderDelayExchange()).with(ORDER_DELAY_SECKILL_ROUTING_KEY);
    }
}
