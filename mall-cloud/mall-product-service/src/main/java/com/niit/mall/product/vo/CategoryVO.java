package com.niit.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * 分类（含子分类）
 */
@Data
public class CategoryVO {

    private Long id;

    private Long parentId;

    private String name;

    private Integer level;

    private Integer sort;

    private String icon;

    private Integer status;

    private List<CategoryVO> children;
}
