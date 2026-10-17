package com.niit.shopmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细
 */
@Data
public class OrderItemVO {

    private Long id;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;
}
