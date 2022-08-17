package world.hzq.ioc.impl;

import world.hzq.ioc.BeanDefinitionRegistry;

/**
 * 注解bean扫描实现类
 */
public class AnnotationBeanDefinitionScanner {
    private final BeanDefinitionRegistry registry;
    public AnnotationBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    //扫描指定包下的所有类
    public void scan(String[] basePackages) {
        doScan(basePackages);
    }


    /**
     * 通过包名反射创建所有包中有特定注解的类对象
     * 通过registry注册到AnnotationBeanFactory中的beanDefinitionMap中
     */
    protected void doScan(String[] basePackages) {
        if(null != basePackages){
            for (String basePackage : basePackages) {
                registry.registerBeanDefinition(basePackage,null);
            }
        }
    }

    public void defaultLoad(){
        //registry.defaultLoad();
    }
}
