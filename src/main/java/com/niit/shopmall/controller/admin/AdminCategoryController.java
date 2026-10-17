package com.niit.shopmall.controller.admin;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.CategoryDTO;
import com.niit.shopmall.service.CategoryService;
import com.niit.shopmall.vo.CategoryVO;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 后台分类管理接口
 */
@Api(tags = "后台-分类管理")
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("分类列表")
    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.adminList());
    }

    @ApiOperation("新增分类")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CategoryDTO dto) {
        categoryService.adminAdd(dto);
        return Result.success();
    }

    @ApiOperation("修改分类")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        categoryService.adminUpdate(id, dto);
        return Result.success();
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.adminDelete(id);
        return Result.success();
    }
}
