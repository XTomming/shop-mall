package com.niit.shopmall.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.entity.SysUser;
import com.niit.shopmall.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动初始化：无管理员时创建默认管理员 admin/123456
 */
@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Long count = sysUserMapper.selectCount(null);
        if (count == null || count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("超级管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setOnline(0);
            sysUserMapper.insert(admin);
            log.info("已初始化默认管理员账号：admin / 123456");
        }
        // 初始化默认客服账号（service / 123456）
        Long serviceCount = sysUserMapper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getRole, "SERVICE"));
        if (serviceCount == null || serviceCount == 0) {
            SysUser service = new SysUser();
            service.setUsername("service");
            service.setPassword(passwordEncoder.encode("123456"));
            service.setNickname("在线客服");
            service.setRole("SERVICE");
            service.setStatus(1);
            service.setOnline(0);
            sysUserMapper.insert(service);
            log.info("已初始化默认客服账号：service / 123456");
        }
    }
}
