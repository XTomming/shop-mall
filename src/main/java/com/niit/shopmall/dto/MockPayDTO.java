package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 模拟支付参数
 */
@Data
public class MockPayDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Integer payType;
}
