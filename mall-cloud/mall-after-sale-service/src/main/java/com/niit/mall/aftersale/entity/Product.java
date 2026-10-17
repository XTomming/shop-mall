package com.niit.mall.aftersale.entity;

import lombok.Data;

/**
 * 商品信息（Feign 从 product-service 获取）
 */
@Data
public class Product {

    private Long id;

    private String name;

    private String mainImage;
}
