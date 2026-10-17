package com.niit.mall.order.client;

import com.niit.mall.common.Result;
import com.niit.mall.order.entity.MemberAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端（跨服务取收货地址）
 */
@FeignClient(name = "mall-user-service")
public interface UserClient {

    @GetMapping("/api/address/{id}")
    Result<MemberAddress> getAddress(@PathVariable("id") Long id);
}
