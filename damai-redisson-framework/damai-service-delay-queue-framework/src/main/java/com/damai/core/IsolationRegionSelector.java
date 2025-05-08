package com.damai.core;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: haonan
 * @description: 延迟队列 分片选择器
 */
public class IsolationRegionSelector {

    private final AtomicInteger count = new AtomicInteger(0);

    private final int thresholdValue;

    public IsolationRegionSelector(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public int reset() {
        count.set(0);
        return count.get();
    }

    public synchronized int getIndex() {
        int cur = count.get();
        if (cur >= thresholdValue) {
            cur = reset();
        } else {
            cur = count.incrementAndGet();
        }
        return cur;
    }
}
