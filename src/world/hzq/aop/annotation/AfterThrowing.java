package world.hzq.aop.annotation;

import java.lang.annotation.*;

//异常通知
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterThrowing {
    /**
     * the pointcut expression bind
     */
    String value();

    /**
     * the exception name bind
     */
    String throwing();
}
