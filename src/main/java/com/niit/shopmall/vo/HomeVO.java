package com.niit.shopmall.vo;

import com.niit.shopmall.entity.Banner;
import com.niit.shopmall.entity.Notice;
import com.niit.shopmall.entity.Product;
import lombok.Data;

import java.util.List;

/**
 * 前台首页数据
 */
@Data
public class HomeVO {

    private List<Banner> banners;

    private List<Notice> notices;

    private List<CategoryVO> categories;

    private List<Product> hotProducts;
}
