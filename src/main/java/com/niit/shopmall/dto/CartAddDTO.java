package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 加入购物车参数
 */
@Data
public class CartAddDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    private Integer quantity;
}
