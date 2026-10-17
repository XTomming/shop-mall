package com.niit.mall.aftersale.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.mall.aftersale.client.OrderClient;
import com.niit.mall.aftersale.client.ProductClient;
import com.niit.mall.aftersale.client.UserClient;
import com.niit.mall.aftersale.dto.ConversationMessageDTO;
import com.niit.mall.aftersale.entity.Conversation;
import com.niit.mall.aftersale.entity.ConversationMessage;
import com.niit.mall.aftersale.entity.Orders;
import com.niit.mall.aftersale.entity.Product;
import com.niit.mall.aftersale.entity.ServiceUserDTO;
import com.niit.mall.aftersale.mapper.ConversationMapper;
import com.niit.mall.aftersale.mapper.ConversationMessageMapper;
import com.niit.mall.aftersale.vo.ConversationVO;
import com.niit.mall.common.BusinessException;
import com.niit.mall.common.Result;
import com.niit.mall.common.enums.ConversationStatus;
import com.niit.mall.common.enums.OrderStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客服会话服务（在线聊天；自动分配在线客服通过 Feign 取 user-service 在线客服）
 */
@Service
public class ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMessageMapper messageMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Conversation create(Long memberId, Long productId) {
        Conversation conv = new Conversation();
        conv.setMemberId(memberId);
        conv.setProductId(productId);
        conv.setStatus(ConversationStatus.WAITING.getCode());
        ServiceUserDTO service = pickOnlineService();
        if (service != null) {
            conv.setServiceId(service.getId());
            conv.setStatus(ConversationStatus.ACTIVE.getCode());
        }
        conversationMapper.insert(conv);
        return conv;
    }

    public ConversationMessage sendMessage(Long conversationId, String senderType, Long senderId, ConversationMessageDTO dto) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv == null) {
            throw new BusinessException("会话不存在");
        }
        if (conv.getStatus() != null && conv.getStatus() == ConversationStatus.ENDED.getCode()) {
            throw new BusinessException("会话已结束");
        }
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
            Result<Product> pr = productClient.getById(dto.getProductId());
            Product product = pr == null ? null : pr.getData();
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
            Result<Orders> or = orderClient.getOrder(dto.getOrderId());
            Orders order = or == null ? null : or.getData();
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            msg.setOrderId(order.getId());
            msg.setContent(order.getOrderNo());
            msg.setOrderStatus(order.getStatus());
            msg.setOrderStatusDesc(OrderStatus.descOf(order.getStatus()));
        } else {
            String content = dto.getContent();
            if (content == null || content.trim().isEmpty()) {
                throw new BusinessException("消息内容不能为空");
            }
            msg.setType("TEXT");
            msg.setContent(content.trim());
        }
        messageMapper.insert(msg);

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

        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, msg);
        return msg;
    }

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

    public List<ConversationVO> memberList(Long memberId) {
        List<Conversation> list = conversationMapper.selectList(Wrappers.<Conversation>lambdaQuery()
                .eq(Conversation::getMemberId, memberId)
                .orderByDesc(Conversation::getUpdateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

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
     * 在在线客服中选当前进行中会话最少的那个（在线客服列表通过 Feign 从 user-service 获取）
     */
    private ServiceUserDTO pickOnlineService() {
        Result<List<ServiceUserDTO>> r = userClient.onlineServiceUsers();
        List<ServiceUserDTO> services = r == null ? null : r.getData();
        if (services == null || services.isEmpty()) {
            return null;
        }
        ServiceUserDTO picked = null;
        long min = Long.MAX_VALUE;
        for (ServiceUserDTO s : services) {
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
        return vo;
    }
}
