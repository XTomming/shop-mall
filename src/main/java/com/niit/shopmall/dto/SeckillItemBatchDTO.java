package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量新增秒杀商品参数
 */
@Data
public class SeckillItemBatchDTO {

    @NotNull(message = "场次不能为空")
    private Long activityId;

    @Valid
    @NotEmpty(message = "请选择商品")
    private List<Item> items;

    @Data
    public static class Item {

        @NotNull(message = "商品不能为空")
        private Long productId;

        @NotNull(message = "秒杀价不能为空")
        private BigDecimal seckillPrice;

        @NotNull(message = "秒杀库存不能为空")
        private Integer seckillStock;

        private Integer limitPerUser;
    }
}
