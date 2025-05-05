package com.damai.initialize.execute.base;


import com.damai.initialize.base.InitializeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Comparator;
import java.util.Map;

/**
 * @author: haonan
 * @description:
 */
@RequiredArgsConstructor
public abstract class AbstractApplicationExecute {

    private final ConfigurableApplicationContext applicationContext;

    public void execute() {
        Map<String, InitializeHandler> initializeHandlerMap = applicationContext.getBeansOfType(InitializeHandler.class);
        initializeHandlerMap.values().stream()
                .filter(r -> r.type().equals(this.type()))
                .sorted(Comparator.comparing(InitializeHandler::executeOrder))
                .forEach(r -> r.executeInit(applicationContext));
    }

    /**
     * 初始化执行 类型
     * @return 类型
     * */
    public abstract String type();
}
