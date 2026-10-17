package com.niit.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.common.Result;
import com.niit.mall.product.entity.Product;
import com.niit.mall.product.service.ProductService;
import com.niit.mall.product.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品接口
 */
@RestController
@RequestMapping("/api/portal/product")
public class PortalProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String sort) {
        return Result.success(productService.portalPage(pageNum, pageSize, categoryId, keyword, sort));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.portalDetail(id));
    }
}
