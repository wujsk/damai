package com.damai.util;


/**
 * @author: haonan
 * @description: 分布式锁 方法类型执行 无返回值的业务
 */
@FunctionalInterface
public interface TaskRun {

    /**
     * 执行任务
     * */
    void run();
}
