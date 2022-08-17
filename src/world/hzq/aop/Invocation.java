package world.hzq.aop;

public interface Invocation extends ProceedingJointPoint{
    //返回调用的参数
    Object[] arguments();
}
