package cn.sleepybear.ddtassistant.utils;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/02 21:45
 */
public class SpringContextUtil {
    @Getter
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
