package world.hzq.aop;

import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends Advice{
    AdviceWay way = AdviceWay.AFTER_RETURNING;
    Object invoke(Object target, Method method, Object arg);
}
