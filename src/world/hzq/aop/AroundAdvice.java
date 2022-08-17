package world.hzq.aop;

import world.hzq.aop.impl.DefaultProceedingJoinPoint;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

//环绕通知接口
public interface AroundAdvice extends Advice{
    AdviceWay way = AdviceWay.AROUND;
    Object invoke(Object target, Method method, DefaultProceedingJoinPoint joinPoint);
}
