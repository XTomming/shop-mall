package com.niit.mall.user.dto;

import lombok.Data;

/**
 * 在线客服信息（供 after-sale-service 自动分配使用）
 */
@Data
public class ServiceUserDTO {

    private Long id;

    private String nickname;
}
