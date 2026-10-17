package com.niit.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单（含明细）
 */
@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long memberId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer status;

    private String statusDesc;

    private Integer payType;

    private LocalDateTime payTime;

    private LocalDateTime deliveryTime;

    private LocalDateTime receiveTime;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private LocalDateTime createTime;

    private List<OrderItemVO> items;
}
