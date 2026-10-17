package com.niit.mall.aftersale.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后单表（退款/发票/纠纷）
 */
@Data
@TableName("after_sale")
public class AfterSale {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String afterSaleNo;

    private Integer type;

    private Long memberId;

    private Long orderId;

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

    private String handleRemark;

    private LocalDateTime handleTime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
