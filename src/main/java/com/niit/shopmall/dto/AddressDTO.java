package com.niit.shopmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 收货地址参数
 */
@Data
public class AddressDTO {

    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String province;

    private String city;

    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    private Integer isDefault;
}
