package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.dto.NoticeDTO;
import com.niit.shopmall.entity.Notice;
import com.niit.shopmall.mapper.NoticeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public Page<Notice> adminPage(long pageNum, long pageSize) {
        return noticeMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Notice>lambdaQuery().orderByDesc(Notice::getCreateTime));
    }

    public void adminAdd(NoticeDTO dto) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        noticeMapper.insert(notice);
    }

    public void adminUpdate(Long id, NoticeDTO dto) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setId(id);
        noticeMapper.updateById(notice);
    }

    public void adminDelete(Long id) {
        noticeMapper.deleteById(id);
    }

    /**
     * 已发布的公告（供首页）
     */
    public List<Notice> listEnabled() {
        return noticeMapper.selectList(Wrappers.<Notice>lambdaQuery()
                .eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getCreateTime)
                .last("limit 5"));
    }
}
