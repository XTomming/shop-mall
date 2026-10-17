package com.niit.shopmall.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客服消息表
 */
@Data
@TableName("conversation_message")
public class ConversationMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    /** 发送方：MEMBER 会员，SERVICE 客服 */
    private String senderType;

    private Long senderId;

    private String content;

    /** 消息类型：TEXT 文本，IMAGE 图片，PRODUCT 商品 */
    private String type;

    /** 商品ID（PRODUCT 类型） */
    private Long productId;

    /** 商品图片（PRODUCT 类型，快照） */
    private String productImage;

    /** 订单ID（ORDER 类型） */
    private Long orderId;

    /** 订单状态（ORDER 类型） */
    private Integer orderStatus;

    /** 订单状态描述（ORDER 类型） */
    private String orderStatusDesc;

    /** 订单快照JSON（ORDER 类型，含明细） */
    private String orderSnapshot;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
