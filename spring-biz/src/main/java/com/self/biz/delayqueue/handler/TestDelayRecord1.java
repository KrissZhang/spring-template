package com.self.biz.delayqueue.handler;

import com.alibaba.fastjson.JSON;
import com.self.biz.delayqueue.RedisDelayQueueHandler;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 测试延迟消息1处理
 */
@Component
public class TestDelayRecord1 implements RedisDelayQueueHandler<String> {

    @Override
    public void execute(String s) {
        //测试延迟消息1接收处理
        System.out.println("延迟消息1接收时间: " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println("延迟消息1接收参数: " + JSON.toJSONString(s));
    }

}
