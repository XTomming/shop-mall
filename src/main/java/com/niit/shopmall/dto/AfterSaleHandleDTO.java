package com.niit.shopmall.dto;

import lombok.Data;

/**
 * 客服处理售后单参数（办结/拒绝共用）
 */
@Data
public class AfterSaleHandleDTO {

    /** 处理说明 */
    private String handleRemark;

    /** 发票号码（办结发票单时填写） */
    private String invoiceNo;
}
