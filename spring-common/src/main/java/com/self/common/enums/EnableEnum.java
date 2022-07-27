package com.self.common.enums;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 是否启用
 */
public enum EnableEnum {

    /**
     * 禁用
     */
    DISABLE(NumberUtils.toByte("0"), "禁用"),

    /**
     * 启用
     */
    ENABLE(NumberUtils.toByte("1"), "启用");

    private final Byte value;

    private final String desc;

    private static final Map<Byte, EnableEnum> MAPPINGS = new HashMap<>(2);

    static {
        for (EnableEnum enableEnum : values()) {
            MAPPINGS.put(enableEnum.getValue(), enableEnum);
        }
    }

    @Nullable
    public static EnableEnum resolve(@Nullable Byte value) {
        return (value != null ? MAPPINGS.get(value) : null);
    }

    EnableEnum(Byte value, String desc) {
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
