package com.niit.shopmall.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.common.enums.ConversationStatus;
import com.niit.shopmall.common.enums.OrderStatus;
import com.niit.shopmall.dto.ConversationMessageDTO;
import com.niit.shopmall.entity.Conversation;
import com.niit.shopmall.entity.ConversationMessage;
import com.niit.shopmall.entity.Member;
import com.niit.shopmall.entity.OrderItem;
import com.niit.shopmall.entity.Orders;
import com.niit.shopmall.entity.Product;
import com.niit.shopmall.entity.SysUser;
import com.niit.shopmall.mapper.ConversationMapper;
import com.niit.shopmall.mapper.ConversationMessageMapper;
import com.niit.shopmall.mapper.MemberMapper;
import com.niit.shopmall.mapper.OrderItemMapper;
import com.niit.shopmall.mapper.OrdersMapper;
import com.niit.shopmall.mapper.ProductMapper;
import com.niit.shopmall.mapper.SysUserMapper;
import com.niit.shopmall.vo.ConversationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客服会话服务（在线聊天）
 */
@Service
public class ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMessageMapper messageMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 会员发起会话：自动分配给在线且最闲的客服
     */
    public Conversation create(Long memberId, Long productId) {
        Conversation conv = new Conversation();
        conv.setMemberId(memberId);
        conv.setProductId(productId);
        conv.setStatus(ConversationStatus.WAITING.getCode());
        SysUser service = pickOnlineService();
        if (service != null) {
            conv.setServiceId(service.getId());
            conv.setStatus(ConversationStatus.ACTIVE.getCode());
        }
        conversationMapper.insert(conv);
        return conv;
    }

    /**
     * 发送消息并实时推送（支持文本 / 图片 / 商品链接）
     */
    public ConversationMessage sendMessage(Long conversationId, String senderType, Long senderId, ConversationMessageDTO dto) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) {
            throw new BusinessException("会话不存在");
        }
        if (conv.getStatus() != null && conv.getStatus() == ConversationStatus.ENDED.getCode()) {
            throw new BusinessException("会话已结束");
        }
        // 校验发送方是会话参与者
        if ("MEMBER".equals(senderType)) {
            if (!conv.getMemberId().equals(senderId)) {
                throw new BusinessException("无权访问该会话");
            }
        } else if ("SERVICE".equals(senderType)) {
            if (conv.getServiceId() == null || !conv.getServiceId().equals(senderId)) {
                throw new BusinessException("无权访问该会话");
            }
        } else {
            throw new BusinessException("非法发送方");
        }

        String type = dto.getType() == null || dto.getType().isEmpty() ? "TEXT" : dto.getType();
        ConversationMessage msg = new ConversationMessage();
        msg.setConversationId(conversationId);
        msg.setSenderType(senderType);
        msg.setSenderId(senderId);
        msg.setType(type);

        if ("IMAGE".equals(type)) {
            String url = dto.getContent();
            if (url == null || url.trim().isEmpty()) {
                throw new BusinessException("图片不能为空");
            }
            msg.setContent(url.trim());
        } else if ("PRODUCT".equals(type)) {
            if (dto.getProductId() == null) {
                throw new BusinessException("商品不能为空");
            }
            Product product = productMapper.selectById(dto.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            msg.setProductId(product.getId());
            msg.setContent(product.getName());
            msg.setProductImage(product.getMainImage());
        } else if ("ORDER".equals(type)) {
            if (dto.getOrderId() == null) {
                throw new BusinessException("订单不能为空");
            }
            Orders order = ordersMapper.selectById(dto.getOrderId());
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            msg.setOrderId(order.getId());
            msg.setContent(order.getOrderNo());
            msg.setOrderStatus(order.getStatus());
            msg.setOrderStatusDesc(OrderStatus.descOf(order.getStatus()));
            msg.setOrderSnapshot(buildOrderSnapshot(order));
        } else {
            String content = dto.getContent();
            if (content == null || content.trim().isEmpty()) {
                throw new BusinessException("消息内容不能为空");
            }
            msg.setType("TEXT");
            msg.setContent(content.trim());
        }
        messageMapper.insert(msg);

        // 更新会话摘要
        String summary;
        if ("IMAGE".equals(msg.getType())) {
            summary = "[图片]";
        } else if ("PRODUCT".equals(msg.getType())) {
            summary = "[商品]" + msg.getContent();
        } else if ("ORDER".equals(msg.getType())) {
            summary = "[订单]" + msg.getContent();
        } else {
            summary = msg.getContent();
        }
        Conversation update = new Conversation();
        update.setId(conversationId);
        update.setLastMessage(summary);
        update.setLastMessageTime(LocalDateTime.now());
        conversationMapper.updateById(update);
        // 实时推送
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, msg);
        return msg;
    }

    /**
     * 构建订单快照JSON（含明细：缩略图/名称/价格/数量）
     */
    private String buildOrderSnapshot(Orders order) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("orderNo", order.getOrderNo());
        snapshot.put("status", order.getStatus());
        snapshot.put("statusDesc", OrderStatus.descOf(order.getStatus()));
        snapshot.put("totalAmount", order.getTotalAmount());
        snapshot.put("payAmount", order.getPayAmount());
        List<OrderItem> items = orderItemMapper.selectList(Wrappers.<OrderItem>lambdaQuery()
                .eq(OrderItem::getOrderId, order.getId()));
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (OrderItem it : items) {
            Map<String, Object> im = new LinkedHashMap<>();
            im.put("productId", it.getProductId());
            im.put("productName", it.getProductName());
            im.put("productImage", it.getProductImage());
            im.put("price", it.getPrice());
            im.put("quantity", it.getQuantity());
            im.put("totalPrice", it.getTotalPrice());
            itemList.add(im);
        }
        snapshot.put("items", itemList);
        return JSONUtil.toJsonStr(snapshot);
    }

    /**
     * 消息历史（校验参与者）
     */
    public List<ConversationMessage> messages(Long conversationId, String senderType, Long senderId) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) {
            throw new BusinessException("会话不存在");
        }
        if ("MEMBER".equals(senderType) && !conv.getMemberId().equals(senderId)) {
            throw new BusinessException("无权访问该会话");
        }
        if ("SERVICE".equals(senderType) && (conv.getServiceId() == null || !conv.getServiceId().equals(senderId))) {
            throw new BusinessException("无权访问该会话");
        }
        return messageMapper.selectList(Wrappers.<ConversationMessage>lambdaQuery()
                .eq(ConversationMessage::getConversationId, conversationId)
                .orderByAsc(ConversationMessage::getCreateTime));
    }

    /**
     * 会员：我的会话列表
     */
    public List<ConversationVO> memberList(Long memberId) {
        List<Conversation> list = conversationMapper.selectList(Wrappers.<Conversation>lambdaQuery()
                .eq(Conversation::getMemberId, memberId)
                .orderByDesc(Conversation::getUpdateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 客服：工作台会话列表（进行中 + 待接入池）
     */
    public List<ConversationVO> serviceList(Long serviceId) {
        List<Conversation> mine = conversationMapper.selectList(Wrappers.<Conversation>lambdaQuery()
                .eq(Conversation::getServiceId, serviceId)
                .eq(Conversation::getStatus, ConversationStatus.ACTIVE.getCode())
                .orderByDesc(Conversation::getUpdateTime));
        List<Conversation> waiting = conversationMapper.selectList(Wrappers.<Conversation>lambdaQuery()
                .isNull(Conversation::getServiceId)
                .eq(Conversation::getStatus, ConversationStatus.WAITING.getCode())
                .orderByDesc(Conversation::getUpdateTime));
        List<Conversation> all = new ArrayList<>(mine);
        all.addAll(waiting);
        return all.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 客服：接入待接入会话
     */
    public void serviceClaim(Long serviceId, Long conversationId) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) {
            throw new BusinessException("会话不存在");
        }
        if (conv.getStatus() != ConversationStatus.WAITING.getCode() || conv.getServiceId() != null) {
            throw new BusinessException("该会话不可接入");
        }
        Conversation update = new Conversation();
        update.setId(conversationId);
        update.setServiceId(serviceId);
        update.setStatus(ConversationStatus.ACTIVE.getCode());
        conversationMapper.updateById(update);
    }

    /**
     * 客服：结束会话
     */
    public void end(Long serviceId, Long conversationId) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null || conv.getServiceId() == null || !conv.getServiceId().equals(serviceId)) {
            throw new BusinessException("会话不存在");
        }
        Conversation update = new Conversation();
        update.setId(conversationId);
        update.setStatus(ConversationStatus.ENDED.getCode());
        conversationMapper.updateById(update);
    }

    /**
     * 客服：上线/下线
     */
    public void online(Long serviceId, boolean online) {
        SysUser update = new SysUser();
        update.setId(serviceId);
        update.setOnline(online ? 1 : 0);
        sysUserMapper.updateById(update);
    }

    /**
     * 在在线且启用的客服中，选当前进行中会话最少的那个
     */
    private SysUser pickOnlineService() {
        List<SysUser> services = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getRole, "SERVICE")
                .eq(SysUser::getStatus, 1)
                .eq(SysUser::getOnline, 1));
        if (services.isEmpty()) {
            return null;
        }
        SysUser picked = null;
        long min = Long.MAX_VALUE;
        for (SysUser s : services) {
            Long cnt = conversationMapper.selectCount(Wrappers.<Conversation>lambdaQuery()
                    .eq(Conversation::getServiceId, s.getId())
                    .eq(Conversation::getStatus, ConversationStatus.ACTIVE.getCode()));
            long c = cnt == null ? 0 : cnt;
            if (c < min) {
                min = c;
                picked = s;
            }
        }
        return picked;
    }

    private ConversationVO toVO(Conversation conv) {
        ConversationVO vo = new ConversationVO();
        BeanUtils.copyProperties(conv, vo);
        vo.setStatusDesc(ConversationStatus.descOf(conv.getStatus()));
        Member member = memberMapper.selectById(conv.getMemberId());
        if (member != null) {
            vo.setMemberNickname(member.getNickname());
        }
        if (conv.getServiceId() != null) {
            SysUser service = sysUserMapper.selectById(conv.getServiceId());
            if (service != null) {
                vo.setServiceNickname(service.getNickname());
            }
        }
        return vo;
    }
}
