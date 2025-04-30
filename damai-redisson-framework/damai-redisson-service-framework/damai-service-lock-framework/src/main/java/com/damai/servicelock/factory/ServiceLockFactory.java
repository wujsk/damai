package com.damai.servicelock.factory;


/**
 * @author: haonan
 * @description: 锁工厂
 */

import com.damai.core.LockManager;
import com.damai.servicelock.LockType;
import com.damai.servicelock.ServiceLocker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceLockFactory {

    private final LockManager manageLocker;

    /**
     * 获取锁 默认是可重入锁
     * @param lockType
     * @return
     */
    public ServiceLocker getLock(LockType lockType){
        if (LockType.isInLockType(lockType.getType())) {
            return manageLocker.getServiceLock(lockType.getType());
        } else {
            return manageLocker.getServiceLock(LockType.Reentrant.getType());
        }
    }
}
