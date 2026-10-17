package com.niit.mall.aftersale.client;

import com.niit.mall.aftersale.entity.Orders;
import com.niit.mall.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 订单服务 Feign 客户端（售后退款联动订单）
 */
@FeignClient(name = "mall-order-service")
public interface OrderClient {

    @GetMapping("/api/order/{id}")
    Result<Orders> getOrder(@PathVariable("id") Long id);

    @PostMapping("/api/order/{id}/refund")
    Result<Void> refund(@PathVariable("id") Long id);
}
