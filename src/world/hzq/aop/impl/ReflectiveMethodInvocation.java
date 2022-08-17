package world.hzq.aop.impl;

import world.hzq.aop.ProxyMethodInvocation;
import world.hzq.aop.Signature;

import java.lang.reflect.Method;

/**
 * 反射方法执行器
 */
public class ReflectiveMethodInvocation implements ProxyMethodInvocation {
    private final Signature signature;
    private Object target;
    private Object proxy;
    private Method method;
    private Object[] args;
    private Throwable t;
    public ReflectiveMethodInvocation(Object proxy,Object target,Method method,Object[] args,Throwable t) {
        this.signature = new DefaultSignature(method);
        this.proxy = proxy;
        this.target = target;
        this.args = args;
        this.method = method;
        this.t = t;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] arguments() {
        return args;
    }

    @Override
    public Object getThis() {
        return proxy;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    /**
     * 方法执行
     */
    @Override
    public Object proceed() throws Throwable {
        if(getArgs() != null){
            return getMethod().invoke(getTarget(),getArgs());
        }else if(getT() != null){
            return getMethod().invoke(getTarget(),getT());
        }
        return getMethod().invoke(getTarget());
    }

    @Override
    public Object getProxy() {
        return proxy;
    }

    public Throwable getT() {
        return t;
    }
}
