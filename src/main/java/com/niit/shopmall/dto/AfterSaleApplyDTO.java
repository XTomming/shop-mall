package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 售后申请参数（退款/发票共用）
 */
@Data
public class AfterSaleApplyDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 申请原因/描述 */
    private String reason;

    /** 凭证图片，逗号分隔 */
    private String images;

    /** 发票抬头类型：1个人，2公司 */
    private Integer invoiceType;

    private String invoiceTitle;

    private String taxNo;

    private String email;
}
