package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.ProductDTO;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 后台商品管理接口
 */
@Api(tags = "后台-商品管理")
@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("商品分页")
    @GetMapping("/page")
    public Result<Page<Product>> page(@RequestParam(defaultValue = "1") long pageNum,
                                      @RequestParam(defaultValue = "10") long pageSize,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) Integer status) {
        return Result.success(productService.adminPage(pageNum, pageSize, keyword, categoryId, status));
    }

    @ApiOperation("商品详情")
    @GetMapping("/{id}")
    public Result<com.niit.shopmall.vo.ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.adminDetail(id));
    }

    @ApiOperation("新增商品")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ProductDTO dto) {
        productService.adminAdd(dto);
        return Result.success();
    }

    @ApiOperation("修改商品")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        productService.adminUpdate(id, dto);
        return Result.success();
    }

    @ApiOperation("删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.adminDelete(id);
        return Result.success();
    }

    @ApiOperation("商品上下架")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.adminUpdateStatus(id, status);
        return Result.success();
    }
}
