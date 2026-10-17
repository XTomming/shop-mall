package com.niit.shopmall.vo;

import lombok.Data;

/**
 * 登录返回
 */
@Data
public class LoginVO {

    private String token;

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String role;

    /** 是否在线（客服）：1在线，0离线 */
    private Integer online;
}
