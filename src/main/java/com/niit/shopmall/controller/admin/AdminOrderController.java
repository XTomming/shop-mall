package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.service.OrderService;
import com.niit.shopmall.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台订单管理接口
 */
@Api(tags = "后台-订单管理")
@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("订单分页")
    @GetMapping("/page")
    public Result<Page<OrderVO>> page(@RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) String orderNo,
                                      @RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(orderService.adminPage(status, orderNo, pageNum, pageSize));
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.adminDetail(id));
    }

    @ApiOperation("订单发货")
    @PutMapping("/{id}/delivery")
    public Result<Void> delivery(@PathVariable Long id) {
        orderService.adminDelivery(id);
        return Result.success();
    }

    @ApiOperation("关闭订单")
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        orderService.adminClose(id);
        return Result.success();
    }
}
