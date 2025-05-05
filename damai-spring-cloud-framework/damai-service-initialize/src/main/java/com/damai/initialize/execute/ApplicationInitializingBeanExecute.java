package com.damai.initialize.execute;


import com.damai.initialize.execute.base.AbstractApplicationExecute;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ConfigurableApplicationContext;

import static com.damai.initialize.constant.InitializeHandlerType.APPLICATION_INITIALIZING_BEAN;

/**
 * @author: haonan
 * @description: 用于处理 {@link InitializingBean} 应用程序启动事件。
 */
public class ApplicationInitializingBeanExecute extends AbstractApplicationExecute implements InitializingBean {

    public ApplicationInitializingBeanExecute(ConfigurableApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public String type() {
        return APPLICATION_INITIALIZING_BEAN;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        execute();
    }
}
