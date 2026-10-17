package com.niit.shopmall.dto;

import lombok.Data;

/**
 * 发送客服消息参数
 */
@Data
public class ConversationMessageDTO {

    /** 消息类型：TEXT 文本（默认），IMAGE 图片，PRODUCT 商品，ORDER 订单 */
    private String type;

    /** 文本内容（TEXT）或图片URL（IMAGE） */
    private String content;

    /** 商品ID（PRODUCT 类型） */
    private Long productId;

    /** 订单ID（ORDER 类型） */
    private Long orderId;
}
