package com.damai.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: haonan
 * @description: elasticsearch配置属性
 */
@Data
@ConfigurationProperties(prefix = BusinessEsProperties.PREFIX)
public class BusinessEsProperties {

    public static final String PREFIX = "elasticsearch";

    private String[] ip;

    private String userName;

    private String passWord;

    private Boolean esSwitch = true;

    private Boolean esTypeSwitch = false;

    private Integer connectTimeOut = 40000;

    private Integer socketTimeOut = 40000;

    private Integer connectionRequestTimeOut = 40000;

    private Integer maxConnectNum = 400;
}
