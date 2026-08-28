package com.self.common.enums;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程实例KEY
 */
public enum ProcessInstanceKeyEnum {

    /**
     * 请假流程
     */
    LEAVE("请假流程", "leaveProcess");

    private final String value;

    private final String desc;

    private static final Map<String, ProcessInstanceKeyEnum> MAPPINGS = new HashMap<>(16);

    static {
        for (ProcessInstanceKeyEnum processInstanceKeyEnum : values()) {
            MAPPINGS.put(processInstanceKeyEnum.getValue(), processInstanceKeyEnum);
        }
    }

    @Nullable
    public static ProcessInstanceKeyEnum resolve(@Nullable String value) {
        return (value != null ? MAPPINGS.get(value) : null);
    }

    ProcessInstanceKeyEnum(String desc, String value) {
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
