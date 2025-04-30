package com.damai.core;


import com.damai.servicelock.LockType;
import com.damai.servicelock.ServiceLocker;
import com.damai.servicelock.impl.RedissonFairLocker;
import com.damai.servicelock.impl.RedissonReadLocker;
import com.damai.servicelock.impl.RedissonReentrantLocker;
import com.damai.servicelock.impl.RedissonWriteLocker;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: haonan
 * @description: 锁管理中心
 */
public class LockManager {

    private final Map<String, ServiceLocker> serviceLockMap = new HashMap<>();

    public LockManager(RedissonClient redissonClient) {
        serviceLockMap.put(LockType.Reentrant.getType(), new RedissonReentrantLocker(redissonClient));
        serviceLockMap.put(LockType.Fair.getType(), new RedissonFairLocker(redissonClient));
        serviceLockMap.put(LockType.Read.getType(), new RedissonReadLocker(redissonClient));
        serviceLockMap.put(LockType.Write.getType(), new RedissonWriteLocker(redissonClient));
    }

    public ServiceLocker getServiceLock(String lockType) {
        return serviceLockMap.get(lockType);
    }
}
