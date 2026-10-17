package com.niit.shopmall.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.common.enums.OrderStatus;
import com.niit.shopmall.dto.MockPayDTO;
import com.niit.shopmall.dto.OrderSubmitDTO;
import com.niit.shopmall.entity.Cart;
import com.niit.shopmall.entity.MemberAddress;
import com.niit.shopmall.entity.OrderItem;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.mapper.CartMapper;
import com.niit.shopmall.mapper.MemberAddressMapper;
import com.niit.shopmall.mapper.OrderItemMapper;
import com.niit.shopmall.mapper.OrdersMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.mq.OrderTimeoutProducer;
import com.niit.shopmall.vo.OrderItemVO;
import com.niit.shopmall.vo.OrderVO;
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
 * 订单服务（前台下单/支付/收货 + 后台发货/关闭 + 超时取消）
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
    private ProductMapper productMapper;

    @Autowired
    private MemberAddressMapper addressMapper;

    /** MQ 关闭时该 Bean 不存在 */
    @Autowired(required = false)
    private OrderTimeoutProducer orderTimeoutProducer;

    @Autowired
    private SeckillService seckillService;

    /**
     * 提交订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Orders submit(Long memberId, OrderSubmitDTO dto) {
        MemberAddress address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getMemberId().equals(memberId)) {
            throw new BusinessException("收货地址不存在");
        }
        List<Cart> carts = cartMapper.selectBatchIds(dto.getCartIds());
        if (carts.isEmpty()) {
            throw new BusinessException("请选择要结算的商品");
        }
        // 校验购物车归属
        for (Cart cart : carts) {
            if (!cart.getMemberId().equals(memberId)) {
                throw new BusinessException("购物车数据异常");
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException("商品已下架，请刷新购物车");
            }
            int quantity = cart.getQuantity();
            if (product.getStock() < quantity) {
                throw new BusinessException("商品[" + product.getName() + "]库存不足");
            }
            // 扣减库存、增加销量（带乐观校验）
            int updated = productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                    .setSql("stock = stock - " + quantity)
                    .setSql("sales = sales + " + quantity)
                    .eq(Product::getId, product.getId())
                    .ge(Product::getStock, quantity));
            if (updated == 0) {
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

        // 生成订单
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

        // 保存订单明细
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 删除已下单购物车记录
        cartMapper.deleteBatchIds(dto.getCartIds());

        // 发送订单超时延迟消息（MQ 开启时）
        if (orderTimeoutProducer != null) {
            orderTimeoutProducer.sendOrderTimeout(order.getId());
        }
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
     * 前台：我的订单
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
     * 前台：订单详情
     */
    public OrderVO portalDetail(Long memberId, Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null || !order.getMemberId().equals(memberId)) {
            throw new BusinessException("订单不存在");
        }
        return toVO(order);
    }

    /**
     * 前台：确认收货
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

    /**
     * 后台：订单分页
     */
    public Page<OrderVO> adminPage(Integer status, String orderNo, long pageNum, long pageSize) {
        Page<Orders> page = ordersMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Orders>lambdaQuery()
                        .eq(status != null, Orders::getStatus, status)
                        .like(orderNo != null && !orderNo.isEmpty(), Orders::getOrderNo, orderNo)
                        .orderByDesc(Orders::getCreateTime));
        return toVOPage(page);
    }

    /**
     * 后台：订单详情
     */
    public OrderVO adminDetail(Long id) {
        return toVO(ordersMapper.selectById(id));
    }

    /**
     * 后台：发货
     */
    public void adminDelivery(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_DELIVERY.getCode()) {
            throw new BusinessException("仅待发货订单可发货");
        }
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(OrderStatus.PENDING_RECEIPT.getCode());
        update.setDeliveryTime(LocalDateTime.now());
        ordersMapper.updateById(update);
    }

    /**
     * 后台：关闭订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void adminClose(Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException("仅待付款订单可关闭");
        }
        closeOrder(order);
    }

    /**
     * 超时取消（MQ 消费者与定时任务共用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrder(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null || order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            return;
        }
        closeOrder(order);
    }

    private void closeOrder(Orders order) {
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.CLOSED.getCode());
        ordersMapper.updateById(update);
        restoreStock(order);
    }

    /**
     * 退款完成：订单置为已退款并恢复库存（供售后退款办结调用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void completeRefund(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        int status = order.getStatus();
        if (status != OrderStatus.PENDING_DELIVERY.getCode()
                && status != OrderStatus.PENDING_RECEIPT.getCode()
                && status != OrderStatus.COMPLETED.getCode()) {
            throw new BusinessException("订单状态不允许退款");
        }
        Orders update = new Orders();
        update.setId(order.getId());
        update.setStatus(OrderStatus.REFUNDED.getCode());
        ordersMapper.updateById(update);
        restoreStock(order);
    }

    /**
     * 回补库存、减少销量（关闭订单与退款完成共用）
     */
    private void restoreStock(Orders order) {
        List<OrderItem> items = orderItemMapper.selectList(Wrappers.<OrderItem>lambdaQuery()
                .eq(OrderItem::getOrderId, order.getId()));
        for (OrderItem item : items) {
            productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                    .setSql("stock = stock + " + item.getQuantity())
                    .setSql("sales = sales - " + item.getQuantity())
                    .eq(Product::getId, item.getProductId()));
        }
        // 秒杀单回补秒杀库存与限购
        seckillService.restoreSeckillOrder(order);
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
