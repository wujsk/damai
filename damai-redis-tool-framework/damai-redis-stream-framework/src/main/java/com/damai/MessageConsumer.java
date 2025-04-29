package com.damai;


import org.springframework.data.redis.connection.stream.ObjectRecord;

/**
 * @author: haonan
 * @description: 消息消费者
 */
@FunctionalInterface
public interface MessageConsumer {

    /**
     * 消费消息
     * @param message
     */
    void accept(ObjectRecord<String, String> message);
}
