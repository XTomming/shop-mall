package com.niit.shopmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品视图
 */
@Data
public class SeckillItemVO {

    private Long id;

    private Long activityId;

    private String activityName;

    private Long productId;

    private String productName;

    private String productImage;

    /** 商品原价 */
    private BigDecimal originalPrice;

    /** 秒杀价 */
    private BigDecimal seckillPrice;

    /** 秒杀总库存 */
    private Integer seckillStock;

    /** 剩余库存（来自 Redis，未初始化则等于 seckillStock） */
    private Integer remainingStock;

    private Integer limitPerUser;

    /** 场次开始/结束时间 */
    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
