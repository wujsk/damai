package com.damai.service.tool;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: haonan
 * @description: 请求计数器
 */
@Component
@Slf4j
public class RequestCounter {

    private final AtomicInteger count = new AtomicInteger(0);

    private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());

    @Value("${request_count_threshold:1000}")
    private int maxRequestsPerSecond;

    public synchronized boolean onRequest() {
        long currentTime = System.currentTimeMillis();
        long differenceValue = 1000;
        if (currentTime - lastResetTime.get() >= differenceValue) {
            lastResetTime.set(currentTime);
            count.set(0);
        }

        if (count.incrementAndGet() > maxRequestsPerSecond) {
            log.warn("请求超过每秒{}次限制", maxRequestsPerSecond);
            count.set(0);
            lastResetTime.set(System.currentTimeMillis());
            return true;
        }
        return false;
    }
}
