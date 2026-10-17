package com.niit.shopmall.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户（由 JWT 解析得到，放入 SecurityContext）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    private Long id;
    private String username;
    /** 角色：ADMIN / MEMBER */
    private String role;
}
