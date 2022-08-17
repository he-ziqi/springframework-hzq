package world.hzq.aop.impl;

import world.hzq.aop.ProceedingJointPoint;
import world.hzq.aop.Signature;

import java.lang.reflect.Method;

/**
 * 默认执行连接点实现
 */
public class DefaultProceedingJoinPoint implements ProceedingJointPoint {
    private final Object proxyObj;
    private final Object target;
    private final Method method;
    private final Object[] args;
    private Signature signature;

    //代理对象、目标对象、目标方法、目标方法参数
    public DefaultProceedingJoinPoint(Object proxyObj, Object target, Method method, Object[] args) {
        this.proxyObj = proxyObj;
        this.target = target;
        this.method = method;
        this.args = args;
        signature = getSignatureInstance();
    }

    /**
     * 执行目标方式
     * @return 执行结果
     */
    @Override
    public Object proceed() throws Throwable {
        return method.invoke(target, args);
    }

    /**
     * 获取当前正在执行的代理对象
     */
    @Override
    public Object getThis() {
        return proxyObj;
    }

    /**
     * 获取目标对象
     */
    @Override
    public Object getTarget() {
        return target;
    }

    /**
     * 获取当前执行方法的参数
     */
    @Override
    public Object[] getArgs() {
        return args;
    }

    /**
     * 获取Signature
     */
    @Override
    public Signature getSignature() {
        return getSignatureInstance();
    }

    private Signature getSignatureInstance() {
        if(signature == null){
            synchronized (this){
                if(signature == null){
                    signature = new DefaultSignature(method);
                }
            }
        }
        return signature;
    }
}
