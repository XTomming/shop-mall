package com.niit.mall.product.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.mall.product.entity.ProductCategory;
import com.niit.mall.product.mapper.ProductCategoryMapper;
import com.niit.mall.product.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务（M1 仅前台分类树）
 */
@Service
public class CategoryService {

    @Autowired
    private ProductCategoryMapper categoryMapper;

    /**
     * 前台：分类树（仅启用的）
     */
    public List<CategoryVO> portalList() {
        List<ProductCategory> list = categoryMapper.selectList(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getStatus, 1)
                .orderByAsc(ProductCategory::getSort));
        return buildTree(list);
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
