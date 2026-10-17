package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.util.RedisUtil;
import com.niit.shopmall.vo.CategoryVO;
import com.niit.shopmall.vo.HomeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页服务（轮播图 + 公告 + 分类 + 热销商品）
 */
@Service
public class HomeService {

    private static final String CACHE_KEY = "mall:home";

    @Autowired
    private BannerService bannerService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisUtil redisUtil;

    public HomeVO home() {
        HomeVO cached = redisUtil.get(CACHE_KEY, HomeVO.class);
        if (cached != null) {
            return cached;
        }
        HomeVO vo = new HomeVO();
        vo.setBanners(bannerService.listEnabled());
        vo.setNotices(noticeService.listEnabled());
        List<CategoryVO> categories = categoryService.portalList();
        vo.setCategories(categories);
        vo.setHotProducts(listHotProducts());
        redisUtil.set(CACHE_KEY, vo, 300);
        return vo;
    }

    private List<Product> listHotProducts() {
        return productMapper.selectList(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("limit 8"));
    }
}
