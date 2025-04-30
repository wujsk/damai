package com.damai.config;


import com.damai.aspect.ServiceLockAspect;
import com.damai.constant.LockInfoType;
import com.damai.core.LockManager;
import com.damai.lockinfo.LockInfoHandle;
import com.damai.lockinfo.factory.LockInfoHandleFactory;
import com.damai.lockinfo.impl.ServiceLockInfoHandle;
import com.damai.servicelock.factory.ServiceLockFactory;
import com.damai.util.ServiceLockTool;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * @author: haonan
 * @description: 分布式锁 配置
 */
public class ServiceLockAutoConfiguration {

    @Bean(LockInfoType.SERVICE_LOCK)
    public LockInfoHandle serviceLockInfoHandle(){
        return new ServiceLockInfoHandle();
    }

    @Bean
    public LockManager lockManager(RedissonClient redissonClient) {
        return new LockManager(redissonClient);
    }

    @Bean
    @ConditionalOnBean(LockManager.class)
    public ServiceLockFactory serviceLockFactory(LockManager lockManager) {
        return new ServiceLockFactory(lockManager);
    }

    @Bean
    public LockInfoHandleFactory lockInfoHandleFactory() {
        return new LockInfoHandleFactory();
    }

    @Bean
    @ConditionalOnBean({LockInfoHandleFactory.class, ServiceLockFactory.class})
    public ServiceLockTool serviceLockTool(LockInfoHandleFactory lockInfoHandleFactory, ServiceLockFactory serviceLockFactory){
        return new ServiceLockTool(lockInfoHandleFactory, serviceLockFactory);
    }

    @Bean
    @ConditionalOnBean({LockInfoHandleFactory.class, ServiceLockFactory.class})
    public ServiceLockAspect serviceLockAspect(LockInfoHandleFactory lockInfoHandleFactory, ServiceLockFactory serviceLockFactory){
        return new ServiceLockAspect(serviceLockFactory, lockInfoHandleFactory);
    }
}
