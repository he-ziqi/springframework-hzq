package world.hzq.aop.impl.advice;

import world.hzq.aop.*;
import world.hzq.aop.annotation.AfterThrowing;
import world.hzq.aop.impl.AbstractAspectAdvice;
import world.hzq.aop.impl.Agent;
import world.hzq.aop.impl.AgentParser;
import world.hzq.aop.impl.ReflectiveMethodInvocation;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

/**
 * 异常通知实现
 */
public class AfterThrowingAdviceImpl extends AbstractAspectAdvice implements MethodInterceptor, AfterThrowingAdvice {
    //通知方法
    private final Method adviceMethod;
    //代理人
    private final Agent agent;
    //通知方法对应的切点表达式
    private final PointExpression pointExpression;
    @Override
    public AdviceWay getKind() {
        return AfterThrowingAdvice.way;
    }

    @Override
    public PointExpression getPointExpression() {
        return pointExpression;
    }

    public Object invoke(MethodInvocation mi) {
        Object res = null;
        try {
            res = mi.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return res;
    }

    public AfterThrowingAdviceImpl(Method adviceMethod, Agent agent) throws NoSuchMethodException {
        this.adviceMethod = adviceMethod;
        this.agent = agent;
        pointExpression = new AgentParser(agent).getPointExpression(AfterThrowing.class,adviceMethod);
    }

    @Override
    public Object invoke(Object target, Method method, Throwable t) {
        return invoke(new ReflectiveMethodInvocation(null,target,method,null,t));
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public Agent getAgent() {
        return agent;
    }
}
