package world.hzq.ioc;

//注解配置类解析接口
@FunctionalInterface
public interface AnnotationConfigClassResolve {
    void resolve(Class<?>... configClass);
}