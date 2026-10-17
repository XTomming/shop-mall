package com.niit.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车条目（含商品信息）
 */
@Data
public class CartVO {

    private Long id;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private Integer quantity;

    private Integer checked;

    private Integer stock;
}
