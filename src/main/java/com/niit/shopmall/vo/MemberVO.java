package com.niit.shopmall.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员信息（不含密码）
 */
@Data
public class MemberVO {

    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    private Integer gender;

    private Integer status;

    private LocalDateTime createTime;
}
