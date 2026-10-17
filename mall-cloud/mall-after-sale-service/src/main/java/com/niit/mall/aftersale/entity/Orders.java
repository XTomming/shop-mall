package com.niit.mall.aftersale.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单信息（Feign 从 order-service 获取，仅保留售后所需字段）
 */
@Data
public class Orders {

    private Long id;

    private Long memberId;

    private String orderNo;

    private Integer status;

    private BigDecimal payAmount;
}
