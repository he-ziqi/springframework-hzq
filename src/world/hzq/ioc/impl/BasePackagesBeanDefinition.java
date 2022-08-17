package world.hzq.ioc.impl;


import java.util.List;

public class BasePackagesBeanDefinition extends GenericBeanDefinition {
    public BasePackagesBeanDefinition(Class<?> beanClass, String beanClassName, String beanName, String scope, boolean lazyInit, List<Class<?>> interfaces, List<Class<?>> superClasses) {
        setBeanClass(beanClass);
        setBeanClassName(beanClassName);
        setBeanName(beanName);
        setScope(scope);
        setLazyInit(lazyInit);
        setBeanType(getBeanClass());
        setSuperClasses(superClasses);
        setInterfaces(interfaces);
    }
}
