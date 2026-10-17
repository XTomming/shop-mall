package com.niit.shopmall.dto;

import lombok.Data;

/**
 * 发起客服会话参数
 */
@Data
public class ConversationCreateDTO {

    /** 咨询的商品ID（可选） */
    private Long productId;
}
