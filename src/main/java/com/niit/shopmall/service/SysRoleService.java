package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.RoleDTO;
import com.niit.shopmall.entity.SysRole;
import com.niit.shopmall.mapper.SysRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务（系统管理）
 */
@Service
public class SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    public List<SysRole> adminList() {
        return sysRoleMapper.selectList(Wrappers.<SysRole>lambdaQuery().orderByAsc(SysRole::getId));
    }

    public void adminAdd(RoleDTO dto) {
        Long count = sysRoleMapper.selectCount(Wrappers.<SysRole>lambdaQuery()
                .eq(SysRole::getCode, dto.getCode()));
        if (count != null && count > 0) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        sysRoleMapper.insert(role);
    }

    public void adminUpdate(Long id, RoleDTO dto) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setId(id);
        sysRoleMapper.updateById(role);
    }

    public void adminDelete(Long id) {
        sysRoleMapper.deleteById(id);
    }
}
