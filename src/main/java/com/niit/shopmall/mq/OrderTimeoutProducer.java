package com.niit.shopmall.mq;

import com.niit.shopmall.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 订单超时消息生产者（仅 MQ 开启时存在）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mall.mq.enabled", havingValue = "true")
public class OrderTimeoutProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送订单超时延迟消息
     */
    public void sendOrderTimeout(Long orderId) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                    RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                    String.valueOf(orderId));
        } catch (Exception e) {
            log.warn("订单超时消息发送失败：{}", e.getMessage());
        }
    }

    /**
     * 发送秒杀订单超时延迟消息（走独立 5 分钟延迟队列）
     */
    public void sendSeckillOrderTimeout(Long orderId) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                    RabbitMQConfig.ORDER_DELAY_SECKILL_ROUTING_KEY,
                    String.valueOf(orderId));
        } catch (Exception e) {
            log.warn("秒杀订单超时消息发送失败：{}", e.getMessage());
        }
    }
}
