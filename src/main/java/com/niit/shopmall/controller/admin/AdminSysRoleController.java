package com.niit.shopmall.controller.admin;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.RoleDTO;
import com.niit.shopmall.entity.SysRole;
import com.niit.shopmall.service.SysRoleService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 后台角色管理接口
 */
@Api(tags = "后台-系统管理-角色")
@RestController
@RequestMapping("/api/admin/sys/role")
public class AdminSysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("角色列表")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.adminList());
    }

    @ApiOperation("新增角色")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody RoleDTO dto) {
        sysRoleService.adminAdd(dto);
        return Result.success();
    }

    @ApiOperation("修改角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        sysRoleService.adminUpdate(id, dto);
        return Result.success();
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.adminDelete(id);
        return Result.success();
    }
}
