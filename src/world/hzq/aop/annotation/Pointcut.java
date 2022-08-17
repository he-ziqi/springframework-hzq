package world.hzq.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pointcut {
    /**
     * pointcut expression
     */
    String value() default "";
}
