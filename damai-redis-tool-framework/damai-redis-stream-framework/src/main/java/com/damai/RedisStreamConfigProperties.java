package com.damai;


import com.damai.constant.RedisStreamConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: haonan
 * @description: redis-stream配置类
 */
@Data
@ConfigurationProperties(prefix = RedisStreamConfigProperties.PREFIX)
public class RedisStreamConfigProperties {

    public static final String PREFIX = "spring.data.redis.stream";

    /**
     * stream名称
     */
    private String streamName;

    /**
     * 消费组名称
     */
    private String consumerGroup;

    /**
     * 消费者名称
     */
    private String consumerName;

    /**
     * 消费方式 默认为GROUP
     */
    private String consumerType = RedisStreamConstant.GROUP;
}
