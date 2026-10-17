package com.niit.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 购物车/订单/模拟支付服务
 */
@SpringBootApplication(scanBasePackages = "com.niit.mall")
@EnableFeignClients(basePackages = "com.niit.mall.order.client")
@MapperScan("com.niit.mall.order.mapper")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
