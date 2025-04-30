package com.damai.aspect;


import cn.hutool.core.util.StrUtil;
import com.damai.annotion.ServiceLock;
import com.damai.constant.LockInfoType;
import com.damai.lockinfo.LockInfoHandle;
import com.damai.lockinfo.factory.LockInfoHandleFactory;
import com.damai.servicelock.LockType;
import com.damai.servicelock.ServiceLocker;
import com.damai.servicelock.factory.ServiceLockFactory;
import com.damai.servicelock.info.LockTimeOutStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author: haonan
 * @description: 分布式锁 切面
 */
@Slf4j
@Aspect
@Order(-10)
@RequiredArgsConstructor
public class ServiceLockAspect {

    private final ServiceLockFactory serviceLockFactory;

    private final LockInfoHandleFactory lockInfoHandleFactory;

    @Around("@annotation(com.damai.annotion.ServiceLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ServiceLock serviceLock = signature.getMethod().getAnnotation(ServiceLock.class);
        if (serviceLock == null) {
            serviceLock = joinPoint.getTarget().getClass().getAnnotation(ServiceLock.class);
        }
        // 获取注解参数
        String name = serviceLock.name();
        LockType lockType = serviceLock.lockType();
        String[] keys = serviceLock.keys();
        long waitTime = serviceLock.waitTime();
        TimeUnit timeUnit = serviceLock.timeUnit();
        LockTimeOutStrategy lockTimeOutStrategy = serviceLock.lockTimeoutStrategy();
        String customLockTimeoutStrategy = serviceLock.customLockTimeoutStrategy();
        // 获取锁名
        LockInfoHandle lockInfoHandle = lockInfoHandleFactory.getLockInfoHandle(LockInfoType.SERVICE_LOCK);
        String lockName = lockInfoHandle.getLockName(joinPoint, name, keys);
        // 获取锁
        ServiceLocker lock = serviceLockFactory.getLock(lockType);
        boolean result = lock.tryLock(lockName, timeUnit, waitTime);
        if (result) {
            try {
                return joinPoint.proceed();
            } finally {
                lock.unlock(lockName);
            }
        } else {
            log.warn("Timeout while acquiring serviceLock:{}",lockName);
            if (StrUtil.isNotBlank(customLockTimeoutStrategy)) {
                return handleCustomLockTimeoutStrategy(customLockTimeoutStrategy, joinPoint, signature);
            } else {
                lockTimeOutStrategy.handle(lockName);
            }
            return joinPoint.proceed();
        }
    }

    private Object handleCustomLockTimeoutStrategy(String customLockTimeoutStrategy,
                                                   ProceedingJoinPoint joinPoint,
                                                   MethodSignature signature) {
        Method method = signature.getMethod();
        Object target = joinPoint.getTarget();
        Method handleMethod = null;
        Object result = null;
        try {
            handleMethod = target.getClass().getDeclaredMethod(customLockTimeoutStrategy, method.getParameterTypes());
            handleMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Illegal annotation param customLockTimeoutStrategy :" + customLockTimeoutStrategy,e);
        }
        Object[] args = joinPoint.getArgs();
        try {
            result = handleMethod.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Fail to illegal access custom lock timeout handler: " + customLockTimeoutStrategy ,e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Fail to invoke custom lock timeout handler: " + customLockTimeoutStrategy ,e);
        }
        return result;
    }
}
