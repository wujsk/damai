package com.damai.config;


import com.damai.handle.RedissonDataHandle;
import com.damai.locallock.LocalLockCache;
import com.damai.lockinfo.factory.LockInfoHandleFactory;
import org.springframework.util.ReflectionUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: haonan
 * @description: redisson通用配置
 */
@EnableConfigurationProperties(RedissonBaseProperties.class)
public class RedissonCommonAutoConfiguration {

    private final AtomicInteger executeTaskThreadCount = new AtomicInteger(0);

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties, RedissonBaseProperties redissonBaseProperties){
        Config config = new Config();
        String prefix = "redis://";
        Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
        if (method != null && (Boolean)ReflectionUtils.invokeMethod(method, redisProperties)) {
            prefix = "rediss://";
        }
        config.useSingleServer()
                .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setConnectTimeout(1000)
                .setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword());
        config.setThreads(redissonBaseProperties.getThreads());
        config.setNettyThreads(redissonBaseProperties.getNettyThreads());
        if (Objects.nonNull(redissonBaseProperties.getCorePoolSize()) &&
                Objects.nonNull(redissonBaseProperties.getMaximumPoolSize())) {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    redissonBaseProperties.getCorePoolSize(),
                    redissonBaseProperties.getMaximumPoolSize(),
                    redissonBaseProperties.getKeepAliveTime(),
                    redissonBaseProperties.getUnit(),
                    new LinkedBlockingQueue<>(redissonBaseProperties.getWorkQueueSize()),
                    r -> new Thread(Thread.currentThread().getThreadGroup(), r,
                            "redisson-thread-" + executeTaskThreadCount.getAndIncrement()));
            config.setExecutor(threadPoolExecutor);
        }
        return Redisson.create(config);
    }

    @Bean
    public LocalLockCache localLockCache(){
        return new LocalLockCache();
    }

    @Bean
    public LockInfoHandleFactory lockInfoHandleFactory(){
        return new LockInfoHandleFactory();
    }

    @Bean
    public RedissonDataHandle redissonDataSource(RedissonClient redissonClient){
        return new RedissonDataHandle(redissonClient);
    }
}
