package com.self.common.constants;

/**
 * 系统配置
 */
public final class CfgConstants {

    private CfgConstants() {
    }

    /**
     * redis host
     */
    public static final String REDIS_HOST = "${spring.redis.host:10.12.2.64}";

    /**
     * redis port
     */
    public static final String REDIS_PORT = "${spring.redis.port:6379}";

    /**
     * redis password
     */
    public static final String REDIS_PASSWORD = "${spring.redis.password:sy_202106}";

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

    /**
     * SeaweedFS 地址
     */
    public static final String SEAWEEDFS_HOST = "${seaweedfs.host: 127.0.0.1}";

    /**
     * SeaweedFS 端口
     */
    public static final String SEAWEEDFS_PORT = "${seaweedfs.port: 8080}";

    /**
     * SeaweedFS use-public
     */
    public static final String SEAWEEDFS_USE_PUBLIC = "${seaweedfs.use-public: false}";

}
