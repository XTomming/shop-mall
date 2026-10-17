package com.niit.mall.order.controller;

import com.niit.mall.common.Result;
import com.niit.mall.order.dto.MockPayDTO;
import com.niit.mall.order.service.OrderService;
import com.niit.mall.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 前台模拟支付接口
 */
@RestController
@RequestMapping("/api/portal/pay")
public class PortalPayController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/mock")
    public Result<Void> mockPay(@Valid @RequestBody MockPayDTO dto) {
        orderService.mockPay(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }
}
