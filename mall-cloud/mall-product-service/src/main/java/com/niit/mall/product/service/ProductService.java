package com.niit.mall.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.common.BusinessException;
import com.niit.mall.product.entity.Product;
import com.niit.mall.product.entity.ProductCategory;
import com.niit.mall.product.entity.ProductImage;
import com.niit.mall.product.mapper.ProductCategoryMapper;
import com.niit.mall.product.mapper.ProductImageMapper;
import com.niit.mall.product.mapper.ProductMapper;
import com.niit.mall.product.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务（M1 仅前台浏览，不含后台管理；Redis 缓存后续再加）
 */
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    /**
     * 前台：商品分页列表
     */
    public Page<Product> portalPage(long pageNum, long pageSize, Long categoryId, String keyword, String sort) {
        List<Long> categoryIds = categoryId == null ? null : collectCategoryIds(categoryId);
        LambdaQueryWrapper<Product> wrapper = Wrappers.<Product>lambdaQuery().eq(Product::getStatus, 1);
        if (categoryIds != null && !categoryIds.isEmpty()) {
            wrapper.in(Product::getCategoryId, categoryIds);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword).or().like(Product::getSubtitle, keyword));
        }
        if ("sales_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getSales);
        } else if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 前台：商品详情（含图片）
     */
    public ProductVO portalDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        return toVO(product);
    }

    /**
     * 收集某分类及其所有子分类的 id
     */
    private List<Long> collectCategoryIds(Long rootId) {
        List<Long> ids = new ArrayList<>();
        ids.add(rootId);
        List<ProductCategory> children = productCategoryMapper.selectList(
                Wrappers.<ProductCategory>lambdaQuery().eq(ProductCategory::getParentId, rootId));
        for (ProductCategory child : children) {
            ids.addAll(collectCategoryIds(child.getId()));
        }
        return ids;
    }

    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        List<ProductImage> images = productImageMapper.selectList(Wrappers.<ProductImage>lambdaQuery()
                .eq(ProductImage::getProductId, product.getId())
                .orderByAsc(ProductImage::getSort));
        vo.setImages(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
        return vo;
    }
}
