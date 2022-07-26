package com.self.common.enums;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 操作状态
 */
public enum BusinessStatusEnum {

    /**
     * 成功
     */
    SUCCESS(NumberUtils.toByte("1"), "成功"),

    /**
     * 失败
     */
    FAIL(NumberUtils.toByte("0"), "失败");

    private final Byte value;

    private final String desc;

    BusinessStatusEnum(Byte value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Byte getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
