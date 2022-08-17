package world.hzq.aop;

/**
 * 代理方法执行接口
 */
public interface ProxyMethodInvocation extends MethodInvocation{
    Object getProxy();
}
