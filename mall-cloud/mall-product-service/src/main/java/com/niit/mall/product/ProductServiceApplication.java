package com.niit.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品/分类/评价/秒杀/内容服务
 */
@SpringBootApplication(scanBasePackages = "com.niit.mall")
@MapperScan("com.niit.mall.product.mapper")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
