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
