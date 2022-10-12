package com.self.biz.event.listener.test;

import com.self.biz.event.handle.test.TestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 测试事件监听器2
 */
@Component
public class TestEventListener2 {

    private static final Logger logger = LoggerFactory.getLogger(TestEventListener2.class);

    @Async
    @EventListener(TestEvent.class)
    public void listen(TestEvent event){
        logger.info("测试事件监听器2消息处理，消息：{}", event.getSource());
    }

}
