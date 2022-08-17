package world.hzq.aop.impl;

import world.hzq.aop.AfterAdvice;
import world.hzq.aop.MethodInterceptor;
import world.hzq.aop.PointExpression;
import world.hzq.aop.impl.autoproxy.AdviceWay;

import java.lang.reflect.Method;

//后置通知实现(Cglib预留)
public class AspectAfterAdvice extends AbstractAspectAdvice implements AfterAdvice, MethodInterceptor {

    @Override
    public AdviceWay getKind() {
        return null;
    }

    @Override
    public PointExpression getPointExpression() {
        return null;
    }

    @Override
    public Object invoke(Object target, Method method) {
        return null;
    }
}
