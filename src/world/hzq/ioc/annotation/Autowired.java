package world.hzq.ioc.annotation;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    //默认需要进行依赖注入
    boolean required() default true;
}
