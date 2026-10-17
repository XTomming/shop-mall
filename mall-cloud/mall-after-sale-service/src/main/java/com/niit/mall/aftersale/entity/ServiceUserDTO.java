package com.niit.mall.aftersale.entity;

import lombok.Data;

/**
 * 在线客服信息（Feign 从 user-service 获取）
 */
@Data
public class ServiceUserDTO {

    private Long id;

    private String nickname;
}
