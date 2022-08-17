package world.hzq.aop.annotation;

import java.lang.annotation.*;

//环绕通知
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Around {
    /**
     * the pointcut expression bind
     */
    String value();
}
