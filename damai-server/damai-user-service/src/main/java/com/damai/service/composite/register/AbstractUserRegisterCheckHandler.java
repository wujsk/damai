package com.damai.service.composite.register;


import com.damai.dto.UserRegisterDto;
import com.damai.enums.CompositeCheckType;
import com.damai.initialize.impl.composite.AbstractComposite;

/**
 * @author: haonan
 * @description: 用户注册验证基类，用户注册的相关验证逻辑继承此类
 */
public abstract class AbstractUserRegisterCheckHandler extends AbstractComposite<UserRegisterDto> {

    @Override
    public String type() {
        return CompositeCheckType.USER_REGISTER_CHECK.getValue();
    }
}
