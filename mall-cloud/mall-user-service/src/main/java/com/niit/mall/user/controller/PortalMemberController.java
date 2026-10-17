package com.niit.mall.user.controller;

import com.niit.mall.common.Result;
import com.niit.mall.security.LoginUser;
import com.niit.mall.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台会员接口（M1 仅提供「当前用户」接口，用于验证网关请求头传递）
 */
@RestController
@RequestMapping("/api/portal/member")
public class PortalMemberController {

    @GetMapping("/me")
    public Result<LoginUser> me() {
        return Result.success(SecurityUtils.getCurrentUser());
    }
}
