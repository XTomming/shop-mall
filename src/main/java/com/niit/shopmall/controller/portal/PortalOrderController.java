package com.niit.shopmall.controller.portal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.OrderSubmitDTO;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.OrderService;
import com.niit.shopmall.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "前台-订单")
@RestController
@RequestMapping("/api/portal/order")
public class PortalOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("提交订单")
    @PostMapping
    public Result<Orders> submit(@Valid @RequestBody OrderSubmitDTO dto) {
        return Result.success(orderService.submit(SecurityUtils.getCurrentUserId(), dto));
    }

    @ApiOperation("我的订单")
    @GetMapping("/list")
    public Result<Page<OrderVO>> list(@RequestParam(required = false) Integer status,
                                      @RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(orderService.portalList(SecurityUtils.getCurrentUserId(), status, pageNum, pageSize));
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.portalDetail(SecurityUtils.getCurrentUserId(), id));
    }

    @ApiOperation("确认收货")
    @PutMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        orderService.receive(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
