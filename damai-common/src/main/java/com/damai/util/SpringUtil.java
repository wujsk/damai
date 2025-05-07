package com.damai.util;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static com.damai.constant.Constant.DEFAULT_PREFIX_DISTINCTION_NAME;
import static com.damai.constant.Constant.PREFIX_DISTINCTION_NAME;

/**
 * @author: haonan
 * @description: spring 工具类
 */
public class SpringUtil implements ApplicationContextAware {

    private static ConfigurableApplicationContext configurableApplicationContext;

    public static String getPrefixDistinctionName() {
        return configurableApplicationContext.getEnvironment().getProperty(PREFIX_DISTINCTION_NAME,
                DEFAULT_PREFIX_DISTINCTION_NAME);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
