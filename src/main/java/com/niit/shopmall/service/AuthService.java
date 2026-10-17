package com.niit.shopmall.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niit.shopmall.common.BusinessException;
import com.niit.shopmall.dto.LoginDTO;
import com.niit.shopmall.dto.RegisterDTO;
import com.niit.shopmall.entity.Member;
import com.niit.shopmall.entity.SysUser;
import com.niit.shopmall.mapper.MemberMapper;
import com.niit.shopmall.mapper.SysUserMapper;
import com.niit.shopmall.security.JwtUtil;
import com.niit.shopmall.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务（会员注册登录 + 管理员登录）
 */
@Service
public class AuthService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 会员注册
     */
    public void register(RegisterDTO dto) {
        Long count = memberMapper.selectCount(Wrappers.<Member>lambdaQuery()
                .eq(Member::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        Member member = new Member();
        member.setUsername(dto.getUsername());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setNickname(dto.getNickname() == null || dto.getNickname().isEmpty() ? dto.getUsername() : dto.getNickname());
        member.setPhone(dto.getPhone());
        member.setEmail(dto.getEmail());
        member.setGender(0);
        member.setStatus(1);
        memberMapper.insert(member);
    }

    /**
     * 会员登录
     */
    public LoginVO memberLogin(LoginDTO dto) {
        Member member = memberMapper.selectOne(Wrappers.<Member>lambdaQuery()
                .eq(Member::getUsername, dto.getUsername()));
        if (member == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (member.getStatus() == null || member.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(member.getId(), member.getUsername(), "MEMBER");
        return buildLoginVO(token, member.getId(), member.getUsername(), member.getNickname(), member.getAvatar(), "MEMBER");
    }

    /**
     * 管理员登录
     */
    public LoginVO adminLogin(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String role = user.getRole() == null ? "ADMIN" : user.getRole();
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);
        return buildLoginVO(token, user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), role);
    }

    /**
     * 客服登录（仅限 role=SERVICE 的账号）
     */
    public LoginVO serviceLogin(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!"SERVICE".equals(user.getRole())) {
            throw new BusinessException("该账号不是客服账号");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "SERVICE");
        LoginVO vo = buildLoginVO(token, user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), "SERVICE");
        vo.setOnline(user.getOnline());
        return vo;
    }

    private LoginVO buildLoginVO(String token, Long id, String username, String nickname, String avatar, String role) {
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(id);
        vo.setUsername(username);
        vo.setNickname(nickname);
        vo.setAvatar(avatar);
        vo.setRole(role);
        return vo;
    }
}
