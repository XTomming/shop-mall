package com.niit.shopmall.controller.portal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.AfterSaleApplyDTO;
import com.niit.shopmall.entity.AfterSale;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.AfterSaleService;
import com.niit.shopmall.vo.AfterSaleVO;
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
 * 前台售后接口
 */
@Api(tags = "前台-售后")
@RestController
@RequestMapping("/api/portal/after-sale")
public class PortalAfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @ApiOperation("我的售后单")
    @GetMapping("/list")
    public Result<Page<AfterSaleVO>> list(@RequestParam(required = false) Integer type,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(afterSaleService.portalList(SecurityUtils.getCurrentUserId(), type, pageNum, pageSize));
    }

    @ApiOperation("申请退款")
    @PostMapping("/refund")
    public Result<AfterSale> refund(@Valid @RequestBody AfterSaleApplyDTO dto) {
        return Result.success(afterSaleService.applyRefund(SecurityUtils.getCurrentUserId(), dto));
    }

    @ApiOperation("申请发票")
    @PostMapping("/invoice")
    public Result<AfterSale> invoice(@Valid @RequestBody AfterSaleApplyDTO dto) {
        return Result.success(afterSaleService.applyInvoice(SecurityUtils.getCurrentUserId(), dto));
    }

    @ApiOperation("退款升级为纠纷")
    @PostMapping("/{id}/escalate")
    public Result<AfterSale> escalate(@PathVariable Long id) {
        return Result.success(afterSaleService.escalate(SecurityUtils.getCurrentUserId(), id));
    }

    @ApiOperation("撤销售后单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        afterSaleService.cancel(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
