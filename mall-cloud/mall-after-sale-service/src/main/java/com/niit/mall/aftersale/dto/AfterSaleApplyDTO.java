package com.niit.mall.aftersale.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 售后申请参数（退款/发票共用）
 */
@Data
public class AfterSaleApplyDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private String reason;

    private String images;

    private Integer invoiceType;

    private String invoiceTitle;

    private String taxNo;

    private String email;
}
