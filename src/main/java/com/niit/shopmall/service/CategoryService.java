package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.CategoryDTO;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.entity.ProductCategory;
import com.niit.shopmall.mapper.ProductCategoryMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.util.RedisUtil;
import com.niit.shopmall.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务（前台列表 + 后台管理）
 */
@Service
public class CategoryService {

    private static final String CACHE_KEY = "mall:category:list";

    @Autowired
    private ProductCategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 前台：分类树（仅启用的）
     */
    public List<CategoryVO> portalList() {
        List<CategoryVO> cached = redisUtil.get(CACHE_KEY, new com.fasterxml.jackson.core.type.TypeReference<List<CategoryVO>>() {
        });
        if (cached != null) {
            return cached;
        }
        List<ProductCategory> list = categoryMapper.selectList(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getStatus, 1)
                .orderByAsc(ProductCategory::getSort));
        List<CategoryVO> tree = buildTree(list);
        redisUtil.set(CACHE_KEY, tree, 300);
        return tree;
    }

    /**
     * 后台：分类树（全部）
     */
    public List<CategoryVO> adminList() {
        List<ProductCategory> list = categoryMapper.selectList(Wrappers.<ProductCategory>lambdaQuery()
                .orderByAsc(ProductCategory::getLevel).orderByAsc(ProductCategory::getSort));
        return buildTree(list);
    }

    public void adminAdd(CategoryDTO dto) {
        ProductCategory category = new ProductCategory();
        BeanUtils.copyProperties(dto, category);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getLevel() == null) {
            category.setLevel(category.getParentId() == 0L ? 1 : 2);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
        clearCache();
    }

    public void adminUpdate(Long id, CategoryDTO dto) {
        ProductCategory category = new ProductCategory();
        BeanUtils.copyProperties(dto, category);
        category.setId(id);
        categoryMapper.updateById(category);
        clearCache();
    }

    public void adminDelete(Long id) {
        // 有子分类不能删除
        Long childCount = categoryMapper.selectCount(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }
        // 有商品不能删除
        Long productCount = productMapper.selectCount(Wrappers.<Product>lambdaQuery()
                .eq(Product::getCategoryId, id));
        if (productCount != null && productCount > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }
        categoryMapper.deleteById(id);
        clearCache();
    }

    private void clearCache() {
        redisUtil.delete(CACHE_KEY);
    }

    private CategoryVO toVO(ProductCategory category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    private List<CategoryVO> buildTree(List<ProductCategory> list) {
        List<CategoryVO> vos = list.stream().map(this::toVO).collect(Collectors.toList());
        Map<Long, List<CategoryVO>> byParent = vos.stream().collect(Collectors.groupingBy(CategoryVO::getParentId));
        vos.forEach(vo -> vo.setChildren(byParent.get(vo.getId())));
        return vos.stream()
                .filter(vo -> vo.getParentId() == null || vo.getParentId() == 0L)
                .collect(Collectors.toList());
    }
}
