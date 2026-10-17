package com.niit.mall.aftersale.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.aftersale.dto.AfterSaleHandleDTO;
import com.niit.mall.aftersale.service.AfterSaleService;
import com.niit.mall.aftersale.vo.AfterSaleVO;
import com.niit.mall.common.Result;
import com.niit.mall.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客服端售后单接口
 */
@RestController
@RequestMapping("/api/service/after-sale")
public class ServiceAfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @GetMapping("/page")
    public Result<Page<AfterSaleVO>> page(@RequestParam(required = false) Integer type,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(afterSaleService.page(type, status, pageNum, pageSize));
    }

    @PutMapping("/{id}/accept")
    public Result<Void> accept(@PathVariable Long id) {
        afterSaleService.serviceAccept(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id, @RequestBody(required = false) AfterSaleHandleDTO dto) {
        afterSaleService.serviceComplete(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody(required = false) AfterSaleHandleDTO dto) {
        afterSaleService.serviceReject(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }
}
