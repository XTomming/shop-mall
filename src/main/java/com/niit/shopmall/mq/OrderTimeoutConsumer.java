package com.niit.shopmall.mq;

import com.niit.shopmall.config.RabbitMQConfig;
import com.niit.shopmall.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 订单超时消息消费者（仅 MQ 开启时存在）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mall.mq.enabled", havingValue = "true")
public class OrderTimeoutConsumer {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_TIMEOUT_QUEUE)
    public void onOrderTimeout(String message) {
        log.info("收到订单超时消息：{}", message);
        Long orderId = Long.valueOf(message);
        // 幂等处理：仅待付款订单会被取消
        orderService.cancelTimeoutOrder(orderId);
    }
}
