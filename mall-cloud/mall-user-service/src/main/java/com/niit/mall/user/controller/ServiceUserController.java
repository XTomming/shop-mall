package com.niit.mall.user.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.mall.common.Result;
import com.niit.mall.user.dto.ServiceUserDTO;
import com.niit.mall.user.entity.SysUser;
import com.niit.mall.user.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客服账号内部接口（供 after-sale-service 通过 OpenFeign 获取在线客服）
 */
@RestController
@RequestMapping("/api/service-user")
public class ServiceUserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/online")
    public Result<List<ServiceUserDTO>> online() {
        List<SysUser> users = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getRole, "SERVICE")
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getOnline, 1));
        List<ServiceUserDTO> result = users.stream().map(u -> {
            ServiceUserDTO dto = new ServiceUserDTO();
            dto.setId(u.getId());
            dto.setNickname(u.getNickname());
            return dto;
        }).collect(Collectors.toList());
        return Result.success(result);
    }
}
