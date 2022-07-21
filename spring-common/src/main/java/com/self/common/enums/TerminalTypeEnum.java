package com.self.common.enums;

/**
 * 终端类型
 */
public enum TerminalTypeEnum {
    /**
     * web
     */
    WEB("web", "web"),

    /**
     * app
     */
    APP("app", "app");

    private String desc;

    private String value;

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
