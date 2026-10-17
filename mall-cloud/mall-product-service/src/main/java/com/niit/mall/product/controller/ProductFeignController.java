package com.niit.mall.product.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.mall.common.Result;
import com.niit.mall.product.entity.Product;
import com.niit.mall.product.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品服务内部接口（供 order-service 通过 OpenFeign 调用）
 */
@RestController
@RequestMapping("/api/product")
public class ProductFeignController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return Result.success(productMapper.selectById(id));
    }

    /**
     * 扣减库存并增加销量（原子校验，库存不足返回 false）
     */
    @PostMapping("/{id}/deduct")
    public Result<Boolean> deduct(@PathVariable Long id, @RequestParam Integer quantity) {
        int updated = productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                .setSql("stock = stock - " + quantity)
                .setSql("sales = sales + " + quantity)
                .eq(Product::getId, id)
                .ge(Product::getStock, quantity));
        return Result.success(updated > 0);
    }

    /**
     * 恢复库存（退款完成时调用）
     */
    @PostMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id, @RequestParam Integer quantity) {
        productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                .setSql("stock = stock + " + quantity)
                .setSql("sales = sales - " + quantity)
                .eq(Product::getId, id));
        return Result.success();
    }
}
