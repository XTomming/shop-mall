package com.niit.mall.aftersale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 售后 + 客服聊天服务
 */
@SpringBootApplication(scanBasePackages = "com.niit.mall")
@EnableFeignClients(basePackages = "com.niit.mall.aftersale.client")
@MapperScan("com.niit.mall.aftersale.mapper")
public class AfterSaleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfterSaleServiceApplication.class, args);
    }
}
