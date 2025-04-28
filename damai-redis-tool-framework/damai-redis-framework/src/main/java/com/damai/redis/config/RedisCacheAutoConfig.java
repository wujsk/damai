package com.damai.redis.config;

import com.damai.redis.RedisCache;
import com.damai.redis.RedisCacheImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author: haonan
 * @description: redis封装实现配置
 */
public class RedisCacheAutoConfig {

    @Bean
    public RedisCache redisCache(@Qualifier("redisToolStringRedisTemplate") StringRedisTemplate stringRedisTemplate){
        return new RedisCacheImpl(stringRedisTemplate);
    }
}
