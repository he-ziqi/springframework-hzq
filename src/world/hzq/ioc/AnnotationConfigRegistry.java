package world.hzq.ioc;
//注解注册配置接口
public interface AnnotationConfigRegistry {
    //注册字节码文件
    void registry(Class<?>... componentClasses);
    //扫描包
    void scan(String... basePackages);
}
