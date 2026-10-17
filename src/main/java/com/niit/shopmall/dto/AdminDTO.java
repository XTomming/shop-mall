package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 管理员新增/修改参数
 */
@Data
public class AdminDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String role;

    private Integer status;
}
