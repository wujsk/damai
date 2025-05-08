package com.damai.context;


import com.damai.core.DelayProduceQueue;
import com.damai.core.IsolationRegionSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: haonan
 * @description: 延迟队列 发送者 分片选择
 */
public class DelayQueueProduceCombine {

    private final IsolationRegionSelector isolationRegionSelector;

    private final List<DelayProduceQueue> delayProduceQueueList = new ArrayList<>();

    public DelayQueueProduceCombine(DelayQueueBasePart delayQueueBasePart,String topic) {
        Integer isolationRegionCount = delayQueueBasePart.getDelayQueueProperties().getIsolationRegionCount();
        isolationRegionSelector = new IsolationRegionSelector(isolationRegionCount);
        for (int i = 0; i < isolationRegionCount; i++) {
            delayProduceQueueList.add(new DelayProduceQueue(delayQueueBasePart.getRedissonClient(), topic + "-" + i));
        }
    }

    public void offer(String content, long delayTime, TimeUnit timeUnit) {
        // 拿取分片，发送消息
        int index = isolationRegionSelector.getIndex();
        delayProduceQueueList.get(index).offer(content, delayTime, timeUnit);
    }
}
