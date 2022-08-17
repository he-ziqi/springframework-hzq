package world.hzq.aop;

import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

public interface AfterAdvice extends Advice{
    AdviceWay way = AdviceWay.AFTER;
    Object invoke(Object target, Method method);
}
