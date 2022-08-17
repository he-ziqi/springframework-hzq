package world.hzq.ioc;

public interface BeanFactory {
    //通过name获取bean
    Object getBean(String name);
    //通过类型获取bean
    <T> T getBean(Class<T> requiredType);
    //通过name和type获取bean
    <T> T getBean(String name,Class<T> requiredType);

    //判断bean是否为单例
    boolean isSingleton(String name);
    //判断是否包含bean
    boolean containsBean(String name);
    //判断bean的类型
    Class<?> getType(String name);
}