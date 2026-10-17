package com.niit.mall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 会员/登录/地址/系统管理服务
 */
@SpringBootApplication(scanBasePackages = "com.niit.mall")
@MapperScan("com.niit.mall.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
