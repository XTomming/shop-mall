package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.NoticeDTO;
import com.niit.shopmall.entity.Notice;
import com.niit.shopmall.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 后台公告管理接口
 */
@Api(tags = "后台-公告管理")
@RestController
@RequestMapping("/api/admin/notice")
public class AdminNoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("公告分页")
    @GetMapping("/page")
    public Result<Page<Notice>> page(@RequestParam(defaultValue = "1") long pageNum,
                                     @RequestParam(defaultValue = "10") long pageSize) {
        return Result.success(noticeService.adminPage(pageNum, pageSize));
    }

    @ApiOperation("新增公告")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody NoticeDTO dto) {
        noticeService.adminAdd(dto);
        return Result.success();
    }

    @ApiOperation("修改公告")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeDTO dto) {
        noticeService.adminUpdate(id, dto);
        return Result.success();
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.adminDelete(id);
        return Result.success();
    }
}
