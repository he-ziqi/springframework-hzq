package world.hzq.ioc.annotation;

import java.lang.annotation.*;

/**
 * 中心配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
    String value() default "";
}
