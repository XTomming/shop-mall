package com.niit.mall.product.controller;

import com.niit.mall.common.Result;
import com.niit.mall.product.service.CategoryService;
import com.niit.mall.product.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台分类接口
 */
@RestController
@RequestMapping("/api/portal/category")
public class PortalCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.portalList());
    }
}
