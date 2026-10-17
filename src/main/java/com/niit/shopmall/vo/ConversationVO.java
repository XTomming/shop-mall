package com.niit.shopmall.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客服会话视图
 */
@Data
public class ConversationVO {

    private Long id;

    private Long memberId;

    private String memberNickname;

    private Long serviceId;

    private String serviceNickname;

    private Long productId;

    private Integer status;

    private String statusDesc;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
