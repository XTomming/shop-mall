package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.service.AfterSaleService;
import com.niit.shopmall.vo.AfterSaleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 后台售后监看接口（只读 + 统计）
 */
@Api(tags = "后台-售后监看")
@RestController
@RequestMapping("/api/admin/after-sale")
public class AdminAfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @ApiOperation("售后单分页（只读）")
    @GetMapping("/page")
    public Result<Page<AfterSaleVO>> page(@RequestParam(required = false) Integer type,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(afterSaleService.page(type, status, pageNum, pageSize));
    }

    @ApiOperation("售后统计")
    @GetMapping("/stat")
    public Result<Map<String, Object>> stat() {
        return Result.success(afterSaleService.adminStat());
    }
}
