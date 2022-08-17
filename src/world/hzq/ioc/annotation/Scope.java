package world.hzq.ioc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.CONSTRUCTOR,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    //默认单例
    boolean singleton() default true;
}
