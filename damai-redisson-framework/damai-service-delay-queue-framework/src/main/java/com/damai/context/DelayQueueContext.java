package com.damai.context;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: haonan
 * @description: 延迟队列 发送者上下文
 */
public class DelayQueueContext {

    private final DelayQueueBasePart delayQueueBasePart;

    private final Map<String, DelayQueueProduceCombine> delayQueueProduceCombineMap = new ConcurrentHashMap<>();

    public DelayQueueContext(DelayQueueBasePart delayQueueBasePart) {
        this.delayQueueBasePart = delayQueueBasePart;
    }

    public void sendMessage(String content, String topic, long delayTime, TimeUnit timeUnit) {
        DelayQueueProduceCombine delayQueueProduceCombine = delayQueueProduceCombineMap.computeIfAbsent(topic,
                k -> new DelayQueueProduceCombine(delayQueueBasePart, topic));
        delayQueueProduceCombine.offer(content, delayTime, timeUnit);
    }
}
