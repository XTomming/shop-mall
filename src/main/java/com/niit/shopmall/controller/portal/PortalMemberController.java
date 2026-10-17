package com.niit.shopmall.controller.portal;

import com.niit.shopmall.common.Result;
import com.niit.shopmall.dto.MemberProfileDTO;
import com.niit.shopmall.dto.PasswordDTO;
import com.niit.shopmall.security.SecurityUtils;
import com.niit.shopmall.service.MemberService;
import com.niit.shopmall.vo.MemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 前台个人中心接口
 */
@Api(tags = "前台-个人中心")
@RestController
@RequestMapping("/api/portal/member")
public class PortalMemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation("个人资料")
    @GetMapping("/profile")
    public Result<MemberVO> profile() {
        return Result.success(memberService.getProfile(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("修改资料")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody MemberProfileDTO dto) {
        memberService.updateProfile(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO dto) {
        memberService.updatePassword(SecurityUtils.getCurrentUserId(), dto);
        return Result.success();
    }
}
