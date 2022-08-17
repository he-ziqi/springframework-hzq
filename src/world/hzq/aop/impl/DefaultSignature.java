package world.hzq.aop.impl;

import world.hzq.aop.Signature;

import java.lang.reflect.Method;

/**
 * 默认Signature实现,用于获取目标方法的信息
 */
public class DefaultSignature implements Signature {
    private final Method method;

    public DefaultSignature(Method method) {
        this.method = method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public Class<?> getDeclaring() {
        return method.getDeclaringClass();
    }

}