package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.common.enums.OrderStatus;
import com.niit.shopmall.dto.CommentDTO;
import com.niit.shopmall.entity.Member;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.entity.ProductComment;
import com.niit.shopmall.mapper.MemberMapper;
import com.niit.shopmall.mapper.OrdersMapper;
import com.niit.shopmall.mapper.ProductCommentMapper;
import com.niit.shopmall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品评价服务
 */
@Service
public class CommentService {

    @Autowired
    private ProductCommentMapper commentMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private MemberMapper memberMapper;

    /**
     * 发表评价（仅已完成订单可评价）
     */
    public void publish(Long memberId, CommentDTO dto) {
        Orders order = ordersMapper.selectById(dto.getOrderId());
        if (order == null || !order.getMemberId().equals(memberId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.COMPLETED.getCode()) {
            throw new BusinessException("仅已完成订单可评价");
        }
        Long exists = commentMapper.selectCount(Wrappers.<ProductComment>lambdaQuery()
                .eq(ProductComment::getOrderId, dto.getOrderId())
                .eq(ProductComment::getProductId, dto.getProductId()));
        if (exists != null && exists > 0) {
            throw new BusinessException("该商品已评价");
        }
        ProductComment comment = new ProductComment();
        comment.setProductId(dto.getProductId());
        comment.setMemberId(memberId);
        comment.setOrderId(dto.getOrderId());
        comment.setContent(dto.getContent());
        comment.setRating(dto.getRating());
        commentMapper.insert(comment);
    }

    /**
     * 某商品的评价列表（前台公开）
     */
    public List<CommentVO> listByProduct(Long productId) {
        List<ProductComment> comments = commentMapper.selectList(Wrappers.<ProductComment>lambdaQuery()
                .eq(ProductComment::getProductId, productId)
                .orderByDesc(ProductComment::getCreateTime));
        return comments.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 后台：评价分页
     */
    public Page<CommentVO> adminPage(long pageNum, long pageSize, Long productId) {
        Page<ProductComment> page = commentMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<ProductComment>lambdaQuery()
                        .eq(productId != null, ProductComment::getProductId, productId)
                        .orderByDesc(ProductComment::getCreateTime));
        Page<CommentVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return result;
    }

    public void adminDelete(Long id) {
        commentMapper.deleteById(id);
    }

    private CommentVO toVO(ProductComment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setProductId(comment.getProductId());
        vo.setMemberId(comment.getMemberId());
        vo.setContent(comment.getContent());
        vo.setRating(comment.getRating());
        vo.setCreateTime(comment.getCreateTime());
        Member member = memberMapper.selectById(comment.getMemberId());
        if (member != null) {
            vo.setNickname(member.getNickname());
            vo.setAvatar(member.getAvatar());
        }
        return vo;
    }
}
