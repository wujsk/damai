package com.damai.service.composite.register.impl;


import com.damai.service.composite.register.AbstractUserRegisterCheckHandler;
import com.damai.dto.UserRegisterDto;
import com.damai.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: haonan
 * @description:
 */
@Component
public class UserExistCheckHandler extends AbstractUserRegisterCheckHandler {

    @Resource
    private UserService userService;

    @Override
    protected void execute(UserRegisterDto param) {
        userService.doExist(param.getMobile());
    }

    @Override
    public Integer executeParentOrder() {
        return 1;
    }

    @Override
    public Integer executeTier() {
        return 2;
    }

    @Override
    public Integer executeOrder() {
        return 2;
    }
}
