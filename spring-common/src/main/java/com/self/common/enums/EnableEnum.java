package com.self.common.enums;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    EnableEnum(Byte value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Byte getValue() {
        return value;
    }

    public static ImmutableMap<Byte, EnableEnum> enableMap(){
        return ImmutableMap.copyOf(EnumSet.allOf(EnableEnum.class).stream().collect(Collectors.toMap(EnableEnum::getValue, Function.identity())));
    }

}
