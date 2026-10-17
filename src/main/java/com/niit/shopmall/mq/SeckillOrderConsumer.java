package com.niit.shopmall.mq;

import com.niit.shopmall.config.RabbitMQConfig;
import com.niit.shopmall.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单消息消费者（仅 MQ 开启时存在）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mall.mq.enabled", havingValue = "true")
public class SeckillOrderConsumer {

    @Autowired
    private SeckillService seckillService;

    @RabbitListener(queues = RabbitMQConfig.SECKILL_ORDER_QUEUE)
    public void onMessage(String message) {
        log.info("收到秒杀订单消息：{}", message);
        String[] parts = message.split(":");
        Long memberId = Long.valueOf(parts[0]);
        Long itemId = Long.valueOf(parts[1]);
        try {
            seckillService.createSeckillOrder(memberId, itemId);
        } catch (Exception e) {
            log.error("秒杀订单创建失败，memberId={}, itemId={}", memberId, itemId, e);
            // 回补库存与限购
            seckillService.restoreSeckillOrder(buildRollbackOrder(memberId, itemId));
        }
    }

    private com.niit.shopmall.entity.Orders buildRollbackOrder(Long memberId, Long itemId) {
        com.niit.shopmall.entity.Orders order = new com.niit.shopmall.entity.Orders();
        order.setMemberId(memberId);
        order.setSeckillItemId(itemId);
        return order;
    }
}
