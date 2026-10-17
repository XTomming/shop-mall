package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.BannerDTO;
import com.niit.shopmall.entity.Banner;
import com.niit.shopmall.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 后台轮播图管理接口
 */
@Api(tags = "后台-轮播图管理")
@RestController
@RequestMapping("/api/admin/banner")
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("轮播图分页")
    @GetMapping("/page")
    public Result<Page<Banner>> page(@RequestParam(defaultValue = "1") long pageNum,
                                     @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(bannerService.adminPage(pageNum, pageSize));
    }

    @ApiOperation("新增轮播图")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody BannerDTO dto) {
        bannerService.adminAdd(dto);
        return Result.success();
    }

    @ApiOperation("修改轮播图")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody BannerDTO dto) {
        bannerService.adminUpdate(id, dto);
        return Result.success();
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.adminDelete(id);
        return Result.success();
    }
}
