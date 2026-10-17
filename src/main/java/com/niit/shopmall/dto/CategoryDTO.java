package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 分类新增/修改参数
 */
@Data
public class CategoryDTO {

    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer level;

    private Integer sort;

    private String icon;

    private Integer status;
}
