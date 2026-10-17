package com.niit.mall.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.common.Result;
import com.niit.mall.order.dto.OrderSubmitDTO;
import com.niit.mall.order.entity.Orders;
import com.niit.mall.order.service.OrderService;
import com.niit.mall.order.vo.OrderVO;
import com.niit.mall.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 前台订单接口
 */
@RestController
@RequestMapping("/api/portal/order")
public class PortalOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Orders> submit(@Valid @RequestBody OrderSubmitDTO dto) {
        return Result.success(orderService.submit(SecurityUtils.getCurrentUserId(), dto));
    }

    @GetMapping("/list")
    public Result<Page<OrderVO>> list(@RequestParam(required = false) Integer status,
                                      @RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(orderService.portalList(SecurityUtils.getCurrentUserId(), status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.portalDetail(SecurityUtils.getCurrentUserId(), id));
    }

    @PutMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        orderService.receive(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
