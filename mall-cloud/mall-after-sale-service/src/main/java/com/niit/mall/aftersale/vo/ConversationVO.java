package com.niit.mall.aftersale.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客服会话视图（M3 简化：不含会员/客服昵称）
 */
@Data
public class ConversationVO {

    private Long id;

    private Long memberId;

    private Long serviceId;

    private Long productId;

    private Integer status;

    private String statusDesc;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
