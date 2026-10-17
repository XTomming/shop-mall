package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.service.CommentService;
import com.niit.shopmall.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台评论管理接口
 */
@Api(tags = "后台-评论管理")
@RestController
@RequestMapping("/api/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("评论分页")
    @GetMapping("/page")
    public Result<Page<CommentVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                        @RequestParam(defaultValue = "10") long pageSize,
                                        @RequestParam(required = false) Long productId) {
        return Result.success(commentService.adminPage(pageNum, pageSize, productId));
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.adminDelete(id);
        return Result.success();
    }
}
