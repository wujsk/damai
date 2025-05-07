package com.damai.service.init;


import cn.hutool.core.collection.CollectionUtil;
import com.damai.BusinessThreadPool;
import com.damai.handler.BloomFilterHandler;
import com.damai.initialize.base.AbstractApplicationPostConstructHandler;
import com.damai.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: haonan
 * @description: 手机号码缓存
 */
@Component
public class UserBloomFilterInitData extends AbstractApplicationPostConstructHandler {

    @Resource
    private BloomFilterHandler bloomFilterHandler;

    @Resource
    private UserService userService;

    @Override
    public Integer executeOrder() {
        return 1;
    }

    @Override
    public void executeInit(ConfigurableApplicationContext context) {
        BusinessThreadPool.execute(() -> {
            List<String> allMobile = userService.getAllMobile();
            if (CollectionUtil.isNotEmpty(allMobile)) {
                allMobile.forEach(mobile -> bloomFilterHandler.add(mobile));
            }
        });
    }
}
