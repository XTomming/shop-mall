package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 轮播图参数
 */
@Data
public class BannerDTO {

    private String title;

    @NotBlank(message = "图片不能为空")
    private String image;

    private String link;

    private Integer sort;

    private Integer status;
}
