package world.hzq.ioc;

//bean注册父接口
public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName,BeanDefinition beanDefinition);
    BeanDefinition getBeanDefinition(String name);

    void defaultLoad();
}