package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.CartAddDTO;
import com.niit.shopmall.dto.CartUpdateDTO;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.CartService;
import com.niit.shopmall.vo.CartVO;
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
 * 前台购物车接口
 */
@Api(tags = "前台-购物车")
@RestController
@RequestMapping("/api/portal/cart")
public class PortalCartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("加入购物车")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CartAddDTO dto) {
        cartService.add(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        return Result.success(cartService.list(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("修改购物车")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CartUpdateDTO dto) {
        cartService.update(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }

    @ApiOperation("删除购物车")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartService.delete(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
