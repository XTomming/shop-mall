package com.niit.shopmall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING_PAYMENT(0, "待付款"),
    PENDING_DELIVERY(1, "待发货"),
    PENDING_RECEIPT(2, "待收货"),
    COMPLETED(3, "已完成"),
    CLOSED(4, "已关闭"),
    REFUNDED(5, "已退款");

    private final int code;
    private final String desc;

    public static String descOf(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) {
                return s.desc;
            }
        }
        return "未知";
    }
}
