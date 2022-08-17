package world.hzq.ioc;

public interface ApplicationContext {
    Object getBean(String name);
    <T> T getBean(Class<T> beanClass);
    <T> T getBean(String name,Class<T> beanClass);
}