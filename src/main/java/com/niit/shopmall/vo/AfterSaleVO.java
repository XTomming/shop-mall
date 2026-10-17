package com.niit.shopmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后单视图
 */
@Data
public class AfterSaleVO {

    private Long id;

    private String afterSaleNo;

    private Integer type;

    private String typeDesc;

    private Long memberId;

    private String memberNickname;

    private Long orderId;

    private String orderNo;

    private String reason;

    private BigDecimal refundAmount;

    private String images;

    private Integer invoiceType;

    private String invoiceTitle;

    private String taxNo;

    private String email;

    private String invoiceNo;

    private Long sourceRefundId;

    private Long serviceId;

    private String serviceNickname;

    private String handleRemark;

    private LocalDateTime handleTime;

    private Integer status;

    private String statusDesc;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
