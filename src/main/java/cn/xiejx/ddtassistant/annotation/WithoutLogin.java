package cn.xiejx.ddtassistant.annotation;

import java.lang.annotation.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/09 19:26
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithoutLogin {
}
