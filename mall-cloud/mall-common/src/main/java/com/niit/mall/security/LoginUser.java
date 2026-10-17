package com.niit.mall.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户（由网关鉴权后通过请求头传递，业务服务解析得到）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long id;
    private String username;
    /** 角色：ADMIN / MEMBER / SERVICE */
    private String role;
}
