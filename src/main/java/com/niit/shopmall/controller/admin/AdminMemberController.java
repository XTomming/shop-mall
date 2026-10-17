package com.niit.shopmall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.Result;
import com.niit.shopmall.service.MemberService;
import com.niit.shopmall.vo.MemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台会员管理接口
 */
@Api(tags = "后台-会员管理")
@RestController
@RequestMapping("/api/admin/member")
public class AdminMemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation("会员分页")
    @GetMapping("/page")
    public Result<Page<MemberVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                       @RequestParam(defaultValue = "10") long pageSize,
                                       @RequestParam(required = false) String keyword) {
        return Result.success(memberService.adminPage(pageNum, pageSize, keyword));
    }

    @ApiOperation("会员详情")
    @GetMapping("/{id}")
    public Result<MemberVO> detail(@PathVariable Long id) {
        return Result.success(memberService.adminDetail(id));
    }

    @ApiOperation("禁用/启用会员")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        memberService.adminUpdateStatus(id, status);
        return Result.success();
    }
}
