package com.niit.shopmall.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.common.enums.OrderStatus;
import com.niit.shopmall.dto.SeckillActivityDTO;
import com.niit.shopmall.dto.SeckillItemBatchDTO;
import com.niit.shopmall.dto.SeckillItemDTO;
import com.niit.shopmall.entity.MemberAddress;
import com.niit.shopmall.entity.OrderItem;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.entity.SeckillActivity;
import com.niit.shopmall.entity.SeckillItem;
import com.niit.shopmall.mapper.MemberAddressMapper;
import com.niit.shopmall.mapper.OrderItemMapper;
import com.niit.shopmall.mapper.OrdersMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.mapper.SeckillActivityMapper;
import com.niit.shopmall.mapper.SeckillItemMapper;
import com.niit.shopmall.mq.OrderTimeoutProducer;
import com.niit.shopmall.mq.SeckillOrderProducer;
import com.niit.shopmall.vo.SeckillActivityVO;
import com.niit.shopmall.vo.SeckillItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 秒杀服务（场次/商品管理 + 前台秒杀）
 */
@Slf4j
@Service
public class SeckillService {

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String USER_KEY_PREFIX = "seckill:user:";

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private SeckillItemMapper itemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private MemberAddressMapper addressMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /** MQ 关闭时该 Bean 不存在 */
    @Autowired(required = false)
    private OrderTimeoutProducer orderTimeoutProducer;

    @Autowired(required = false)
    private SeckillOrderProducer seckillOrderProducer;

    // ==================== 后台：场次管理 ====================

