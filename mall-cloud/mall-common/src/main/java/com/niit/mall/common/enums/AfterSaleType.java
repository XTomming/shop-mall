package com.niit.mall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 售后单类型
 */
@Getter
@AllArgsConstructor
public enum AfterSaleType {

    REFUND(1, "退款"),
    INVOICE(2, "发票"),
    DISPUTE(3, "纠纷");

    private final int code;
    private final String desc;

    public static String descOf(int code) {
        for (AfterSaleType t : values()) {
            if (t.code == code) {
                return t.desc;
            }
        }
        return "未知";
    }
}
