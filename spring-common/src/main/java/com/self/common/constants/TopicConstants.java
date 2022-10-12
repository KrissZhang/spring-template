package com.self.common.constants;

/**
 * Kafka Topic
 * @author zp
 */
public final class TopicConstants {

    private TopicConstants() {
        super();
    }

    /**
     * 测试 Topic
     */
    public static final String APP_TOPIC_TEST_RECORD = "${app.topic.test_record:TEST-RECORD-DEV}";

}
