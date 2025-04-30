package com.damai.lockinfo;


import cn.hutool.core.util.StrUtil;
import com.damai.constant.Constants;
import com.damai.parser.ExtParameterNameDiscoverer;
import com.damai.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: haonan
 * @description: 锁信息抽象
 */
@Slf4j
public abstract class AbstractLockInfoHandle implements LockInfoHandle {

    private static final String LOCK_DISTRIBUTE_ID_NAME_PREFIX = "LOCK_DISTRIBUTE_ID";

    private final ParameterNameDiscoverer nameDiscoverer = new ExtParameterNameDiscoverer();

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 锁信息前缀
     * @return 具体前缀
     * */
    protected abstract String getLockPrefixName();

    @Override
    public String getLockName(JoinPoint joinPoint, String name, String[] keys) {
        return SpringUtil.getPrefixDistinctionName() + Constants.HYPHEN + getLockPrefixName()
                + Constants.SEPARATOR + name + Constants.SEPARATOR + getRelKey(joinPoint, keys);
    }

    @Override
    public String simpleGetLockName(String name, String[] keys) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String key : keys) {
            if (StrUtil.isNotBlank(key)) {
                definitionKeyList.add(key);
            }
        }
        return SpringUtil.getPrefixDistinctionName() + Constants.HYPHEN + LOCK_DISTRIBUTE_ID_NAME_PREFIX +
                Constants.SEPARATOR + name +
                Constants.SEPARATOR + String.join(Constants.SEPARATOR, definitionKeyList);
    }

    public String getRelKey(JoinPoint joinPoint, String[] keys) {
        Method method = getMethod(joinPoint);
        List<String> definitionKeys = getSpElKey(keys, method, joinPoint.getArgs());
        return String.join(Constants.SEPARATOR, definitionKeys);
    }

    public Method getMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            } catch (Exception e) {
                log.error("获取方法异常", e);
            }
        }
        return method;
    }

    private List<String> getSpElKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (!ObjectUtils.isEmpty(definitionKey)) {
                EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);
                Object objKey = parser.parseExpression(definitionKey).getValue(context);
                definitionKeyList.add(ObjectUtils.nullSafeToString(objKey));
            }
        }
        return definitionKeyList;
    }
}
