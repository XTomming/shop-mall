package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.SeckillService;
import com.niit.shopmall.vo.SeckillActivityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台秒杀接口
 */
@Api(tags = "前台-秒杀")
@RestController
@RequestMapping("/api/portal/seckill")
public class PortalSeckillController {

    @Autowired
    private SeckillService seckillService;

    @ApiOperation("秒杀场次与商品列表")
    @GetMapping("/list")
    public Result<List<SeckillActivityVO>> list() {
        return Result.success(seckillService.portalList());
    }

    @ApiOperation("立即秒杀")
    @PostMapping("/{itemId}/buy")
    public Result<Void> buy(@PathVariable Long itemId) {
        seckillService.buy(SecurityUtils.getCurrentUserId(), itemId);
        return Result.success();
    }
}
