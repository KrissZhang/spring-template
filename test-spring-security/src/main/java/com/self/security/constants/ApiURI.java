package com.self.security.constants;

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
     * 测试接口1
     */
    public static final String TEST_ONE = MODULE_URI_PREFIX + "/test/test1";

}
