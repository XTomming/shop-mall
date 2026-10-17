package com.niit.shopmall.controller.portal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.service.ProductService;
import com.niit.shopmall.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品接口
 */
@Api(tags = "前台-商品")
@RestController
@RequestMapping("/api/portal/product")
public class PortalProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("商品分页列表")
    @GetMapping("/page")
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String sort) {
        return Result.success(productService.portalPage(pageNum, pageSize, categoryId, keyword, sort));
    }

    @ApiOperation("商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.portalDetail(id));
    }
}
