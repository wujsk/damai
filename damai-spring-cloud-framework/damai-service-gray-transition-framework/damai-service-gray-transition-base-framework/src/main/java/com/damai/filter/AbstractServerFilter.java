package com.damai.filter;


import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.Ordered;

import java.util.Iterator;
import java.util.List;

/**
 * @author: haonan
 * @description: 过滤器基类
 */
public abstract class AbstractServerFilter implements Ordered {

    public void filter(List<? extends ServiceInstance> servers) {
        Iterator<? extends ServiceInstance> iterator = servers.iterator();
        while (iterator.hasNext()) {
            ServiceInstance service = iterator.next();
            if (!doFilter(servers, service)) {
                iterator.remove();
            }
        }
    }

    public abstract boolean doFilter(List<? extends ServiceInstance> servers, ServiceInstance service);
}
