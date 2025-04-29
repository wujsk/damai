package com.damai;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;

/**
 * @author: haonan
 * @description: 消息监听器
 */
@Slf4j
@RequiredArgsConstructor
public class RedisStreamListener implements StreamListener<String, ObjectRecord<String, String>> {

    private final MessageConsumer messageConsumer;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        try {
            RecordId messageId = message.getId();
            String value = message.getValue();
            log.info("redis stream 消费到了数据 messageId : {}, streamName : {}, message : {}",
                    messageId, message.getStream(), value);
            messageConsumer.accept(message);
        } catch (Exception e) {
            log.error("redis stream 消费消息失败", e);
        }
    }
}
