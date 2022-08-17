package world.hzq.aop;


import world.hzq.aop.impl.autoproxy.AdviceWay;

public interface Advice {
    //获取通知类型
    AdviceWay getKind();
    //获取切点表达式
    PointExpression getPointExpression();
}
