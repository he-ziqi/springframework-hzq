package world.hzq.aop;

/**
 * 方法基本信息
 */
public interface Signature {
    //获取方法名
    String getName();
    //获取方法修饰符
    int getModifiers();
    //获取方法返回值类型
    Class<?> getReturnType();
    Class<?> getDeclaring();
}
