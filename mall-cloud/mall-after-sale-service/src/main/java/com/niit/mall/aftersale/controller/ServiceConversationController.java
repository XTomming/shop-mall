package com.niit.mall.aftersale.controller;

import com.niit.mall.aftersale.dto.ConversationMessageDTO;
import com.niit.mall.aftersale.entity.ConversationMessage;
import com.niit.mall.aftersale.service.ConversationService;
import com.niit.mall.aftersale.vo.ConversationVO;
import com.niit.mall.common.Result;
import com.niit.mall.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 客服端会话接口
 */
@RestController
@RequestMapping("/api/service/conversation")
public class ServiceConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/list")
    public Result<List<ConversationVO>> list() {
        return Result.success(conversationService.serviceList(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}/claim")
    public Result<Void> claim(@PathVariable Long id) {
        conversationService.serviceClaim(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }

    @GetMapping("/{id}/message")
    public Result<List<ConversationMessage>> messages(@PathVariable Long id) {
        return Result.success(conversationService.messages(id, "SERVICE", SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/{id}/message")
    public Result<ConversationMessage> send(@PathVariable Long id, @Valid @RequestBody ConversationMessageDTO dto) {
        return Result.success(conversationService.sendMessage(id, "SERVICE", SecurityUtils.getCurrentUserId(), dto));
    }

    @PutMapping("/{id}/end")
    public Result<Void> end(@PathVariable Long id) {
        conversationService.end(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
