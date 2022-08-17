package world.hzq.ioc.annotation;

import java.lang.annotation.*;

/**
 * 扫描指定包上所有类的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {
    String[] basePackages() default {};
}
