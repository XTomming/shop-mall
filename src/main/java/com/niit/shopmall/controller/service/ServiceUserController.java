package com.niit.shopmall.controller.service;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.AdminDTO;
import com.niit.shopmall.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 客服端-客服账号接口
 */
@Api(tags = "客服端-客服账号")
@RestController
@RequestMapping("/api/service/user")
public class ServiceUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("注册客服账号")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody AdminDTO dto) {
        sysUserService.serviceAdd(dto);
        return Result.success();
    }
}
