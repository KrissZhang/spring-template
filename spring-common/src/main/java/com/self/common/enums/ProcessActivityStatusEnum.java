package com.self.common.enums;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程节点活动状态
 */
public enum ProcessActivityStatusEnum {

    /**
     * 当前停留
     */
    CURRENT("当前停留", "CURRENT"),

    /**
     * 已完成
     */
    FINISHED("已完成", "FINISHED"),

    /**
     * 通过
     */
    APPROVED("通过", "APPROVED"),

    /**
     * 驳回
     */
    REJECTED("驳回", "REJECTED"),

    /**
     * 未到达
     */
    PENDING("未到达", "PENDING");

    private final String value;

    private final String desc;

    private static final Map<String, ProcessActivityStatusEnum> MAPPINGS = new HashMap<>(16);

    static {
        for (ProcessActivityStatusEnum processActivityStatusEnum : values()) {
            MAPPINGS.put(processActivityStatusEnum.getValue(), processActivityStatusEnum);
        }
    }

    @Nullable
    public static ProcessActivityStatusEnum resolve(@Nullable String value) {
        return (value != null ? MAPPINGS.get(value) : null);
    }

    ProcessActivityStatusEnum(String desc, String value) {
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
