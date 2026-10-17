package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.ProductDTO;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.entity.ProductCategory;
import com.niit.shopmall.entity.ProductImage;
import com.niit.shopmall.mapper.ProductCategoryMapper;
import com.niit.shopmall.mapper.ProductImageMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.util.RedisUtil;
import com.niit.shopmall.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务（前台浏览 + 后台管理）
 */
@Service
public class ProductService {

    private static final String DETAIL_CACHE_PREFIX = "mall:product:detail:";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 前台：商品分页列表
     */
    public Page<Product> portalPage(long pageNum, long pageSize, Long categoryId, String keyword, String sort) {
        List<Long> categoryIds = categoryId == null ? null : collectCategoryIds(categoryId);
        Page<Product> page = new Page<>(pageNum, pageSize);
        return productMapper.selectPage(page, buildPortalWrapper(categoryIds, keyword, sort));
    }

    /**
     * 收集某分类及其所有子分类的 id（点击大类时展示其下所有子类商品）
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

    private com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product> buildPortalWrapper(
            List<Long> categoryIds, String keyword, String sort) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product> wrapper =
                Wrappers.<Product>lambdaQuery().eq(Product::getStatus, 1);
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
        return wrapper;
    }

    /**
     * 前台：商品详情（带 Redis 缓存）
     */
    public ProductVO portalDetail(Long id) {
        String key = DETAIL_CACHE_PREFIX + id;
        ProductVO cached = redisUtil.get(key, ProductVO.class);
        if (cached != null) {
            return cached;
        }
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品不存在或已下架");
        }
        ProductVO vo = toVO(product);
        redisUtil.set(key, vo, 600);
        return vo;
    }

    /**
     * 后台：商品分页
     */
    public Page<Product> adminPage(long pageNum, long pageSize, String keyword, Long categoryId, Integer status) {
        return productMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Product>lambdaQuery()
                        .like(keyword != null && !keyword.isEmpty(), Product::getName, keyword)
                        .eq(categoryId != null, Product::getCategoryId, categoryId)
                        .eq(status != null, Product::getStatus, status)
                        .orderByDesc(Product::getCreateTime));
    }

    @Transactional(rollbackFor = Exception.class)
    public void adminAdd(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        productMapper.insert(product);
        saveImages(product.getId(), dto.getImages());
    }

    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(Long id, ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        productMapper.updateById(product);
        // 先删后插商品图片
        productImageMapper.delete(Wrappers.<ProductImage>lambdaQuery().eq(ProductImage::getProductId, id));
        saveImages(id, dto.getImages());
        redisUtil.delete(DETAIL_CACHE_PREFIX + id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(Long id) {
        productMapper.deleteById(id);
        productImageMapper.delete(Wrappers.<ProductImage>lambdaQuery().eq(ProductImage::getProductId, id));
        redisUtil.delete(DETAIL_CACHE_PREFIX + id);
    }

    public void adminUpdateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        Product update = new Product();
        update.setId(id);
        update.setStatus(status);
        productMapper.updateById(update);
        redisUtil.delete(DETAIL_CACHE_PREFIX + id);
    }

    /**
     * 后台：商品详情（含图片，供编辑回显）
     */
    public ProductVO adminDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return toVO(product);
    }

    private void saveImages(Long productId, List<String> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        int sort = 0;
        for (String url : images) {
            ProductImage image = new ProductImage();
            image.setProductId(productId);
            image.setImageUrl(url);
            image.setSort(sort++);
            productImageMapper.insert(image);
        }
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
