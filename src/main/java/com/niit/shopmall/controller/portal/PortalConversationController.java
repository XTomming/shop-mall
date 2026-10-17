package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.ConversationCreateDTO;
import com.niit.shopmall.dto.ConversationMessageDTO;
import com.niit.shopmall.entity.Conversation;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前台客服会话接口
 */
@Api(tags = "前台-客服会话")
@RestController
@RequestMapping("/api/portal/conversation")
public class PortalConversationController {

    @Autowired
    private ConversationService conversationService;

    @ApiOperation("发起会话")
    @PostMapping
    public Result<Conversation> create(@RequestBody(required = false) ConversationCreateDTO dto) {
        Long productId = dto == null ? null : dto.getProductId();
        return Result.success(conversationService.create(SecurityUtils.getCurrentUserId(), productId));
    }

    @ApiOperation("我的会话列表")
    @GetMapping("/list")
    public Result<List<ConversationVO>> list() {
        return Result.success(conversationService.memberList(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("消息历史")
    @GetMapping("/{id}/message")
    public Result<List<ConversationMessage>> messages(@PathVariable Long id) {
        return Result.success(conversationService.messages(id, "MEMBER", SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("发送消息")
    @PostMapping("/{id}/message")
    public Result<ConversationMessage> send(@PathVariable Long id, @Valid @RequestBody ConversationMessageDTO dto) {
        return Result.success(conversationService.sendMessage(id, "MEMBER", SecurityUtils.getCurrentUserId(), dto));
    }
}
