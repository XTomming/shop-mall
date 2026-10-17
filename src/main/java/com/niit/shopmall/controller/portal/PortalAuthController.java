package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.LoginDTO;
import com.niit.shopmall.dto.RegisterDTO;
import com.niit.shopmall.service.AuthService;
import com.niit.shopmall.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 前台认证接口
 */
@Api(tags = "前台-认证")
@RestController
@RequestMapping("/api/portal")
public class PortalAuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.memberLogin(dto));
    }
}
