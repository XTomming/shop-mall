package com.niit.mall.aftersale.client;

import com.niit.mall.aftersale.entity.ServiceUserDTO;
import com.niit.mall.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 用户服务 Feign 客户端（客服自动分配：取在线客服）
 */
@FeignClient(name = "mall-user-service")
public interface UserClient {

    @GetMapping("/api/service-user/online")
    Result<List<ServiceUserDTO>> onlineServiceUsers();
}
