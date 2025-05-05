package com.damai.initialize.impl.composite.config;


import com.damai.initialize.impl.composite.CompositeContainer;
import com.damai.initialize.impl.composite.init.CompositeInit;
import org.springframework.context.annotation.Bean;

/**
 * @author: haonan
 * @description: 组合模式配置
 */
public class CompositeAutoConfiguration {

    @Bean
    public CompositeContainer compositeContainer(){
        return new CompositeContainer();
    }

    @Bean
    public CompositeInit compositeInit(CompositeContainer compositeContainer){
        return new CompositeInit(compositeContainer);
    }
}