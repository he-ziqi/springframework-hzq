package world.hzq.aop;

/**
 * 连接点方法执行接口
 */
public interface ProceedingJointPoint extends JoinPoint{
    //执行目标方法
    Object proceed() throws Throwable;
}
