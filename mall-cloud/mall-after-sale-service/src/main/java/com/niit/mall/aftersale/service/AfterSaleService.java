package com.niit.mall.aftersale.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.aftersale.client.OrderClient;
import com.niit.mall.aftersale.dto.AfterSaleApplyDTO;
import com.niit.mall.aftersale.dto.AfterSaleHandleDTO;
import com.niit.mall.aftersale.entity.AfterSale;
import com.niit.mall.aftersale.entity.Orders;
import com.niit.mall.aftersale.mapper.AfterSaleMapper;
import com.niit.mall.aftersale.vo.AfterSaleVO;
import com.niit.mall.common.BusinessException;
import com.niit.mall.common.Result;
import com.niit.mall.common.enums.AfterSaleStatus;
import com.niit.mall.common.enums.AfterSaleType;
import com.niit.mall.common.enums.OrderStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 售后单服务（退款/发票/纠纷；退款办结通过 Feign 联动订单服务）
 */
@Service
public class AfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Autowired
    private OrderClient orderClient;

    @Transactional(rollbackFor = Exception.class)
    public AfterSale applyRefund(Long memberId, AfterSaleApplyDTO dto) {
        Orders order = getPaidOrder(memberId, dto.getOrderId());
        checkNoOpenTicket(order.getId(), AfterSaleType.REFUND.getCode());
        AfterSale afterSale = new AfterSale();
        afterSale.setAfterSaleNo(genNo());
        afterSale.setType(AfterSaleType.REFUND.getCode());
        afterSale.setMemberId(memberId);
        afterSale.setOrderId(order.getId());
        afterSale.setReason(dto.getReason());
        afterSale.setImages(dto.getImages());
        afterSale.setRefundAmount(order.getPayAmount());
        afterSale.setStatus(AfterSaleStatus.PENDING.getCode());
        afterSaleMapper.insert(afterSale);
        return afterSale;
    }

    @Transactional(rollbackFor = Exception.class)
    public AfterSale applyInvoice(Long memberId, AfterSaleApplyDTO dto) {
        Orders order = getPaidOrder(memberId, dto.getOrderId());
        checkNoOpenTicket(order.getId(), AfterSaleType.INVOICE.getCode());
        AfterSale afterSale = new AfterSale();
        afterSale.setAfterSaleNo(genNo());
        afterSale.setType(AfterSaleType.INVOICE.getCode());
        afterSale.setMemberId(memberId);
        afterSale.setOrderId(order.getId());
        afterSale.setReason(dto.getReason());
        afterSale.setInvoiceType(dto.getInvoiceType());
        afterSale.setInvoiceTitle(dto.getInvoiceTitle());
        afterSale.setTaxNo(dto.getTaxNo());
        afterSale.setEmail(dto.getEmail());
        afterSale.setStatus(AfterSaleStatus.PENDING.getCode());
        afterSaleMapper.insert(afterSale);
        return afterSale;
    }

    @Transactional(rollbackFor = Exception.class)
    public AfterSale escalate(Long memberId, Long refundId) {
        AfterSale refund = afterSaleMapper.selectById(refundId);
        if (refund == null || !refund.getMemberId().equals(memberId)) {
            throw new BusinessException("退款单不存在");
        }
        if (refund.getType() != AfterSaleType.REFUND.getCode()) {
            throw new BusinessException("仅退款单可升级为纠纷");
        }
        if (refund.getStatus() != AfterSaleStatus.REJECTED.getCode()) {
            throw new BusinessException("仅被拒绝的退款单可升级为纠纷");
        }
        Long count = afterSaleMapper.selectCount(Wrappers.<AfterSale>lambdaQuery()
                .eq(AfterSale::getSourceRefundId, refundId));
        if (count != null && count > 0) {
            throw new BusinessException("该退款单已升级为纠纷");
        }
        AfterSale dispute = new AfterSale();
        dispute.setAfterSaleNo(genNo());
        dispute.setType(AfterSaleType.DISPUTE.getCode());
        dispute.setMemberId(memberId);
        dispute.setOrderId(refund.getOrderId());
        dispute.setReason("退款被拒后的纠纷：" + (refund.getReason() == null ? "" : refund.getReason()));
        dispute.setRefundAmount(refund.getRefundAmount());
        dispute.setImages(refund.getImages());
        dispute.setSourceRefundId(refundId);
        dispute.setStatus(AfterSaleStatus.PENDING.getCode());
        afterSaleMapper.insert(dispute);
        return dispute;
    }

    public void cancel(Long memberId, Long id) {
        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null || !afterSale.getMemberId().equals(memberId)) {
            throw new BusinessException("售后单不存在");
        }
        if (afterSale.getStatus() != AfterSaleStatus.PENDING.getCode()) {
            throw new BusinessException("仅待处理的售后单可撤销");
        }
        AfterSale update = new AfterSale();
        update.setId(id);
        update.setStatus(AfterSaleStatus.CLOSED.getCode());
        afterSaleMapper.updateById(update);
    }

    public Page<AfterSaleVO> portalList(Long memberId, Integer type, long pageNum, long pageSize) {
        Page<AfterSale> page = afterSaleMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<AfterSale>lambdaQuery()
                        .eq(AfterSale::getMemberId, memberId)
                        .eq(type != null, AfterSale::getType, type)
                        .orderByDesc(AfterSale::getCreateTime));
        return toVOPage(page);
    }

    public Page<AfterSaleVO> page(Integer type, Integer status, long pageNum, long pageSize) {
        Page<AfterSale> page = afterSaleMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<AfterSale>lambdaQuery()
                        .eq(type != null, AfterSale::getType, type)
                        .eq(status != null, AfterSale::getStatus, status)
                        .orderByAsc(AfterSale::getStatus)
                        .orderByDesc(AfterSale::getCreateTime));
        return toVOPage(page);
    }

    public void serviceAccept(Long serviceId, Long id) {
        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null) {
            throw new BusinessException("售后单不存在");
        }
        if (afterSale.getStatus() != AfterSaleStatus.PENDING.getCode()) {
            throw new BusinessException("仅待处理售后单可接单");
        }
        AfterSale update = new AfterSale();
        update.setId(id);
        update.setStatus(AfterSaleStatus.PROCESSING.getCode());
        update.setServiceId(serviceId);
        afterSaleMapper.updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void serviceComplete(Long serviceId, Long id, AfterSaleHandleDTO dto) {
        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null || afterSale.getStatus() != AfterSaleStatus.PROCESSING.getCode()) {
            throw new BusinessException("仅处理中的售后单可办结");
        }
        if (afterSale.getType() == AfterSaleType.REFUND.getCode()) {
            // 跨服务退款：order-service 改订单状态 + 恢复库存
            orderClient.refund(afterSale.getOrderId());
        }
        AfterSale update = new AfterSale();
        update.setId(id);
        update.setStatus(AfterSaleStatus.COMPLETED.getCode());
        update.setServiceId(serviceId);
        update.setHandleRemark(dto == null ? null : dto.getHandleRemark());
        update.setHandleTime(LocalDateTime.now());
        if (afterSale.getType() == AfterSaleType.INVOICE.getCode()) {
            update.setInvoiceNo(generateInvoiceNo());
        }
        afterSaleMapper.updateById(update);
    }

    public void serviceReject(Long serviceId, Long id, AfterSaleHandleDTO dto) {
        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null || afterSale.getStatus() != AfterSaleStatus.PROCESSING.getCode()) {
            throw new BusinessException("仅处理中的售后单可拒绝");
        }
        AfterSale update = new AfterSale();
        update.setId(id);
        update.setStatus(AfterSaleStatus.REJECTED.getCode());
        update.setServiceId(serviceId);
        update.setHandleRemark(dto == null ? null : dto.getHandleRemark());
        update.setHandleTime(LocalDateTime.now());
        afterSaleMapper.updateById(update);
    }

    private Orders getPaidOrder(Long memberId, Long orderId) {
        Result<Orders> r = orderClient.getOrder(orderId);
        Orders order = r == null ? null : r.getData();
        if (order == null || order.getMemberId() == null || !order.getMemberId().equals(memberId)) {
            throw new BusinessException("订单不存在");
        }
        int status = order.getStatus();
        if (status != OrderStatus.PENDING_DELIVERY.getCode()
                && status != OrderStatus.PENDING_RECEIPT.getCode()
                && status != OrderStatus.COMPLETED.getCode()) {
            throw new BusinessException("仅已支付订单可申请售后");
        }
        return order;
    }

    private void checkNoOpenTicket(Long orderId, int type) {
        Long count = afterSaleMapper.selectCount(Wrappers.<AfterSale>lambdaQuery()
                .eq(AfterSale::getOrderId, orderId)
                .eq(AfterSale::getType, type)
                .in(AfterSale::getStatus, AfterSaleStatus.PENDING.getCode(), AfterSaleStatus.PROCESSING.getCode()));
        if (count != null && count > 0) {
            throw new BusinessException("该订单已有同类售后申请处理中");
        }
    }

    private String genNo() {
        return "AS" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(4);
    }

    private String generateInvoiceNo() {
        for (int i = 0; i < 10; i++) {
            String no = "FP" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
            Long count = afterSaleMapper.selectCount(Wrappers.<AfterSale>lambdaQuery()
                    .eq(AfterSale::getInvoiceNo, no));
            if (count == null || count == 0) {
                return no;
            }
        }
        return "FP" + System.currentTimeMillis() + RandomUtil.randomNumbers(6);
    }

    private Page<AfterSaleVO> toVOPage(Page<AfterSale> page) {
        Page<AfterSaleVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return result;
    }

    private AfterSaleVO toVO(AfterSale afterSale) {
        if (afterSale == null) {
            return null;
        }
        AfterSaleVO vo = new AfterSaleVO();
        BeanUtils.copyProperties(afterSale, vo);
        vo.setTypeDesc(AfterSaleType.descOf(afterSale.getType()));
        vo.setStatusDesc(AfterSaleStatus.descOf(afterSale.getStatus()));
        return vo;
    }
}
