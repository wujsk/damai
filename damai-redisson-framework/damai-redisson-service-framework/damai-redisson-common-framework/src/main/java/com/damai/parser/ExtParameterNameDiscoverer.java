package com.damai.parser;


import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.NativeDetector;

/**
 * @author: haonan
 * @description: 对DefaultParameterNameDiscoverer进行扩展，添加{@link LocalVariableTableParameterNameDiscoverer}
 */
public class ExtParameterNameDiscoverer extends DefaultParameterNameDiscoverer {

    public ExtParameterNameDiscoverer() {
        super();
        // 检测当前是否运行在 GraalVM 原生镜像（Native Image）环境中的方法
        if (!NativeDetector.inNativeImage()) {
            super.addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        }
    }
}
