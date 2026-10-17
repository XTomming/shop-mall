package com.niit.mall.user.controller;

import com.niit.mall.common.Result;
import com.niit.mall.user.dto.LoginDTO;
import com.niit.mall.user.service.AuthService;
import com.niit.mall.user.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 后台认证接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.adminLogin(dto));
    }
}
