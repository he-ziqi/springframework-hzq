package world.hzq.aop.impl.autoproxy;

import world.hzq.aop.AopProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理抽象父类(定义通知的算法模板)
 */
public abstract class AbstractJdkDynamicAopProxy implements InvocationHandler, AopProxy {
    //目标对象
    public Object target;

    /**
     * 拦截目标对象的当前方法并对当前方法进行增强
     * @param proxy 代理对象
     * @param method 当前方法
     * @param args 当前方法参数
     * @return 当前方法执行结果
     * @throws Throwable 当前方法异常
     */
    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //不拦截,放行
        if(!shouldIntercept(method)){
            return method.invoke(target,args);
        }
        //前置通知
        advice(AdviceWay.BEFORE, null, null, method);
        Object res = null;
        try {
            //方法执行结果
            res = method.invoke(target, args);
            //返回通知
            advice(AdviceWay.AFTER_RETURNING, res, null, method);
        } catch (Throwable t) {
            //异常通知(将异常对象传递给切面方法)
            advice(AdviceWay.AFTER_THROWING, null, t, method);
            //发生异常时为res适配返回值类型 当返回值类型为基本数据类型时,如果返回null会出现新错误
            res = adapterType(method.getReturnType());
        } finally {
            //后置通知
            advice(AdviceWay.AFTER, null, null, method);
        }
        //环绕通知
        aroundAdvice(method,args);
        return res;
    }

    /**
     * 环绕通知方法
     * @param method 目标方法
     * @param args 目标方法参数
     */
    protected abstract void aroundAdvice(Method method, Object[] args) throws NoSuchMethodException;

    /**
     * 发生异常时为res适配返回值类型 当返回值类型为基本数据类型时,如果返回null会出现新错误
     * @param returnType 返回值类型
     */
    protected Object adapterType(Class<?> returnType){
        if(returnType.equals(boolean.class)){
            return false;
        }else {
            Class<?>[] integerReturn = new Class[]{
                    int.class, short.class, byte.class, long.class,
                    float.class, double.class, char.class
            };
            for (Class<?> aClass : integerReturn) {
                if (aClass.equals(returnType)) {
                    return 0;
                }
            }
        }
        return null;
    }

    /**
     * 通知方法，对前置、后置、返回、异常通知方法的抽象
     * @param way 通知方式
     * @param res 目标方法执行结果
     * @param t 目标方法异常对象
     * @param method 目标方法
     */
    protected abstract void advice(AdviceWay way, Object res, Throwable t, Method method);

    /**
     * 判断当前方法是否应该进行拦截
     * @param method 当前方法
     */
    protected abstract boolean shouldIntercept(Method method) throws NoSuchMethodException;

    @Override
    public Object getProxy() {
        ClassLoader classLoader = AbstractJdkDynamicAopProxy.class.getClassLoader();
        if(classLoader == null){
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return getProxy(classLoader);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return target != null ? Proxy.newProxyInstance(classLoader,target.getClass().getInterfaces(),this) : null;
    }

}
