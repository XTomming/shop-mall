package com.niit.mall.order.client;

import com.niit.mall.common.Result;
import com.niit.mall.order.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品服务 Feign 客户端（跨服务调用）
 */
@FeignClient(name = "mall-product-service")
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    Result<Product> getById(@PathVariable("id") Long id);

    @PostMapping("/api/product/{id}/deduct")
    Result<Boolean> deduct(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);

    @PostMapping("/api/product/{id}/restore")
    Result<Void> restore(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
