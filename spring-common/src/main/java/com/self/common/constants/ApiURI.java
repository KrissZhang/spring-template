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
     * 测试x-www-form-urlencoded请求
     */
    public static final String TEST_FORM_URLENCODED_REQ = MODULE_URI_PREFIX + "/test/testFormUrlEncoded";

    /**
     * 测试函数式接口
     */
    public static final String TEST_FUNCTION = MODULE_URI_PREFIX + "/test/testFunction";

    /**
     * 测试脱敏
     */
    public static final String TEST_SENSITIVE = MODULE_URI_PREFIX + "/test/testSensitive";

    /**
     * 测试逻辑删除
     */
    public static final String TEST_DEL = MODULE_URI_PREFIX + "/test/testDel";

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

    /**
     * 测试上传文件
     */
    public static final String TEST_UPLOAD_FILE = MODULE_URI_PREFIX + "/test/testUploadFile";

    /**
     * 测试下载文件
     */
    public static final String TEST_DOWNLOAD_FILE = MODULE_URI_PREFIX + "/test/testDownloadFile";

    /**
     * 测试导入
     */
    public static final String TEST_IMPORT = MODULE_URI_PREFIX + "/test/testImport";

    /**
     * 测试导出
     */
    public static final String TEST_EXPORT = MODULE_URI_PREFIX + "/test/testExport";

    /**
     * 测试Retrofit
     */
    public static final String TEST_RETROFIT = MODULE_URI_PREFIX + "/test/testRetrofit";

    /**
     * 测试Kafka消息
     */
    public static final String TEST_KAFKA_SEND = MODULE_URI_PREFIX + "/test/testKafkaSend";

}
