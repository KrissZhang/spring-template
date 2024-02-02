package com.self.common.enums;

import com.self.common.constants.CacheConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 延迟队列消息类型
 * (相当于topic, 同一类型业务应当共用一个枚举类型以减少侦听线程数量)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RedisDelayQueueEnum {

    TEST_DELAY_RECORD_1(CacheConstants.DELAY_QUEUE_KEY + "testDelayRecord1", "测试延迟消息1", "testDelayRecord1"),

    TEST_DELAY_RECORD_2(CacheConstants.DELAY_QUEUE_KEY + "testDelayRecord2", "测试延迟消息2", "testDelayRecord2");

    /**
     * 延迟队列 redis key
     */
    private String code;

    /**
     * 延迟队列名称
     */
    private String name;

    /**
     * 延迟队列业务 bean
     */
    private String beanId;

}
