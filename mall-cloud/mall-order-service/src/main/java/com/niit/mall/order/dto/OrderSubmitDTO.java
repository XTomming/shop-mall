package com.niit.mall.order.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 提交订单参数
 */
@Data
public class OrderSubmitDTO {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    @NotEmpty(message = "请选择要结算的购物车商品")
    private List<Long> cartIds;
}
