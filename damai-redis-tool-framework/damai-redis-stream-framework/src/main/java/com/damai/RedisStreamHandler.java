package com.damai;


import com.alibaba.fastjson.JSON;
import com.damai.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: haonan
 * @description: redis-stream操作
 */
@Slf4j
@RequiredArgsConstructor
public class RedisStreamHandler {

    private final StringRedisTemplate redisTemplate;

    private final RedisStreamPushHandler redisStreamPushHandler;

    public Boolean hasKey(String key) {
        if (StringUtil.isEmpty(key)) {
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    public void addGroup(String streamName, String group) {
        redisTemplate.opsForStream().createGroup(streamName, group);
    }

    public void del(String streamName, RecordId recordId) {
        redisTemplate.opsForStream().delete(streamName, recordId);
    }

    /**
     * redis-stream绑定group
     *
     */
    public void streamBindingGroup(String streamName, String group) {
        Map<String, String> message = new HashMap<>();
        message.put("key", "value");
        RecordId recordId = redisStreamPushHandler.push(JSON.toJSONString(message));
        addGroup(streamName, group);
        del(streamName, recordId);
        log.info("initStream streamName : {} group : {}",streamName,group);
    }
}
