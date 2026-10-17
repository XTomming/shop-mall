package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.dto.BannerDTO;
import com.niit.shopmall.entity.Banner;
import com.niit.shopmall.mapper.BannerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务
 */
@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    public Page<Banner> adminPage(long pageNum, long pageSize) {
        return bannerMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Banner>lambdaQuery().orderByAsc(Banner::getSort).orderByDesc(Banner::getCreateTime));
    }

    public void adminAdd(BannerDTO dto) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        bannerMapper.insert(banner);
    }

    public void adminUpdate(Long id, BannerDTO dto) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        banner.setId(id);
        bannerMapper.updateById(banner);
    }

    public void adminDelete(Long id) {
        bannerMapper.deleteById(id);
    }

    /**
     * 启用的轮播图（供首页）
     */
    public List<Banner> listEnabled() {
        return bannerMapper.selectList(Wrappers.<Banner>lambdaQuery()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort));
    }
}
