package com.niit.shopmall.controller.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.AfterSaleHandleDTO;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.AfterSaleService;
import com.niit.shopmall.vo.AfterSaleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服端-售后单接口
 */
@Api(tags = "客服端-售后单")
@RestController
@RequestMapping("/api/service/after-sale")
public class ServiceAfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @ApiOperation("售后单分页")
    @GetMapping("/page")
    public Result<Page<AfterSaleVO>> page(@RequestParam(required = false) Integer type,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(afterSaleService.page(type, status, pageNum, pageSize));
    }

    @ApiOperation("接单")
    @PutMapping("/{id}/accept")
    public Result<Void> accept(@PathVariable Long id) {
        afterSaleService.serviceAccept(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }

    @ApiOperation("办结")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id, @RequestBody(required = false) AfterSaleHandleDTO dto) {
        afterSaleService.serviceComplete(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }

    @ApiOperation("拒绝")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody(required = false) AfterSaleHandleDTO dto) {
        afterSaleService.serviceReject(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }
}
