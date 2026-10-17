package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.AddressDTO;
import com.niit.shopmall.entity.MemberAddress;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.AddressService;
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
 * 前台收货地址接口
 */
@Api(tags = "前台-收货地址")
@RestController
@RequestMapping("/api/portal/address")
public class PortalAddressController {

    @Autowired
    private AddressService addressService;

    @ApiOperation("地址列表")
    @GetMapping("/list")
    public Result<List<MemberAddress>> list() {
        return Result.success(addressService.list(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("新增地址")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody AddressDTO dto) {
        addressService.add(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @ApiOperation("修改地址")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AddressDTO dto) {
        addressService.update(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }

    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
