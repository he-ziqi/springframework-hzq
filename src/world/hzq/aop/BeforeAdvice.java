package world.hzq.aop;

import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

public interface BeforeAdvice extends Advice{
    AdviceWay way = AdviceWay.BEFORE;
    Object invoke(Object target, Method method);
}
