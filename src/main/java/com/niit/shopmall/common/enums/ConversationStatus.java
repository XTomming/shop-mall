package com.niit.shopmall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客服会话状态
 */
@Getter
@AllArgsConstructor
public enum ConversationStatus {

    WAITING(0, "待接入"),
    ACTIVE(1, "进行中"),
    ENDED(2, "已结束");

    private final int code;
    private final String desc;

    public static String descOf(int code) {
        for (ConversationStatus s : values()) {
            if (s.code == code) {
                return s.desc;
            }
        }
        return "未知";
    }
}
