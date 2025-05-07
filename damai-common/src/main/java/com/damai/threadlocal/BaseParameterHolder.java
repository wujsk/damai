package com.damai.threadlocal;


import java.util.HashMap;
import java.util.Map;

/**
 * @author: haonan
 * @description: 线程绑定工具
 */
public class BaseParameterHolder {

    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL_MAP = new ThreadLocal<>();

    public static void setParameter(String key, String value) {
        Map<String, String> map = THREAD_LOCAL_MAP.get();
        if (map == null) {
            map = new HashMap<>(64);
        }
        map.put(key, value);
        THREAD_LOCAL_MAP.set(map);
    }

    public static String getParameter(String key) {
        Map<String, String> map = THREAD_LOCAL_MAP.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static void remove(String key) {
        Map<String, String> map = THREAD_LOCAL_MAP.get();
        if (map != null) {
            map.remove(key);
        }
    }

    public static Map<String, String> getParameterMap() {
        Map<String, String> map = THREAD_LOCAL_MAP.get();
        if (map == null) {
            return new HashMap<>(64);
        }
        return map;
    }

    public static ThreadLocal<Map<String, String>> getThreadLocalMap() {
        return THREAD_LOCAL_MAP;
    }

    public static void setParameterMap(Map<String, String> map) {
        THREAD_LOCAL_MAP.set(map);
    }

    public static void removeParameterMap(){
        THREAD_LOCAL_MAP.remove();
    }
}
