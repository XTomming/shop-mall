package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.AdminDTO;
import com.niit.shopmall.entity.SysUser;
import com.niit.shopmall.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 管理员服务（系统管理）
 */
@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<SysUser> adminPage(long pageNum, long pageSize, String keyword) {
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<SysUser>lambdaQuery()
                        .ne(SysUser::getRole, "SERVICE")
                        .like(keyword != null && !keyword.isEmpty(), SysUser::getUsername, keyword)
                        .orderByDesc(SysUser::getCreateTime));
        // 不返回密码
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    public void adminAdd(AdminDTO dto) {
        Long count = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword() == null ? "123456" : dto.getPassword()));
        if (user.getRole() == null) {
            user.setRole("ADMIN");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        sysUserMapper.insert(user);
    }

    public void adminUpdate(Long id, AdminDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setId(id);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserMapper.updateById(user);
    }

    public void adminDelete(Long id) {
        sysUserMapper.deleteById(id);
    }

    public void adminUpdateStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserMapper.updateById(user);
    }

    /**
     * 客服分页（role=SERVICE）
     */
    public Page<SysUser> servicePage(long pageNum, long pageSize, String keyword) {
        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getRole, "SERVICE")
                        .like(keyword != null && !keyword.isEmpty(), SysUser::getUsername, keyword)
                        .orderByDesc(SysUser::getCreateTime));
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    /**
     * 新增客服账号（强制 role=SERVICE）
     */
    public void serviceAdd(AdminDTO dto) {
        Long count = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setRole("SERVICE");
        user.setPassword(passwordEncoder.encode(dto.getPassword() == null || dto.getPassword().isEmpty() ? "123456" : dto.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getOnline() == null) {
            user.setOnline(0);
        }
        sysUserMapper.insert(user);
    }

    /**
     * 修改客服（强制 role=SERVICE，防止越权改角色）
     */
    public void serviceUpdate(Long id, AdminDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setId(id);
        user.setRole("SERVICE");
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserMapper.updateById(user);
    }
}
