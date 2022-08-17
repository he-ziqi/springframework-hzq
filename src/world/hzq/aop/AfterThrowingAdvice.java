package world.hzq.aop;

import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

public interface AfterThrowingAdvice extends Advice{
    AdviceWay way = AdviceWay.AFTER_THROWING;
    Object invoke(Object target, Method method,Throwable t);
}
