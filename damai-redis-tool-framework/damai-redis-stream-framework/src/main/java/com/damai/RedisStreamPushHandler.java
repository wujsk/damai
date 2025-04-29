package com.damai;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author: haonan
 * @description: redis-stream 推送消息
 */
@Slf4j
@RequiredArgsConstructor
public class RedisStreamPushHandler {

    private final StringRedisTemplate redisTemplate;

    private final RedisStreamConfigProperties redisStreamConfigProperties;

    public RecordId push(String message) {
        ObjectRecord<String, String> record = StreamRecords.newRecord()
                .in(redisStreamConfigProperties.getStreamName())
                .ofObject(message)
                .withId(RecordId.autoGenerate());
        RecordId recordId = this.redisTemplate.opsForStream().add(record);
        log.info("redis streamName : {} message : {}", redisStreamConfigProperties.getStreamName(), message);
        return recordId;
    }
}
