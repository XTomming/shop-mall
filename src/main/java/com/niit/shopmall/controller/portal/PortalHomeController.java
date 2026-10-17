package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.service.HomeService;
import com.niit.shopmall.vo.HomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台首页接口
 */
@Api(tags = "前台-首页")
@RestController
@RequestMapping("/api/portal")
public class PortalHomeController {

    @Autowired
    private HomeService homeService;

    @ApiOperation("首页数据")
    @GetMapping("/home")
    public Result<HomeVO> home() {
        return Result.success(homeService.home());
    }
}
