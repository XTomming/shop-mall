package com.niit.mall.aftersale.client;

import com.niit.mall.aftersale.entity.Product;
import com.niit.mall.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品服务 Feign 客户端（聊天发商品卡片时查商品信息）
 */
@FeignClient(name = "mall-product-service")
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    Result<Product> getById(@PathVariable("id") Long id);
}
