package com.self.common.enums;

/**
 * 逻辑删除标识
 */
public enum DeletedEnum {

    /**
     * 未删除
     */
    ENABLE("未删除"),

    /**
     * 已删除
     */
    DELETED("已删除");

    private final String desc;

    DeletedEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
