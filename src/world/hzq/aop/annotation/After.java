package world.hzq.aop.annotation;

import java.lang.annotation.*;

//后置通知(拿不到方法执行结果)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    /**
     * the pointcut expression bind
     */
    String value();
}
