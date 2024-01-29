package com.self.biz.delayqueue;

/**
 * 延迟队列执行器
 */
public interface RedisDelayQueueHandler<T> {

    void execute(T t);

}
