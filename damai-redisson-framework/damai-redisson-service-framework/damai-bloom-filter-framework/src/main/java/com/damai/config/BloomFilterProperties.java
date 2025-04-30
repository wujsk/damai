package com.damai.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: haonan
 * @description: 布隆过滤器配置类
 */
@Data
@ConfigurationProperties(prefix = BloomFilterProperties.PREFIX)
public class BloomFilterProperties {

    public static final String PREFIX = "bloom-filter";

    private String name;

    private Long expectedInsertions = 20000L;

    private Double falseProbability = 0.01D;
}
