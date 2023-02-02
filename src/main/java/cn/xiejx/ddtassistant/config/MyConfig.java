package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.constant.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author sleepybear
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
    @Resource
    private UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (String s : Constants.firstDirList()) {
            registry.addResourceHandler(s + "**").addResourceLocations("file:" + s);
        }

        String[] disks = new String[]{"C", "D", "E", "F"};
        for (String disk : disks) {
            registry.addResourceHandler(disk + "/**").addResourceLocations("file:" + disk + ":/");
        }
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(userLoginInterceptor);
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(
                "/login.html",
                "/**/login.html",
                "/**/login/**",
                "/**/*.js",
                "/**/*.css"
        );
    }
}