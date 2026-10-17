package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.MockPayDTO;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 前台模拟支付接口
 */
@Api(tags = "前台-支付")
@RestController
@RequestMapping("/api/portal/pay")
public class PortalPayController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("模拟支付")
    @PostMapping("/mock")
    public Result<Void> mockPay(@Valid @RequestBody MockPayDTO dto) {
        orderService.mockPay(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }
}