    public Page<SeckillActivity> adminActivityPage(long pageNum, long pageSize) {
        return activityMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<SeckillActivity>lambdaQuery().orderByDesc(SeckillActivity::getCreateTime));
    }

    public void adminAddActivity(SeckillActivityDTO dto) {
        SeckillActivity activity = new SeckillActivity();
        BeanUtils.copyProperties(dto, activity);
        if (activity.getStatus() == null) {
            activity.setStatus(1);
        }
        activityMapper.insert(activity);
    }

    public void adminUpdateActivity(Long id, SeckillActivityDTO dto) {
        SeckillActivity activity = new SeckillActivity();
        BeanUtils.copyProperties(dto, activity);
        activity.setId(id);
        activityMapper.updateById(activity);
    }

    public void adminDeleteActivity(Long id) {
        activityMapper.deleteById(id);
        itemMapper.delete(Wrappers.<SeckillItem>lambdaQuery().eq(SeckillItem::getActivityId, id));
    }

    // ==================== 后台：秒杀商品管理 ====================

    public Page<SeckillItemVO> adminItemPage(Long activityId, long pageNum, long pageSize) {
        Page<SeckillItem> page = itemMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<SeckillItem>lambdaQuery()
                        .eq(activityId != null, SeckillItem::getActivityId, activityId)
                        .orderByDesc(SeckillItem::getCreateTime));
        Page<SeckillItemVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toItemVO).collect(Collectors.toList()));
        return result;
    }

    public void adminAddItem(SeckillItemDTO dto) {
        SeckillItem item = new SeckillItem();
        BeanUtils.copyProperties(dto, item);
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        if (item.getLimitPerUser() == null) {
            item.setLimitPerUser(1);
        }
        itemMapper.insert(item);
    }

    /**
     * 批量新增秒杀商品（一次多选多个商品）
     */
    public void adminAddItems(SeckillItemBatchDTO dto) {
        for (SeckillItemBatchDTO.Item it : dto.getItems()) {
            SeckillItem item = new SeckillItem();
            item.setActivityId(dto.getActivityId());
            item.setProductId(it.getProductId());
            item.setSeckillPrice(it.getSeckillPrice());
            item.setSeckillStock(it.getSeckillStock());
            item.setLimitPerUser(it.getLimitPerUser() == null ? 1 : it.getLimitPerUser());
            item.setStatus(1);
            itemMapper.insert(item);
        }
    }

    public void adminUpdateItem(Long id, SeckillItemDTO dto) {
        SeckillItem item = new SeckillItem();
        BeanUtils.copyProperties(dto, item);
        item.setId(id);
        itemMapper.updateById(item);
        // 库存可能变化，删除缓存让下次重新预热
        safeDelete(stockKey(id));
    }

    public void adminDeleteItem(Long id) {
        itemMapper.deleteById(id);
        safeDelete(stockKey(id));
    }

    // ==================== 前台：秒杀列表 ====================

    public List<SeckillActivityVO> portalList() {
        List<SeckillActivity> activities = activityMapper.selectList(Wrappers.<SeckillActivity>lambdaQuery()
                .eq(SeckillActivity::getStatus, 1)
                .orderByAsc(SeckillActivity::getStartTime));
        List<SeckillActivityVO> result = new ArrayList<>();
        for (SeckillActivity activity : activities) {
            SeckillActivityVO vo = new SeckillActivityVO();
            BeanUtils.copyProperties(activity, vo);
            List<SeckillItem> items = itemMapper.selectList(Wrappers.<SeckillItem>lambdaQuery()
                    .eq(SeckillItem::getActivityId, activity.getId())
                    .eq(SeckillItem::getStatus, 1)
                    .orderByAsc(SeckillItem::getId));
            vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));
            result.add(vo);
        }
        return result;
    }

    // ==================== 前台：秒杀抢购 ====================

    public void buy(Long memberId, Long itemId) {
        SeckillItem item = itemMapper.selectById(itemId);
        if (item == null || item.getStatus() == null || item.getStatus() != 1) {
            throw new BusinessException("秒杀商品不存在");
        }
        SeckillActivity activity = activityMapper.selectById(item.getActivityId());
        if (activity == null || activity.getStatus() == null || activity.getStatus() != 1) {
            throw new BusinessException("秒杀场次不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            throw new BusinessException("秒杀尚未开始");
        }
        if (now.isAfter(activity.getEndTime())) {
            throw new BusinessException("秒杀已结束");
        }
        // 必须有收货地址
        Long addrCount = addressMapper.selectCount(Wrappers.<MemberAddress>lambdaQuery()
                .eq(MemberAddress::getMemberId, memberId));
        if (addrCount == null || addrCount == 0) {
            throw new BusinessException("请先设置收货地址");
        }

        // 限购：同一用户同一商品只能抢一次
        String userKey = userKey(itemId, memberId);
        Boolean first;
        try {
            first = stringRedisTemplate.opsForValue().setIfAbsent(userKey, "1", Duration.ofHours(2));
        } catch (Exception e) {
            log.error("秒杀限购校验失败", e);
            throw new BusinessException("秒杀服务繁忙，请稍后重试：" + e.getMessage());
        }
        if (first == null || !first) {
            throw new BusinessException("您已参与过该秒杀");
        }

        // 库存预热 + 原子扣减
        Long remaining;
        try {
            stringRedisTemplate.opsForValue().setIfAbsent(stockKey(itemId), String.valueOf(item.getSeckillStock()), Duration.ofHours(24));
            remaining = stringRedisTemplate.opsForValue().decrement(stockKey(itemId));
        } catch (Exception e) {
            log.error("秒杀库存扣减失败", e);
            safeDelete(userKey);
            throw new BusinessException("秒杀服务繁忙，请稍后重试：" + e.getMessage());
        }
        if (remaining == null || remaining < 0) {
            safeIncrement(stockKey(itemId));
            safeDelete(userKey);
            throw new BusinessException("商品已抢光");
        }

        try {
            if (seckillOrderProducer != null) {
                seckillOrderProducer.send(memberId, itemId);
            } else {
                createSeckillOrder(memberId, itemId);
            }
        } catch (Exception e) {
            rollbackSeckill(memberId, itemId);
            throw new BusinessException("抢购失败，请重试");
        }
    }

    /**
     * 创建秒杀订单（异步消费者与同步兜底共用）
     */
    @Transactional(rollbackFor = Exception.class)
    public Orders createSeckillOrder(Long memberId, Long itemId) {
        SeckillItem item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("秒杀商品不存在");
        }
        Product product = productMapper.selectById(item.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
        // 扣商品库存
        int updated = productMapper.update(null, Wrappers.<Product>lambdaUpdate()
                .setSql("stock = stock - 1")
                .setSql("sales = sales + 1")
                .eq(Product::getId, product.getId())
                .ge(Product::getStock, 1));
        if (updated == 0) {
            throw new BusinessException("商品库存不足");
        }
        // 默认地址
        MemberAddress address = addressMapper.selectOne(Wrappers.<MemberAddress>lambdaQuery()
                .eq(MemberAddress::getMemberId, memberId)
                .orderByDesc(MemberAddress::getIsDefault)
                .last("limit 1"));
        if (address == null) {
            throw new BusinessException("请先设置收货地址");
        }
        // 建单
        Orders order = new Orders();
        order.setOrderNo(DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(4));
        order.setMemberId(memberId);
        order.setSeckillItemId(itemId);
        order.setTotalAmount(item.getSeckillPrice());
        order.setPayAmount(item.getSeckillPrice());
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(buildAddress(address));
        ordersMapper.insert(order);
        // 明细
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setPrice(item.getSeckillPrice());
        orderItem.setQuantity(1);
        orderItem.setTotalPrice(item.getSeckillPrice());
        orderItemMapper.insert(orderItem);
        // 发送 5 分钟超时消息
        if (orderTimeoutProducer != null) {
            orderTimeoutProducer.sendSeckillOrderTimeout(order.getId());
        }
        return order;
    }

    /**
     * 秒杀单关闭/超时后回补库存与限购
     */
    public void restoreSeckillOrder(Orders order) {
        if (order == null || order.getSeckillItemId() == null) {
            return;
        }
        rollbackSeckill(order.getMemberId(), order.getSeckillItemId());
    }

    private void rollbackSeckill(Long memberId, Long itemId) {
        try {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey(itemId)))) {
                stringRedisTemplate.opsForValue().increment(stockKey(itemId));
            }
        } catch (Exception ignore) {
        }
        safeDelete(userKey(itemId, memberId));
    }

    private void safeDelete(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception ignore) {
        }
    }

    private void safeIncrement(String key) {
        try {
            stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception ignore) {
        }
    }

    // ==================== 工具方法 ====================

    private SeckillItemVO toItemVO(SeckillItem item) {
        SeckillItemVO vo = new SeckillItemVO();
        BeanUtils.copyProperties(item, vo);
        Product product = productMapper.selectById(item.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setProductImage(product.getMainImage());
            vo.setOriginalPrice(product.getPrice());
        }
        SeckillActivity activity = activityMapper.selectById(item.getActivityId());
        if (activity != null) {
            vo.setActivityName(activity.getName());
            vo.setStartTime(activity.getStartTime());
            vo.setEndTime(activity.getEndTime());
        }
        vo.setRemainingStock(getRemainingStock(item.getId(), item.getSeckillStock()));
        return vo;
    }

    private Integer getRemainingStock(Long itemId, Integer fallback) {
        try {
            String val = stringRedisTemplate.opsForValue().get(stockKey(itemId));
            if (val != null) {
                return Math.max(Integer.parseInt(val), 0);
            }
        } catch (Exception ignore) {
            // Redis 异常时降级为数据库库存
        }
        return fallback;
    }

    private String stockKey(Long itemId) {
        return STOCK_KEY_PREFIX + itemId;
    }

    private String userKey(Long itemId, Long memberId) {
        return USER_KEY_PREFIX + itemId + ":" + memberId;
    }

    private String buildAddress(MemberAddress address) {
        return (address.getProvince() == null ? "" : address.getProvince())
                + (address.getCity() == null ? "" : address.getCity())
                + (address.getDistrict() == null ? "" : address.getDistrict())
                + address.getDetailAddress();
    }
}
