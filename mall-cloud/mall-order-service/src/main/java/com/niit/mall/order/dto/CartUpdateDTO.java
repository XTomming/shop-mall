package com.niit.mall.order.dto;

import lombok.Data;

/**
 * 修改购物车参数（数量 / 选中）
 */
@Data
public class CartUpdateDTO {

    private Integer quantity;

    private Integer checked;
}
