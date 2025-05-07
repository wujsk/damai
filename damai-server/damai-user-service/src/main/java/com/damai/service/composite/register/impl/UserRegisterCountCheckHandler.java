package com.damai.service.composite.register.impl;


import com.damai.service.composite.register.AbstractUserRegisterCheckHandler;
import com.damai.dto.UserRegisterDto;
import com.damai.enums.BaseCode;
import com.damai.exception.DaMaiFrameException;
import com.damai.service.tool.RequestCounter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: haonan
 * @description: 用户注册请求数检查
 */
@Component
public class UserRegisterCountCheckHandler extends AbstractUserRegisterCheckHandler {

    @Resource
    private RequestCounter requestCounter;

    @Override
    protected void execute(UserRegisterDto param) {
        boolean result = requestCounter.onRequest();
        if (result) {
            throw new DaMaiFrameException(BaseCode.USER_REGISTER_FREQUENCY);
        }
    }

    @Override
    public Integer executeParentOrder() {
        return 0;
    }

    @Override
    public Integer executeTier() {
        return 1;
    }

    @Override
    public Integer executeOrder() {
        return 1;
    }
}
