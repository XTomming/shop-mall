package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.AdminDTO;
import com.niit.shopmall.entity.SysUser;
import com.niit.shopmall.service.SysUserService;
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
 * 后台客服管理接口（客服账号增删改查）
 */
@Api(tags = "后台-客服管理")
@RestController
@RequestMapping("/api/admin/service-user")
public class AdminServiceUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("客服分页")
    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(sysUserService.servicePage(pageNum, pageSize, keyword));
    }

    @ApiOperation("新增客服")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody AdminDTO dto) {
        sysUserService.serviceAdd(dto);
        return Result.success();
    }

    @ApiOperation("修改客服")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AdminDTO dto) {
        sysUserService.serviceUpdate(id, dto);
        return Result.success();
    }

    @ApiOperation("删除客服")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.adminDelete(id);
        return Result.success();
    }

    @ApiOperation("启用/禁用客服")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        sysUserService.adminUpdateStatus(id, status);
        return Result.success();
    }
}
