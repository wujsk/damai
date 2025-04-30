package com.damai.servicelock.info;


/**
 * @author: haonan
 * @description: 锁超时处理
 */
public interface LockTimeOutHandler {

    /**
     * 处理
     * @param lockName
     */
    void handle(String lockName);
}
