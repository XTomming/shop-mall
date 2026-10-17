package com.niit.shopmall.entity;

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

    /** 类型：1退款，2发票，3纠纷 */
    private Integer type;

    private Long memberId;

    private Long orderId;

    /** 申请原因/描述 */
    private String reason;

    /** 退款金额（退款单） */
    private BigDecimal refundAmount;

    /** 凭证图片，逗号分隔 */
    private String images;

    /** 发票抬头类型：1个人，2公司 */
    private Integer invoiceType;

    private String invoiceTitle;

    private String taxNo;

    private String email;

    /** 发票号码（开具后填写） */
    private String invoiceNo;

    /** 纠纷来源退款单ID（纠纷单） */
    private Long sourceRefundId;

    private Long serviceId;

    /** 客服处理说明 */
    private String handleRemark;

    private LocalDateTime handleTime;

    /** 状态：0待处理，1处理中，2已完成，3已拒绝，4已关闭 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
