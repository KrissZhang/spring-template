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
     * Minio api地址
     */
    public static final String MINIO_URL = "${minio.url: #{null}}";

    /**
     * Minio 端口
     */
    public static final String MINIO_PORT = "${minio.port: #{null}}";

    /**
     * Minio 账号
     */
    public static final String MINIO_ACCESS_KEY = "${minio.access_key: #{null}}";

    /**
     * Minio 密码
     */
    public static final String MINIO_SECRET_KEY = "${minio.secret_key: #{null}}";

    /**
     * Minio 文件夹名称
     */
    public static final String MINIO_BUCKET_NAME = "${minio.bucket_name: #{null}}";

    /**
     * Minio secure
     */
    public static final String MINIO_SECURE = "${minio.secure: #{null}}";

}
