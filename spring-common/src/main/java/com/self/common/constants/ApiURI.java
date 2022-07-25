package com.self.common.constants;

/**
 * 模块API基类
 */
public class ApiURI {

    private ApiURI() {
    }

    /**
     * 模块基础地址
     */
    public static final String MODULE_URI_PREFIX = "/api";

    /**
     * token认证
     */
    public static final String TOKEN_LOGIN = MODULE_URI_PREFIX + "/token/login";

    /**
     * 短信认证
     */
    public static final String TOKEN_SMS_LOGIN = MODULE_URI_PREFIX + "/token/smsLogin";

    /**
     * 测试请求
     */
    public static final String TEST_REQ = MODULE_URI_PREFIX + "/test/testReq";

    /**
     * 测试分页
     */
    public static final String TEST_PAGE = MODULE_URI_PREFIX + "/test/testPage";

}
