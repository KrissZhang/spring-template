package com.self.common.enums;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程表单状态
 */
public enum ProcessFormStatusEnum {

    /**
     * 首次提交
     */
    FIRST_SUBMIT("首次提交", "FIRST_SUBMIT"),

    /**
     * 被驳回
     */
    REJECTED("被驳回", "REJECTED"),

    /**
     * 重新提交
     */
    RESUBMIT("重新提交", "RESUBMIT");

    private final String value;

    private final String desc;

    private static final Map<String, ProcessFormStatusEnum> MAPPINGS = new HashMap<>(16);

    static {
        for (ProcessFormStatusEnum processFormStatusEnum : values()) {
            MAPPINGS.put(processFormStatusEnum.getValue(), processFormStatusEnum);
        }
    }

    @Nullable
    public static ProcessFormStatusEnum resolve(@Nullable String value) {
        return (value != null ? MAPPINGS.get(value) : null);
    }

    ProcessFormStatusEnum(String desc, String value) {
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
