package com.niit.mall.aftersale.controller;

import com.niit.mall.aftersale.dto.ConversationCreateDTO;
import com.niit.mall.aftersale.dto.ConversationMessageDTO;
import com.niit.mall.aftersale.entity.Conversation;
import com.niit.mall.aftersale.entity.ConversationMessage;
import com.niit.mall.aftersale.service.ConversationService;
import com.niit.mall.aftersale.vo.ConversationVO;
import com.niit.mall.common.Result;
import com.niit.mall.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前台客服会话接口
 */
@RestController
@RequestMapping("/api/portal/conversation")
public class PortalConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public Result<Conversation> create(@RequestBody(required = false) ConversationCreateDTO dto) {
        Long productId = dto == null ? null : dto.getProductId();
        return Result.success(conversationService.create(SecurityUtils.getCurrentUserId(), productId));
    }

    @GetMapping("/list")
    public Result<List<ConversationVO>> list() {
        return Result.success(conversationService.memberList(SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}/message")
    public Result<List<ConversationMessage>> messages(@PathVariable Long id) {
        return Result.success(conversationService.messages(id, "MEMBER", SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/{id}/message")
    public Result<ConversationMessage> send(@PathVariable Long id, @Valid @RequestBody ConversationMessageDTO dto) {
        return Result.success(conversationService.sendMessage(id, "MEMBER", SecurityUtils.getCurrentUserId(), dto));
    }
}
