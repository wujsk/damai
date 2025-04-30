package com.damai.config;


import com.damai.constant.LockInfoType;
import com.damai.handle.RedissonDataHandle;
import com.damai.info.impl.RepeatExecuteLimitLockInfoHandle;
import com.damai.locallock.LocalLockCache;
import com.damai.lockinfo.factory.LockInfoHandleFactory;
import com.damai.repeatexecutelimit.aspect.RepeatExecuteLimitAspect;
import com.damai.servicelock.factory.ServiceLockFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author: haonan
 * @description: 防重复幂等配置
 */
public class RepeatExecuteLimitAutoConfiguration {

    @Bean(LockInfoType.REPEAT_EXECUTE_LIMIT)
    public RepeatExecuteLimitLockInfoHandle repeatExecuteLimitLockInfoHandle(){
        return new RepeatExecuteLimitLockInfoHandle();
    }

    @Bean
    public RepeatExecuteLimitAspect repeatExecuteLimitAspect(LocalLockCache localLockCache,
                                                             LockInfoHandleFactory lockInfoHandleFactory,
                                                             ServiceLockFactory serviceLockFactory,
                                                             RedissonDataHandle redissonDataHandle){
        return new RepeatExecuteLimitAspect(localLockCache, lockInfoHandleFactory,serviceLockFactory,redissonDataHandle);
    }
}
