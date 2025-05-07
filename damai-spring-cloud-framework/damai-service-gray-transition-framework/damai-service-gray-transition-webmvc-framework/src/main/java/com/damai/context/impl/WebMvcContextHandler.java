package com.damai.context.impl;

import com.damai.context.ContextHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @description: WebMvc上下文获取实现
 * @author: haonan
 **/
public class WebMvcContextHandler implements ContextHandler {
    @Override
    public String getValueFromHeader(final String name) {
        HttpServletRequest request = getHttpServletRequest();
        if (Objects.nonNull(request)) {
            return request.getHeader(name);
        }
        return null;
    }
    
    public HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = getRestAttributes();
        if (attributes == null) {
            return null;
        }
        
        return attributes.getRequest();
    }
    
    public ServletRequestAttributes getRestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return (ServletRequestAttributes) requestAttributes;
    }
}
