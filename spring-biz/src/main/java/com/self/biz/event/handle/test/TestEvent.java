package com.self.biz.event.handle.test;

import org.springframework.context.ApplicationEvent;

/**
 * 测试事件
 */
public class TestEvent extends ApplicationEvent {

    public TestEvent(Object source) {
        super(source);
    }

}
