package com.niit.mall.order.controller;

import com.niit.mall.common.Result;
import com.niit.mall.order.dto.CartAddDTO;
import com.niit.mall.order.dto.CartUpdateDTO;
import com.niit.mall.order.service.CartService;
import com.niit.mall.order.vo.CartVO;
import com.niit.mall.security.SecurityUtils;
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
@RestController
@RequestMapping("/api/portal/cart")
public class PortalCartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public Result<Void> add(@Valid @RequestBody CartAddDTO dto) {
        cartService.add(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<CartVO>> list() {
        return Result.success(cartService.list(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CartUpdateDTO dto) {
        cartService.update(SecurityUtils.getCurrentUserId(), id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cartService.delete(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
