package world.hzq.aop.impl;

import world.hzq.aop.AfterThrowingAdvice;
import world.hzq.aop.MethodInterceptor;
import world.hzq.aop.PointExpression;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

//异常通知实现(Cglib预留)
public class AspectAfterThrowingAdvice extends AbstractAspectAdvice implements AfterThrowingAdvice, MethodInterceptor {
    @Override
    public AdviceWay getKind() {
        return null;
    }

    @Override
    public PointExpression getPointExpression() {
        return null;
    }

    @Override
    public Object invoke(Object target, Method method, Throwable t) {
        return null;
    }
}
