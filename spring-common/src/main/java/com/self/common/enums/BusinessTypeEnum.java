package com.self.common.enums;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 功能类型
 */
public enum BusinessTypeEnum {

    /**
     * 新增
     */
    ADD(NumberUtils.toByte("1"), "新增"),

    /**
     * 修改
     */
    EDIT(NumberUtils.toByte("2"), "修改"),

    /**
     * 删除
     */
    DELETE(NumberUtils.toByte("3"), "删除"),

    /**
     * 导入
     */
    IMPORT(NumberUtils.toByte("4"), "导入"),

    /**
     * 导出
     */
    EXPORT(NumberUtils.toByte("5"), "导出"),

    /**
     * 其它
     */
    OTHER(NumberUtils.toByte("0"), "其它");

    private final Byte value;

    private final String desc;

    BusinessTypeEnum(Byte value, String desc) {
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
