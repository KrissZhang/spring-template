package com.self.common.enums;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 逻辑删除标识
 */
public enum DeletedEnum {

    /**
     * 未删除
     */
    ENABLE(NumberUtils.toByte("0"), "未删除"),

    /**
     * 已删除
     */
    DELETED(NumberUtils.toByte("1"), "已删除");

    private final Byte value;

    private final String desc;

    private static final Map<Byte, DeletedEnum> MAPPINGS = new HashMap<>(2);

    static {
        for (DeletedEnum deletedEnum : values()) {
            MAPPINGS.put(deletedEnum.getValue(), deletedEnum);
        }
    }

    @Nullable
    public static DeletedEnum resolve(@Nullable Byte value) {
        return (value != null ? MAPPINGS.get(value) : null);
    }

    DeletedEnum(Byte value, String desc) {
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
