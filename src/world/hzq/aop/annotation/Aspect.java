package world.hzq.aop.annotation;

import java.lang.annotation.*;

//切面类
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aspect {
    String value() default "";
}
