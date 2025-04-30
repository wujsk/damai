package com.damai.info.impl;


import com.damai.lockinfo.AbstractLockInfoHandle;

/**
 * @author: haonan
 * @description:
 */
public class RepeatExecuteLimitLockInfoHandle extends AbstractLockInfoHandle {

    public static final String PREFIX_NAME = "REPEAT_EXECUTE_LIMIT";

    @Override
    protected String getLockPrefixName() {
        return PREFIX_NAME;
    }
}
