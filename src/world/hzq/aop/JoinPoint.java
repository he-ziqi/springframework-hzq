package world.hzq.aop;

/**
 * 连接点
 */
public interface JoinPoint {
    //获取当前正在执行的对象
    Object getThis();
    //获取目标对象
    Object getTarget();
    //获取目标对象的参数
    Object[] getArgs();
    //获取方法声明
    Signature getSignature();
}
