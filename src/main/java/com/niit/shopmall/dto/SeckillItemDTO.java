package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 秒杀商品参数
 */
@Data
public class SeckillItemDTO {

    @NotNull(message = "场次不能为空")
    private Long activityId;

    @NotNull(message = "商品不能为空")
    private Long productId;

    @NotNull(message = "秒杀价不能为空")
    private BigDecimal seckillPrice;

    @NotNull(message = "秒杀库存不能为空")
    private Integer seckillStock;

    private Integer limitPerUser;

    private Integer status;
}
