package com.niit.mall.aftersale.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.aftersale.dto.AfterSaleApplyDTO;
import com.niit.mall.aftersale.entity.AfterSale;
import com.niit.mall.aftersale.service.AfterSaleService;
import com.niit.mall.aftersale.vo.AfterSaleVO;
import com.niit.mall.common.Result;
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
 * 前台售后接口
 */
@RestController
@RequestMapping("/api/portal/after-sale")
public class PortalAfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @GetMapping("/list")
    public Result<Page<AfterSaleVO>> list(@RequestParam(required = false) Integer type,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(afterSaleService.portalList(SecurityUtils.getCurrentUserId(), type, pageNum, pageSize));
    }

    @PostMapping("/refund")
    public Result<AfterSale> refund(@Valid @RequestBody AfterSaleApplyDTO dto) {
        return Result.success(afterSaleService.applyRefund(SecurityUtils.getCurrentUserId(), dto));
    }

    @PostMapping("/invoice")
    public Result<AfterSale> invoice(@Valid @RequestBody AfterSaleApplyDTO dto) {
        return Result.success(afterSaleService.applyInvoice(SecurityUtils.getCurrentUserId(), dto));
    }

    @PostMapping("/{id}/escalate")
    public Result<AfterSale> escalate(@PathVariable Long id) {
        return Result.success(afterSaleService.escalate(SecurityUtils.getCurrentUserId(), id));
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        afterSaleService.cancel(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
