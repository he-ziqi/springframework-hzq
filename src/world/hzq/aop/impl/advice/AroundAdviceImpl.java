package world.hzq.aop.impl.advice;

import world.hzq.aop.*;
import world.hzq.aop.annotation.Around;
import world.hzq.aop.impl.AbstractAspectAdvice;
import world.hzq.aop.impl.Agent;
import world.hzq.aop.impl.AgentParser;
import world.hzq.aop.impl.DefaultProceedingJoinPoint;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 环绕通知实现
 */
public class AroundAdviceImpl extends AbstractAspectAdvice implements MethodInterceptor, AroundAdvice {
    //通知方法
    private final Method adviceMethod;
    //代理人
    private final Agent agent;
    //通知方法对应的切点表达式
    private final PointExpression pointExpression;

    public AroundAdviceImpl(Method adviceMethod, Agent agent) throws NoSuchMethodException {
        this.adviceMethod = adviceMethod;
        this.agent = agent;
        pointExpression = new AgentParser(agent).getPointExpression(Around.class,adviceMethod);
    }

    @Override
    public AdviceWay getKind() {
        return AroundAdvice.way;
    }

    @Override
    public PointExpression getPointExpression() {
        return pointExpression;
    }

    @Override
    public Object invoke(Object target, Method method, DefaultProceedingJoinPoint joinPoint) {
        Object res = null;
        try {
            res = method.invoke(target,joinPoint);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Agent getAgent() {
        return agent;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }
}
