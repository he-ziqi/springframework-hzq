package world.hzq.aop;

import java.lang.reflect.Method;

/**
 * 方法调用接口
 */
public interface MethodInvocation extends Invocation {
    Method getMethod();
}
