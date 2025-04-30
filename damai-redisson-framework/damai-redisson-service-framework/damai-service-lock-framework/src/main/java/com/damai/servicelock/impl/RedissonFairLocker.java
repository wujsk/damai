package com.damai.servicelock.impl;


import com.damai.servicelock.ServiceLocker;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author: haonan
 * @description: 分布式 公平锁
 */
@RequiredArgsConstructor
public class RedissonFairLocker implements ServiceLocker {

    private final RedissonClient redissonClient;

    @Override
    public RLock getLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    @Override
    public RLock lock(String lockKey) {
        RLock fairLock = getLock(lockKey);
        fairLock.lock();
        return fairLock;
    }

    @Override
    public RLock lock(String lockKey, long leaseTime) {
        RLock fairLock = getLock(lockKey);
        fairLock.lock(leaseTime, TimeUnit.SECONDS);
        return fairLock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock fairLock = getLock(lockKey);
        fairLock.lock(leaseTime, unit);
        return fairLock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime) {
        try {
            return getLock(lockKey).tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        try {
            return getLock(lockKey).tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        getLock(lockKey).unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }
}
