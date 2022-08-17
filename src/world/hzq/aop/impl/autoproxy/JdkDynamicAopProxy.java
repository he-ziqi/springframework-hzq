package world.hzq.aop.impl.autoproxy;

import world.hzq.aop.*;
import world.hzq.aop.annotation.*;
import world.hzq.aop.impl.*;
import world.hzq.aop.impl.advice.*;
import world.hzq.ioc.impl.ApplicationContextUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * jdk动态代理实现自动代理
 */
public final class JdkDynamicAopProxy extends AbstractJdkDynamicAopProxy {
    //代理人
    private final Agent agent;
    //代理人解析器
    private final AgentParser agentParser;
    //从ioc容器中获取代理对象
    private volatile static Object proxyObj;

    private Object getProxyObj(){
        if(proxyObj == null){
            synchronized (this){
                if(proxyObj == null){
                    proxyObj = ApplicationContextUtil.getApplicationContext().getBean(agent.getAspectClass());
                }
            }
        }
        return proxyObj;
    }

    public JdkDynamicAopProxy(Object target,Agent agent) throws NoSuchMethodException {
        this.agent = agent;
        this.target = target;
        agentParser = new AgentParser(agent);
    }

    /**
     * 环绕通知
     * @param method 目标方法
     * @param args 目标参数
     * @return 目标方法执行结果
     */
    @Override
    public void aroundAdvice(Method method, Object[] args) throws NoSuchMethodException {
        //获取aspect类的对象
        Object obj = getProxyObj();
        //获取所有的环绕增强方法
        List<Method> aroundMethods = agentParser.getInterceptedSpecifyAdviceWay(Around.class,method);
        for (Method aroundMethod : aroundMethods) {
            //通过环绕通知实现类执行环绕方法,并且注入连接点
            AroundAdvice aroundAdvice = new AroundAdviceImpl(aroundMethod,agent);
            aroundAdvice.invoke(obj, aroundMethod, new DefaultProceedingJoinPoint(proxyObj, target, method, args));
        }
    }

    /**
     * 执行指定通知方式的所有增强方法
     * @param AdviceWay 通知方式
     * @param res 源对象执行结果
     * @param t 异常对象
     * @param method 源对象
     */
    @Override
    public void advice(AdviceWay AdviceWay,Object res,Throwable t,Method method){
        try {
            List<Method> adviceMethods = null;
            if(world.hzq.aop.impl.autoproxy.AdviceWay.AFTER.equals(AdviceWay)){
                adviceMethods = agentParser.getInterceptedSpecifyAdviceWay(After.class,method);
            } else if(world.hzq.aop.impl.autoproxy.AdviceWay.BEFORE.equals(AdviceWay)){
                adviceMethods = agentParser.getInterceptedSpecifyAdviceWay(Before.class,method);
            } else if(world.hzq.aop.impl.autoproxy.AdviceWay.AFTER_RETURNING.equals(AdviceWay)){
                adviceMethods = agentParser.getInterceptedSpecifyAdviceWay(AfterReturning.class,method);
            } else if(world.hzq.aop.impl.autoproxy.AdviceWay.AFTER_THROWING.equals(AdviceWay)){
                adviceMethods = agentParser.getInterceptedSpecifyAdviceWay(AfterThrowing.class,method);
            }
            //执行所有的指定方式的增强方法
            if(adviceMethods == null){
                return;
            }
            //通过通知实现类装饰反射方法执行器调用当前增强方法
            for (Method adviceMethod : adviceMethods) {
                if(world.hzq.aop.impl.autoproxy.AdviceWay.AFTER.equals(AdviceWay)){
                    AfterAdvice afterAdvice = new AfterAdviceImpl(adviceMethod,agent);
                    afterAdvice.invoke(getProxyObj(),adviceMethod);
                } else if(world.hzq.aop.impl.autoproxy.AdviceWay.BEFORE.equals(AdviceWay)){
                    BeforeAdvice beforeAdvice = new BeforeAdviceImpl(adviceMethod,agent);
                    beforeAdvice.invoke(getProxyObj(),adviceMethod);
                } else if(world.hzq.aop.impl.autoproxy.AdviceWay.AFTER_RETURNING.equals(AdviceWay)){
                    AfterReturningAdvice afterReturningAdvice = new AfterReturningAdviceImpl(adviceMethod,agent);
                    afterReturningAdvice.invoke(getProxyObj(),adviceMethod,res);
                } else { //afterThrowing
                    AfterThrowingAdvice afterThrowingAdvice = new AfterThrowingAdviceImpl(adviceMethod,agent);
                    afterThrowingAdvice.invoke(getProxyObj(),adviceMethod,t);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean shouldIntercept(Method method) throws NoSuchMethodException {
        return agentParser.shouldIntercept(method);
    }

}
