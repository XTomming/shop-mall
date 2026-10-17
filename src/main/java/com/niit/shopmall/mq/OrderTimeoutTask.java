package com.niit.shopmall.mq;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.enums.OrderStatus;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.mapper.OrdersMapper;
import com.niit.shopmall.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单超时定时任务兜底（仅 MQ 关闭时启用）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "mall.mq.enabled", havingValue = "false")
public class OrderTimeoutTask {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderService orderService;

    @Value("${mall.order.timeout-minutes}")
    private long timeoutMinutes;

    @Value("${mall.order.seckill-timeout-minutes}")
    private long seckillTimeoutMinutes;

    /**
     * 每分钟扫描一次超时的待付款订单（普通单与秒杀单分开计算超时时间）
     */
    @Scheduled(fixedDelay = 60000)
    public void scanTimeoutOrders() {
        LocalDateTime now = LocalDateTime.now();
        // 普通单超时
        List<Orders> normalOrders = ordersMapper.selectList(Wrappers.<Orders>lambdaQuery()
                .eq(Orders::getStatus, OrderStatus.PENDING_PAYMENT.getCode())
                .isNull(Orders::getSeckillItemId)
                .lt(Orders::getCreateTime, now.minusMinutes(timeoutMinutes)));
        // 秒杀单超时
        List<Orders> seckillOrders = ordersMapper.selectList(Wrappers.<Orders>lambdaQuery()
                .eq(Orders::getStatus, OrderStatus.PENDING_PAYMENT.getCode())
                .isNotNull(Orders::getSeckillItemId)
                .lt(Orders::getCreateTime, now.minusMinutes(seckillTimeoutMinutes)));
        List<Orders> all = new ArrayList<>(normalOrders);
        all.addAll(seckillOrders);
        for (Orders order : all) {
            log.info("定时任务取消超时订单：{}", order.getOrderNo());
            orderService.cancelTimeoutOrder(order.getId());
        }
    }
}
