package com.niit.mall.order.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品信息（Feign 从 product-service 获取，仅保留订单所需字段）
 */
@Data
public class Product {

    private Long id;

    private String name;

    private String mainImage;

    private BigDecimal price;

    private Integer stock;

    private Integer status;
}
