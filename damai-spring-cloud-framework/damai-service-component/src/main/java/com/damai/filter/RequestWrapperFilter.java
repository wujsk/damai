package com.damai.filter;


import com.damai.request.CustomizeRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author: haonan
 * @description: request包装过滤器
 */
public class RequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CustomizeRequestWrapper customizeRequestWrapper = new CustomizeRequestWrapper(request);
        filterChain.doFilter(customizeRequestWrapper, response);
    }
}
