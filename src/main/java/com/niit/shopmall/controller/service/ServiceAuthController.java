package com.niit.shopmall.controller.service;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.LoginDTO;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.AuthService;
import com.niit.shopmall.service.ConversationService;
import com.niit.shopmall.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 客服端认证接口
 */
@Api(tags = "客服端-认证")
@RestController
@RequestMapping("/api/service")
public class ServiceAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ConversationService conversationService;

    @ApiOperation("客服登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.serviceLogin(dto));
    }

    @ApiOperation("上线/下线")
    @PutMapping("/online")
    public Result<Void> online(@RequestParam boolean online) {
        conversationService.online(SecurityUtils.getCurrentUserId(), online);
        return Result.success();
    }
}
