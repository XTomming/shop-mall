package com.niit.shopmall.mq;

import com.niit.shopmall.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单消息生产者（仅 MQ 开启时存在）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mall.mq.enabled", havingValue = "true")
public class SeckillOrderProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀建单消息，失败抛异常以便上层回滚库存
     */
    public void send(Long memberId, Long itemId) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE, memberId + ":" + itemId);
        } catch (Exception e) {
            log.error("秒杀订单消息发送失败：{}", e.getMessage());
            throw new RuntimeException("秒杀消息发送失败", e);
        }
    }
}
