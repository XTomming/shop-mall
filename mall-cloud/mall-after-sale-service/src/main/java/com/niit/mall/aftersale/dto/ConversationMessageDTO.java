package com.niit.mall.aftersale.dto;

import lombok.Data;

/**
 * 发送客服消息参数
 */
@Data
public class ConversationMessageDTO {

    private String type;

    private String content;

    private Long productId;

    private Long orderId;
}
