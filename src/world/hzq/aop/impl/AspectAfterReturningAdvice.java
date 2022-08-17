package world.hzq.aop.impl;

import world.hzq.aop.AfterReturningAdvice;
import world.hzq.aop.MethodInterceptor;
import world.hzq.aop.PointExpression;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

//返回通知实现(Cglib预留)
public class AspectAfterReturningAdvice extends AbstractAspectAdvice implements AfterReturningAdvice, MethodInterceptor {

    @Override
    public AdviceWay getKind() {
        return null;
    }

    @Override
    public PointExpression getPointExpression() {
        return null;
    }

    @Override
    public Object invoke(Object target, Method method, Object arg) {
        return null;
    }
}
