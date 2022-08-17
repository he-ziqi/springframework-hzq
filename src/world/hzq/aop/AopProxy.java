package world.hzq.aop;

public interface AopProxy {
    //创建一个新的代理对象
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
