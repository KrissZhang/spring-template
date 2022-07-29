package com.self.common.enums;

import org.springframework.http.HttpStatus;

public enum RespCodeEnum {

    /**
     * 成功
     */
    SUCCESS(String.valueOf(HttpStatus.OK.value()), "成功"),

    /**
     * 参数错误
     */
    FAIL_PARAM(String.valueOf(HttpStatus.BAD_REQUEST.value()), "参数错误"),

    /**
     * 未授权
     */
    FAIL_UNAUTHORIZED(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "未授权"),

    /**
     * 请求过多
     */
    FAIL_TOO_MANY_REQUESTS(String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()), "请求过多"),

    /**
     * 业务逻辑错误
     */
    FAIL_BIZ(String.valueOf(HttpStatus.CONFLICT.value()), "业务逻辑错误"),

    /**
     * 系统错误
     */
    FAIL_SYS(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "系统错误");


    private final String code;

    private final String msg;

    RespCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
