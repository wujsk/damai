package com.damai.config;


import com.damai.util.SpringUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @author: haonan
 * @description: 通用配置
 */
public class DaMaiCommonAutoConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustom(){
        return new JacksonCustom();
    }

    @Bean
    public SpringUtil springUtil(){
        return new SpringUtil();
    }
}
