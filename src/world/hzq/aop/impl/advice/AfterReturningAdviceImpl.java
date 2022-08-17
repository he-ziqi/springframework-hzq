package world.hzq.aop.impl.advice;

import world.hzq.aop.AfterReturningAdvice;
import world.hzq.aop.MethodInterceptor;
import world.hzq.aop.MethodInvocation;
import world.hzq.aop.PointExpression;
import world.hzq.aop.annotation.AfterReturning;
import world.hzq.aop.impl.AbstractAspectAdvice;
import world.hzq.aop.impl.Agent;
import world.hzq.aop.impl.AgentParser;
import world.hzq.aop.impl.ReflectiveMethodInvocation;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

/**
 * 返回通知实现
 */
public class AfterReturningAdviceImpl extends AbstractAspectAdvice implements MethodInterceptor, AfterReturningAdvice {
    //通知方法
    private final Method adviceMethod;
    //代理人
    private final Agent agent;
    //通知方法对应的切点表达式
    private final PointExpression pointExpression;
    public Object invoke(MethodInvocation mi) {
        Object res = null;
        try {
            res = mi.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return res;
    }

    public AfterReturningAdviceImpl(Method adviceMethod, Agent agent) throws NoSuchMethodException {
        this.adviceMethod = adviceMethod;
        this.agent = agent;
        pointExpression = new AgentParser(agent).getPointExpression(AfterReturning.class,adviceMethod);
    }

    @Override
    public AdviceWay getKind() {
        return AfterReturningAdvice.way;
    }

    @Override
    public PointExpression getPointExpression() {
        return pointExpression;
    }

    @Override
    public Object invoke(Object target, Method method, Object arg) {
        return invoke(new ReflectiveMethodInvocation(null,target,method,new Object[]{arg},null));
    }

    public Agent getAgent() {
        return agent;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }
}
