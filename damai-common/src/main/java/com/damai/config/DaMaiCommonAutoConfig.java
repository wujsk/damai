package com.damai.config;


import com.damai.util.SpringUtil;
import org.springframework.context.annotation.Bean;

/**
 * @author: haonan
 * @description: 通用配置
 */
public class DaMaiCommonAutoConfig {

    @Bean
    public SpringUtil springUtil(){
        return new SpringUtil();
    }
}
