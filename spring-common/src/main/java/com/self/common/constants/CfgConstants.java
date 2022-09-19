package com.self.common.constants;

/**
 * 系统配置
 */
public final class CfgConstants {

    private CfgConstants() {
    }

    /**
     * druid 登陆用户名
     */
    public static final String DRUID_LOGIN_USERNAME = "${spring.druid.login-username: admin}";

    /**
     * druid 登陆密码
     */
    public static final String DRUID_LOGIN_PASSWORD = "${spring.druid.login-password: 123}";

    /**
     * druid 允许访问地址
     */
    public static final String DRUID_LOGIN_ALLOW = "${spring.druid.allow: 127.0.0.1}";

}
