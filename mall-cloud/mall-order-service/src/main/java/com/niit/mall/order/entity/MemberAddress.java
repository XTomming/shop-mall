package com.niit.mall.order.entity;

import lombok.Data;

/**
 * 收货地址（Feign 从 user-service 获取）
 */
@Data
public class MemberAddress {

    private Long id;

    private Long memberId;

    private String receiverName;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;
}
