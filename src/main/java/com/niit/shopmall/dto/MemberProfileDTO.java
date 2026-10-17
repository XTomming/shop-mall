package com.niit.shopmall.dto;

import lombok.Data;

/**
 * 会员资料修改参数
 */
@Data
public class MemberProfileDTO {

    private String nickname;

    private String phone;

    private String email;

    private Integer gender;

    private String avatar;
}
