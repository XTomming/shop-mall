package com.niit.shopmall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 售后单状态
 */
@Getter
@AllArgsConstructor
public enum AfterSaleStatus {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    COMPLETED(2, "已完成"),
    REJECTED(3, "已拒绝"),
    CLOSED(4, "已关闭");

    private final int code;
    private final String desc;

    public static String descOf(int code) {
        for (AfterSaleStatus s : values()) {
            if (s.code == code) {
                return s.desc;
            }
        }
        return "未知";
    }
}
