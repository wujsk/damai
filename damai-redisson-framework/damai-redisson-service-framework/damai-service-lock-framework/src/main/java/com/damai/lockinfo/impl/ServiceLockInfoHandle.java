package com.damai.lockinfo.impl;


import com.damai.lockinfo.AbstractLockInfoHandle;

/**
 * @author: haonan
 * @description: 锁信息实现(分布式锁)
 */
public class ServiceLockInfoHandle extends AbstractLockInfoHandle {

    private static final String LOCK_PREFIX_NAME = "SERVICE_LOCK";

    @Override
    protected String getLockPrefixName() {
        return LOCK_PREFIX_NAME;
    }
}
