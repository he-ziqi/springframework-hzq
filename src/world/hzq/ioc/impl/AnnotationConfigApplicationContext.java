package world.hzq.ioc.impl;

import world.hzq.ioc.AnnotationConfigRegistry;
import world.hzq.ioc.ApplicationContext;

/**
 * 注解bean扫描注册实现类
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry, ApplicationContext {
    private final AnnotationBeanDefinitionReader reader;
    private final AnnotationBeanDefinitionScanner scanner;
    public AnnotationConfigApplicationContext() {
        this.reader = new AnnotationBeanDefinitionReader(this);
        this.scanner = new AnnotationBeanDefinitionScanner(this);
        defaultLoad();
    }

    public AnnotationConfigApplicationContext(Class<?>... configClasses){
        this.reader = new AnnotationBeanDefinitionReader(this);
        this.scanner = new AnnotationBeanDefinitionScanner(this);
        registry(configClasses);
    }

    /**
     * 解析配置类,将配置的扫描包等信息传入scan方法
     * @param componentClasses 配置类字节码文件
     */
    @Override
    public void registry(Class<?>... componentClasses) {
        this.reader.register(componentClasses);
    }

    @Override
    public void scan(String... basePackages) {
        this.scanner.scan(basePackages);
    }

}
