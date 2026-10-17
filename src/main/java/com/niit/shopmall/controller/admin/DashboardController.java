package com.niit.shopmall.controller.admin;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.service.DashboardService;
import com.niit.shopmall.vo.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台统计接口
 */
@Api(tags = "后台-统计")
@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @ApiOperation("首页统计")
    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        return Result.success(dashboardService.dashboard());
    }
}
