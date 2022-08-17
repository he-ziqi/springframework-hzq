package world.hzq.ioc.impl;

import world.hzq.ioc.AnnotationConfigClassResolve;
import world.hzq.ioc.BeanFactory;

/**
 * 抽象应用上下文,包括部分bean工厂方法实现及bean工厂抽象方法
 */
public abstract class AbstractApplicationContext implements BeanFactory, AnnotationConfigClassResolve {

    //获取bean工厂的抽象方法由子类GenericApplicationContext实现
    public abstract AnnotationBeanFactory getBeanFactory();

    @Override
    public Object getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        //bean工厂存活检验(待定)
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return getBeanFactory().getBean(name,requiredType);
    }

    @Override
    public boolean isSingleton(String name) {
        return getBeanFactory().isSingleton(name);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    @Override
    public Class<?> getType(String name) {
        return getBeanFactory().getType(name);
    }

    @Override
    public void resolve(Class<?>... configClass) {
        getBeanFactory().resolveConfigClass(configClass);
    }
}