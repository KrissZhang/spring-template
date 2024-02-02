package com.self.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedissonUtils.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 带超时锁(秒)
     */
    public RLock lock(String lockKey, long timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 带超时锁
     */
    public RLock lock(String lockKey, TimeUnit unit, long timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**
     * 尝试锁
     * @param waitTime 最多等待时间
     */
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 尝试锁
     * @param waitTime 最多等待时间(秒)
     * @param leaseTime 上锁后自动释放锁时间(秒)
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 尝试锁
     * @param waitTime 最多等待时间(秒)
     * @param leaseTime 上锁后自动释放锁时间(秒)
     */
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 释放锁
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }

    /**
     * key被锁定且被当前线程持有时才释放锁
     */
    public void unlockAviable(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if(lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * key被锁定且被当前线程持有时才释放锁
     */
    public void unlockAviable(RLock lock) {
        if(lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 添加延迟队列消息
     * @param queueCode 队列key
     * @param delay 延迟时间
     * @param timeUnit 延迟时间单位
     * @param value 队列值
     */
    public <T> void addDelayQueue(String queueCode, long delay, TimeUnit timeUnit, T value){
        try{
            RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(redissonClient.getBlockingQueue(queueCode));
            delayedQueue.offer(value, delay, timeUnit);
            logger.info("发送延迟消息，队列key: {}，队列值: {}，发送时间: {}，延迟时间: {}", queueCode, value, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"), (timeUnit.toSeconds(delay) + "秒"));
        }catch (Exception e){
            throw new RuntimeException("添加延时队列失败: " + e.getMessage());
        }
    }

    /**
     * 获取延迟队列消息
     * @param queueCode 队列key
     */
    public <T> T getDelayQueue(String queueCode) throws InterruptedException {
        RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueCode);
        return blockingQueue.take();
    }

}
