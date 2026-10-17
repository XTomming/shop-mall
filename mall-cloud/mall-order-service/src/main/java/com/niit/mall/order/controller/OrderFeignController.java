package com.niit.mall.order.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.mall.common.BusinessException;
import com.niit.mall.common.Result;
import com.niit.mall.common.enums.OrderStatus;
import com.niit.mall.order.client.ProductClient;
import com.niit.mall.order.entity.OrderItem;
import com.niit.mall.order.entity.Orders;
import com.niit.mall.order.mapper.OrderItemMapper;
import com.niit.mall.order.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单服务内部接口（供 after-sale-service 通过 OpenFeign 调用）
 */
@RestController
@RequestMapping("/api/order")
public class OrderFeignController {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductClient productClient;

    @GetMapping("/{id}")
    public Result<Orders> getOrder(@PathVariable Long id) {
        return Result.success(ordersMapper.selectById(id));
    }

    /**
     * 退款：订单置为已退款，并恢复库存（Feign 调 product-service）
     */
    @PostMapping("/{id}/refund")
    public Result<Void> refund(@PathVariable Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        int status = order.getStatus();
        if (status != OrderStatus.PENDING_DELIVERY.getCode()
                && status != OrderStatus.PENDING_RECEIPT.getCode()
                && status != OrderStatus.COMPLETED.getCode()) {
            throw new BusinessException("订单状态不允许退款");
        }
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(OrderStatus.REFUNDED.getCode());
        ordersMapper.updateById(update);
        List<OrderItem> items = orderItemMapper.selectList(Wrappers.<OrderItem>lambdaQuery()
                .eq(OrderItem::getOrderId, id));
        for (OrderItem item : items) {
            productClient.restore(item.getProductId(), item.getQuantity());
        }
        return Result.success();
    }
}
