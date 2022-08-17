package world.hzq.aop.annotation;

import java.lang.annotation.*;

//前置通知
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    /**
     * the pointcut expression bind
     */
    String value();
}
