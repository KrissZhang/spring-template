package com.self.common.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonUtils {

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

}
