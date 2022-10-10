package com.self.common.constants;

/**
 * 缓存常量
 * @author zp
 */
public final class CacheConstants {

    private CacheConstants() {
        super();
    }

    /**
     * 字典 key
     */
    public static final String SYS_DICT_KEY = "sys:dict:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 雪花漂移 ID_GENERATOR Key
     */
    public static final String YITTER_ID_GENERATOR_KEY = "yitter:id:generator";

    /**
     * 雪花漂移 最大机器号 Key
     */
    public static final String YITTER_WORKERID_MAXID_KEY = "yitter:workerid:maxid";

    /**
     * 雪花漂移 已分配的机器号 Key
     */
    public static final String YITTER_ID_IP_KEY = "yitter:id:ip";

}
