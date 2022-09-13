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

    /**
     * 测试事务
     */
    public static final String TEST_TRANSACTION = MODULE_URI_PREFIX + "/test/testTransaction";

    /**
     * 测试添加Cron定时任务
     */
    public static final String TEST_CRON_JOB_ADD = MODULE_URI_PREFIX + "/test/testCronJobAdd";

    /**
     * 测试添加时间间隔定时任务
     */
    public static final String TEST_SIMPLE_JOB_ADD = MODULE_URI_PREFIX + "/test/testSimpleJobAdd";

    /**
     * 测试添加延迟执行任务
     */
    public static final String TEST_DELAY_JOB_ADD = MODULE_URI_PREFIX + "/test/testDelayJobAdd";

    /**
     * 测试暂停定时任务
     */
    public static final String TEST_JOB_PAUSE = MODULE_URI_PREFIX + "/test/testJobPause";

    /**
     * 测试恢复定时任务
     */
    public static final String TEST_JOB_RESUME = MODULE_URI_PREFIX + "/test/testJobResume";

    /**
     * 测试重启定时任务
     */
    public static final String TEST_JOB_RESCHEDULE = MODULE_URI_PREFIX + "/test/testJobReschedule";

    /**
     * 测试删除定时任务
     */
    public static final String TEST_JOB_DEL = MODULE_URI_PREFIX + "/test/testJobDel";

}
