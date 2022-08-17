package world.hzq.aop;

import java.lang.reflect.Method;

/**
 * 检查切点表达式是否合法
 */
public interface PointExpression {

    /**
     * 确定切点表达式可以匹配给定类中的连接点
     * @param givenClass 给定类字节码文件
     * @return 匹配结果
     */
    boolean couldMatchJoinPointsInType(Class<?> givenClass);

    /**
     * 确定切点表达式是否与给定方法匹配
     * @param method 给定方法
     * @return 匹配结果
     */
    boolean matchMethodExecution(Method method);
}
