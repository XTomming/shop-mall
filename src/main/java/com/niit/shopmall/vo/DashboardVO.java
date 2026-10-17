package com.niit.shopmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 后台首页统计
 */
@Data
public class DashboardVO {

    private Long memberCount;

    private Long productCount;

    private Long orderCount;

    private BigDecimal totalSales;

    /** 近 7 天日期（MM-dd） */
    private List<String> days;

    /** 近 7 天每日订单数 */
    private List<Long> orderCountPerDay;

    /** 近 7 天每日销售额 */
    private List<BigDecimal> salesPerDay;
}
