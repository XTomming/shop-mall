package com.niit.shopmall.controller.service;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.ConversationMessageDTO;
import com.niit.shopmall.entity.ConversationMessage;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.ConversationService;
import com.niit.shopmall.vo.ConversationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 * 客服端-会话接口
 */
@Api(tags = "客服端-会话")
@RestController
@RequestMapping("/api/service/conversation")
public class ServiceConversationController {

    @Autowired
    private ConversationService conversationService;

    @ApiOperation("会话列表（进行中 + 待接入池）")
    @GetMapping("/list")
    public Result<List<ConversationVO>> list() {
        return Result.success(conversationService.serviceList(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("接入待接入会话")
    @PutMapping("/{id}/claim")
    public Result<Void> claim(@PathVariable Long id) {
        conversationService.serviceClaim(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }

    @ApiOperation("消息历史")
    @GetMapping("/{id}/message")
    public Result<List<ConversationMessage>> messages(@PathVariable Long id) {
        return Result.success(conversationService.messages(id, "SERVICE", SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("发送消息")
    @PostMapping("/{id}/message")
    public Result<ConversationMessage> send(@PathVariable Long id, @Valid @RequestBody ConversationMessageDTO dto) {
        return Result.success(conversationService.sendMessage(id, "SERVICE", SecurityUtils.getCurrentUserId(), dto));
    }

    @ApiOperation("结束会话")
    @PutMapping("/{id}/end")
    public Result<Void> end(@PathVariable Long id) {
        conversationService.end(SecurityUtils.getCurrentUserId(), id);
        return Result.success();
    }
}
