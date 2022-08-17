package world.hzq.aop.impl;

import java.lang.reflect.Method;
import java.util.Map;

//代理人(用于beanDefinition agent属性的设置)
public class Agent {
    //切面类
    private Class<?> aspectClass;
    //切点解析器map
    private Map<Method,PointExpressionImpl> pointcutParser;

    public Agent(Class<?> aspectClass, Map<Method, PointExpressionImpl> pointcutParser) {
        this.aspectClass = aspectClass;
        this.pointcutParser = pointcutParser;
    }

    public Class<?> getAspectClass() {
        return aspectClass;
    }

    public Map<Method, PointExpressionImpl> getPointcutParser() {
        return pointcutParser;
    }
}
