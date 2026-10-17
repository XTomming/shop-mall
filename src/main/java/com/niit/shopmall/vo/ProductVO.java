package com.niit.shopmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情
 */
@Data
public class ProductVO {

    private Long id;

    private Long categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private String detail;

    private Integer status;

    private LocalDateTime createTime;

    private List<String> images;
}
