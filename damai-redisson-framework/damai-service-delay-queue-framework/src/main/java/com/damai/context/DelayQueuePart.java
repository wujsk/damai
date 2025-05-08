package com.damai.context;


import com.damai.core.ConsumerTask;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author: haonan
 * @description: 消息主题
 */
@Data
@RequiredArgsConstructor
public class DelayQueuePart {

    private final DelayQueueBasePart delayQueueBasePart;

    private final ConsumerTask consumerTask;
}
