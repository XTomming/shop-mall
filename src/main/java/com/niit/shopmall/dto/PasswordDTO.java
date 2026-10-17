package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码参数
 */
@Data
public class PasswordDTO {

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
