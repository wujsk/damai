package com.damai.base;


import com.damai.threadlocal.BaseParameterHolder;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author: haonan
 * @description: 线程池基类
 */
public class BaseThreadPool {

    protected static Map<String, String> getContextForTask() {
        return MDC.getCopyOfContextMap();
    }

    protected static Map<String, String> getContextForHold() {
        return BaseParameterHolder.getParameterMap();
    }

    protected static Runnable wrapTask(Runnable task, Map<String, String> parentMdcContext, Map<String, String> parentHoldContext) {
        return () -> {
            Map<String, Map<String, String>> preprocess = preprocess(parentMdcContext, parentHoldContext);
            Map<String, String> mdcContext = preprocess.get("mdcContext");
            Map<String, String> holdContext = preprocess.get("holdContext");
            try {
                task.run();
            } finally {
                postProcess(mdcContext, holdContext);
            }
        };
    }

    protected static <T> Callable<T> wrapTask(Callable<T> task, Map<String, String> parentMdcContext, Map<String, String> parentHoldContext) {
        return () -> {
            Map<String, Map<String, String>> preprocess = preprocess(parentMdcContext, parentHoldContext);
            Map<String, String> mdcContext = preprocess.get("mdcContext");
            Map<String, String> holdContext = preprocess.get("holdContext");
            try {
                return task.call();
            } finally {
                postProcess(mdcContext, holdContext);
            }
        };
    }

    private static Map<String, Map<String, String>> preprocess(Map<String, String> parentMdcContext, Map<String, String> parentHoldContext) {
        Map<String, Map<String, String>> map = new HashMap<>(8);
        Map<String, String> holdContext = BaseParameterHolder.getParameterMap();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        if (parentMdcContext == null) {
            MDC.clear();
        } else {
            MDC.setContextMap(parentMdcContext);
        }
        if (parentHoldContext == null) {
            BaseParameterHolder.removeParameterMap();
        } else {
            BaseParameterHolder.setParameterMap(parentHoldContext);
        }
        map.put("holdContext", holdContext);
        map.put("mdcContext", mdcContext);
        return map;
    }

    private static void postProcess(Map<String, String> mdcContext, Map<String, String> holdContext) {
        if (mdcContext == null) {
            MDC.clear();
        } else {
            MDC.setContextMap(mdcContext);
        }
        if (holdContext == null) {
            BaseParameterHolder.removeParameterMap();
        } else {
            BaseParameterHolder.setParameterMap(holdContext);
        }
    }
}
