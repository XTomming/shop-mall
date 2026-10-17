package com.niit.mall.user.controller;

import com.niit.mall.common.Result;
import com.niit.mall.user.entity.MemberAddress;
import com.niit.mall.user.mapper.MemberAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收货地址内部接口（供 order-service 通过 OpenFeign 调用）
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private MemberAddressMapper addressMapper;

    @GetMapping("/{id}")
    public Result<MemberAddress> getById(@PathVariable Long id) {
        return Result.success(addressMapper.selectById(id));
    }
}
