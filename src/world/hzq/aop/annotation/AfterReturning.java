package world.hzq.aop.annotation;

import java.lang.annotation.*;

//返回通知(可以拿到方法的返回结果)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterReturning {
    /**
     * the pointcut expression bind
     */
    String value();
}
