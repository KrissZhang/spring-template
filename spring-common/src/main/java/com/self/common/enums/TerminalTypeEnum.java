package com.self.common.enums;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 终端类型
 */
public enum TerminalTypeEnum {

    /**
     * 手机
     */
    MOBILE("mobile", "mobile"),

    /**
     * 平板电脑
     */
    TABLET("tablet", "tablet"),

    /**
     * WEB
     */
    WEB("web", "web");

    private final String value;

    private final String desc;

    private static final Map<String, TerminalTypeEnum> MAPPINGS = new HashMap<>(16);

    static {
        for (TerminalTypeEnum terminalTypeEnum : values()) {
            MAPPINGS.put(terminalTypeEnum.getValue(), terminalTypeEnum);
        }
    }

    @Nullable
    public static TerminalTypeEnum resolve(@Nullable String value) {
        return (value != null ? MAPPINGS.get(value) : null);
    }

    TerminalTypeEnum(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public String getValue() {
        return value;
    }

}
