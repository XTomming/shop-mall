package com.niit.shopmall.dto;

import lombok.Data;

/**
 * 修改购物车参数（数量 / 选中状态，均可不传）
 */
@Data
public class CartUpdateDTO {

    private Integer quantity;

    private Integer checked;
}
