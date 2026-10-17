package com.niit.shopmall.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价（含会员昵称头像）
 */
@Data
public class CommentVO {

    private Long id;

    private Long productId;

    private Long memberId;

    private String nickname;

    private String avatar;

    private String content;

    private Integer rating;

    private LocalDateTime createTime;
}
