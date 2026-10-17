package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.MemberProfileDTO;
import com.niit.shopmall.dto.PasswordDTO;
import com.niit.shopmall.entity.Member;
import com.niit.shopmall.mapper.MemberMapper;
import com.niit.shopmall.vo.MemberVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 会员服务（个人中心 + 后台会员管理）
 */
@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取个人资料
     */
    public MemberVO getProfile(Long memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        return toVO(member);
    }

    /**
     * 修改个人资料
     */
    public void updateProfile(Long memberId, MemberProfileDTO dto) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        member.setNickname(dto.getNickname());
        member.setPhone(dto.getPhone());
        member.setEmail(dto.getEmail());
        member.setGender(dto.getGender());
        member.setAvatar(dto.getAvatar());
        memberMapper.updateById(member);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Long memberId, PasswordDTO dto) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), member.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        Member update = new Member();
        update.setId(memberId);
        update.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberMapper.updateById(update);
    }

    /**
     * 后台：会员分页
     */
    public Page<MemberVO> adminPage(long pageNum, long pageSize, String keyword) {
        Page<Member> page = memberMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<Member>lambdaQuery()
                        .and(keyword != null && !keyword.isEmpty(), w -> w
                                .like(Member::getUsername, keyword)
                                .or().like(Member::getNickname, keyword)
                                .or().like(Member::getPhone, keyword))
                        .orderByDesc(Member::getCreateTime));
        Page<MemberVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toVO).collect(java.util.stream.Collectors.toList()));
        return result;
    }

    /**
     * 后台：会员详情
     */
    public MemberVO adminDetail(Long id) {
        return toVO(memberMapper.selectById(id));
    }

    /**
     * 后台：禁用/启用会员
     */
    public void adminUpdateStatus(Long id, Integer status) {
        Member member = memberMapper.selectById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        Member update = new Member();
        update.setId(id);
        update.setStatus(status);
        memberMapper.updateById(update);
    }

    private MemberVO toVO(Member member) {
        if (member == null) {
            return null;
        }
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(member, vo);
        return vo;
    }
}
