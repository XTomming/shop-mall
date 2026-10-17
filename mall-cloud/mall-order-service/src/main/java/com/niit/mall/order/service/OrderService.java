package com.niit.mall.order.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.seata.spring.annotation.GlobalTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.mall.common.BusinessException;
import com.niit.mall.common.Result;
import com.niit.mall.common.enums.OrderStatus;
import com.niit.mall.order.client.ProductClient;
import com.niit.mall.order.client.UserClient;
import com.niit.mall.order.dto.MockPayDTO;
import com.niit.mall.order.dto.OrderSubmitDTO;
import com.niit.mall.order.entity.Cart;
import com.niit.mall.order.entity.MemberAddress;
import com.niit.mall.order.entity.OrderItem;
import com.niit.mall.order.entity.Orders;
import com.niit.mall.order.entity.Product;
import com.niit.mall.order.mapper.CartMapper;
import com.niit.mall.order.mapper.OrderItemMapper;
import com.niit.mall.order.mapper.OrdersMapper;
import com.niit.mall.order.vo.OrderItemVO;
import com.niit.mall.order.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务（购物车/下单/支付/收货；跨服务：Feign 调 product 扣库存、调 user 取地址）
 */
@Service
public class OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private UserClient userClient;

    /**
     * 提交订单（Feign 跨服务：取地址、查商品、扣库存）
     * Seata 全局事务：下单跨服务调用纳入分布式事务，扣库存失败则全局回滚
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public Orders submit(Long memberId, OrderSubmitDTO dto) {
        Result<MemberAddress> addrResult = userClient.getAddress(dto.getAddressId());
        MemberAddress address = addrResult == null ? null : addrResult.getData();
        if (address == null || address.getMemberId() == null || !address.getMemberId().equals(memberId)) {
            throw new BusinessException("收货地址不存在");
        }
        List<Cart> carts = cartMapper.selectBatchIds(dto.getCartIds());
        if (carts.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }
        for (Cart cart : carts) {
            if (!cart.getMemberId().equals(memberId)) {
                throw new BusinessException("购物车数据异常");
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (Cart cart : carts) {
            Result<Product> pr = productClient.getById(cart.getProductId());
            Product product = pr == null ? null : pr.getData();
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException("商品已下架，请刷新购物车");
            }
            int quantity = cart.getQuantity();
            if (product.getStock() == null || product.getStock() < quantity) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }
            // 跨服务扣库存（product-service 原子校验）
            Result<Boolean> deduct = productClient.deduct(product.getId(), quantity);
            if (deduct == null || deduct.getData() == null || !deduct.getData()) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            items.add(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        Orders order = new Orders();
        order.setOrderNo(DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(4));
        order.setMemberId(memberId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(buildFullAddress(address));
        ordersMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        cartMapper.deleteBatchIds(dto.getCartIds());
        return order;
    }

    /**
     * 模拟支付
     */
    @Transactional(rollbackFor = Exception.class)
    public void mockPay(Long memberId, MockPayDTO dto) {
        Orders order = ordersMapper.selectById(dto.getOrderId());
        if (order == null || !order.getMemberId().equals(memberId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("订单状态不允许支付");
        }
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.PENDING_DELIVERY.getCode());
        update.setPayType(dto.getPayType() == null ? 3 : dto.getPayType());
        update.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    /**
     * 我的订单
     */
    public Page<OrderVO> portalList(Long memberId, Integer status, long pageNum, long pageSize) {
        Page<Orders> page = ordersMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Orders>lambdaQuery()
                        .eq(Orders::getMemberId, memberId)
                        .eq(status != null, Orders::getStatus, status)
                        .orderByDesc(Orders::getCreateTime));
        return toVOPage(page);
    }

    /**
     * 订单详情
     */
    public OrderVO portalDetail(Long memberId, Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getMemberId().equals(memberId)) {
            throw new BusinessException("订单不存在");
        }
        return toVO(order);
    }

    /**
     * 确认收货
     */
    public void receive(Long memberId, Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getMemberId().equals(memberId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_RECEIPT.getCode()) {
            throw new BusinessException("订单状态不允许收货");
        }
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(OrderStatus.COMPLETED.getCode());
        update.setReceiveTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    private Page<OrderVO> toVOPage(Page<Orders> page) {
        Page<OrderVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return result;
    }

    private OrderVO toVO(Orders order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusDesc(OrderStatus.descOf(order.getStatus()));
        List<OrderItem> items = orderItemMapper.selectList(Wrappers.<OrderItem>lambdaQuery()
                .eq(OrderItem::getOrderId, order.getId()));
        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);
        return vo;
    }

    private String buildFullAddress(MemberAddress address) {
        return (address.getProvince() == null ? "" : address.getProvince())
                + (address.getCity() == null ? "" : address.getCity())
                + (address.getDistrict() == null ? "" : address.getDistrict())
                + address.getDetailAddress();
    }
}
