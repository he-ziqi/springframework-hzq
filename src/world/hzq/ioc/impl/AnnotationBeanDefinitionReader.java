package world.hzq.ioc.impl;

import world.hzq.ioc.BeanDefinitionRegistry;
import world.hzq.ioc.annotation.*;

import java.lang.annotation.Annotation;

//bean注册读取器
public class AnnotationBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    public AnnotationBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public void register(Class<?>... componentClasses){
        for (Class<?> componentClass : componentClasses) {
            registerBean(componentClass);
        }
    }

    //加载配置类,通过配置类创建ioc容器
    public void registerBean(Class<?> beanClass) {
        //非注解类
        if(null == beanClass.getDeclaredAnnotation(Configuration.class)){
            return;
        }
        if(registry instanceof AnnotationConfigApplicationContext){
            AnnotationConfigApplicationContext applicationContext = (AnnotationConfigApplicationContext) registry;
            //对配置类进行解析
            applicationContext.resolve(beanClass);
            //从配置类中获取要注入ioc容器的信息
            for (Annotation declaredAnnotation : beanClass.getDeclaredAnnotations()) {
                //获取类上注解的信息
                if(declaredAnnotation instanceof ComponentScan){
                    ComponentScan scan = (ComponentScan) declaredAnnotation;
                    String[] packages = scan.basePackages();
                    //通过scan方法获取扫描的类的字节码文件
                    for (String packageName : packages) {
                        applicationContext.scan(packageName);
                    }
                }
            }
        }
    }
}