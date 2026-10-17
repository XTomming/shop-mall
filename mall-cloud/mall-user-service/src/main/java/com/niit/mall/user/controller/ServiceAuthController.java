package com.niit.mall.user.controller;

import com.niit.mall.common.Result;
import com.niit.mall.security.SecurityUtils;
import com.niit.mall.user.dto.LoginDTO;
import com.niit.mall.user.entity.SysUser;
import com.niit.mall.user.mapper.SysUserMapper;
import com.niit.mall.user.service.AuthService;
import com.niit.mall.user.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 客服端认证接口（登录 + 上线/下线）
 */
@RestController
@RequestMapping("/api/service")
public class ServiceAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.serviceLogin(dto));
    }

    @PutMapping("/online")
    public Result<Void> online(@RequestParam boolean online) {
        SysUser update = new SysUser();
        update.setId(SecurityUtils.getCurrentUserId());
        update.setOnline(online ? 1 : 0);
        sysUserMapper.updateById(update);
        return Result.success();
    }
}
