package cn.xiejx.ddtassistant.annotation;

import java.lang.annotation.*;

/**
 * @author sleepybear
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAppInfo {

    String value() default "";
}
