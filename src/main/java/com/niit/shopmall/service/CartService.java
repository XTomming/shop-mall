package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.CartAddDTO;
import com.niit.shopmall.dto.CartUpdateDTO;
import com.niit.shopmall.entity.Cart;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.mapper.CartMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务
 */
@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 加入购物车
     */
    public void add(Long memberId, CartAddDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
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
            if (newQuantity > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            Cart update = new Cart();
            update.setId(exist.getId());
            update.setQuantity(newQuantity);
            cartMapper.updateById(update);
        } else {
            if (quantity > product.getStock()) {
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

    /**
     * 购物车列表
     */
    public List<CartVO> list(Long memberId) {
        List<Cart> carts = cartMapper.selectList(Wrappers.<Cart>lambdaQuery()
                .eq(Cart::getMemberId, memberId)
                .orderByDesc(Cart::getCreateTime));
        List<CartVO> result = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
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

    /**
     * 修改购物车（数量 / 选中）
     */
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
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null && dto.getQuantity() > product.getStock()) {
                throw new BusinessException("库存不足");
            }
            update.setQuantity(dto.getQuantity());
        }
        if (dto.getChecked() != null) {
            update.setChecked(dto.getChecked());
        }
        cartMapper.updateById(update);
    }

    /**
     * 删除购物车
     */
    public void delete(Long memberId, Long id) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getMemberId().equals(memberId)) {
            throw new BusinessException("购物车记录不存在");
        }
        cartMapper.deleteById(id);
    }
}
