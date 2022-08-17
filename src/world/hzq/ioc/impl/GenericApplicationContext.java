package world.hzq.ioc.impl;

import world.hzq.ioc.BeanDefinition;
import world.hzq.ioc.BeanDefinitionRegistry;

/**
 * bean工厂的创建及bean工厂抽象方法的实现
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {
    private final AnnotationBeanFactory beanFactory;

    public GenericApplicationContext() {
        this.beanFactory = new AnnotationBeanFactory(new PackageAndAnnotationResolve());
    }

    /**
     * 注册bean到bean工厂中
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(beanName,beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return beanFactory.getBeanDefinition(name);
    }

    @Override
    public void defaultLoad() {
        beanFactory.defaultLoad();
    }

    @Override
    public AnnotationBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
}
