package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.CommentDTO;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.CommentService;
import com.niit.shopmall.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前台评价接口
 */
@Api(tags = "前台-评价")
@RestController
@RequestMapping("/api/portal/comment")
public class PortalCommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("发表评价")
    @PostMapping
    public Result<Void> publish(@Valid @RequestBody CommentDTO dto) {
        commentService.publish(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @ApiOperation("商品评价列表")
    @GetMapping("/list")
    public Result<List<CommentVO>> list(@RequestParam Long productId) {
        return Result.success(commentService.listByProduct(productId));
    }
}
