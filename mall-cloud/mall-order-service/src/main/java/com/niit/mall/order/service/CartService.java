package com.niit.mall.order.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.mall.common.BusinessException;
import com.niit.mall.common.Result;
import com.niit.mall.order.client.ProductClient;
import com.niit.mall.order.dto.CartAddDTO;
import com.niit.mall.order.dto.CartUpdateDTO;
import com.niit.mall.order.entity.Cart;
import com.niit.mall.order.entity.Product;
import com.niit.mall.order.mapper.CartMapper;
import com.niit.mall.order.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务（商品信息通过 Feign 从 product-service 获取）
 */
@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductClient productClient;

    public void add(Long memberId, CartAddDTO dto) {
        Result<Product> pr = productClient.getById(dto.getProductId());
        Product product = pr == null ? null : pr.getData();
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        int quantity = dto.getQuantity() == null ? 1 : dto.getQuantity();
        if (quantity <= 0) {
            throw new BusinessException("数量必须大于0");
        }
        Cart exist = cartMapper.selectOne(Wrappers.<Cart>lambdaQuery()
                .eq(Cart::getMemberId, memberId)
                .eq(Cart::getProductId, dto.getProductId()));
        if (exist != null) {
            int newQuantity = exist.getQuantity() + quantity;
            if (product.getStock() != null && newQuantity > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            Cart update = new Cart();
            update.setId(exist.getId());
            update.setQuantity(newQuantity);
            cartMapper.updateById(update);
        } else {
            if (product.getStock() != null && quantity > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            Cart cart = new Cart();
            cart.setMemberId(memberId);
            cart.setProductId(dto.getProductId());
            cart.setQuantity(quantity);
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
    }

    public List<CartVO> list(Long memberId) {
        List<Cart> carts = cartMapper.selectList(Wrappers.<Cart>lambdaQuery()
                .eq(Cart::getMemberId, memberId)
                .orderByDesc(Cart::getCreateTime));
        List<CartVO> result = new ArrayList<>();
        for (Cart cart : carts) {
            Result<Product> pr = productClient.getById(cart.getProductId());
            Product product = pr == null ? null : pr.getData();
            if (product == null) {
                continue;
            }
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setProductId(cart.getProductId());
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
            vo.setPrice(product.getPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setChecked(cart.getChecked());
            vo.setStock(product.getStock());
            result.add(vo);
        }
        return result;
    }

    public void update(Long memberId, Long id, CartUpdateDTO dto) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getMemberId().equals(memberId)) {
            throw new BusinessException("购物车记录不存在");
        }
        Cart update = new Cart();
        update.setId(id);
        if (dto.getQuantity() != null) {
            if (dto.getQuantity() <= 0) {
                throw new BusinessException("数量必须大于0");
            }
            Result<Product> pr = productClient.getById(cart.getProductId());
            Product product = pr == null ? null : pr.getData();
            if (product != null && product.getStock() != null && dto.getQuantity() > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            update.setQuantity(dto.getQuantity());
        }
        if (dto.getChecked() != null) {
            update.setChecked(dto.getChecked());
        }
        cartMapper.updateById(update);
    }

    public void delete(Long memberId, Long id) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getMemberId().equals(memberId)) {
            throw new BusinessException("购物车记录不存在");
        }
        cartMapper.deleteById(id);
    }
}
