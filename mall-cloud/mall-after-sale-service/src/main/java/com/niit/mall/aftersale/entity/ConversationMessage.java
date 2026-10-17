package com.niit.mall.aftersale.entity;

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

    private String senderType;

    private Long senderId;

    private String content;

    private String type;

    private Long productId;

    private String productImage;

    private Long orderId;

    private Integer orderStatus;

    private String orderStatusDesc;

    private String orderSnapshot;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
