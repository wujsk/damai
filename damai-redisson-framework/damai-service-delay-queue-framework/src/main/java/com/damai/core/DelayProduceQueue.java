package com.damai.core;


import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author: haonan
 * @description: 延迟队列 延迟队列
 */
public class DelayProduceQueue extends DelayBaseQueue {

    private final RDelayedQueue<String> delayedQueue;

    public DelayProduceQueue(RedissonClient redissonClient, String relTopic) {
        super(redissonClient, relTopic);
        delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
    }

    public void offer(String message, long delayTime, TimeUnit timeUnit) {
        delayedQueue.offer(message, delayTime, timeUnit);
    }
}
