package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.constant.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;

/**
 * @author sleepybear
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
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
        InterceptorRegistration registration = registry.addInterceptor(userLoginInterceptor);
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(
                "/login.html",
                "/**/login.html",
                "/**/*.js",
                "/**/*.css"
        );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowCredentials(false)
                .exposedHeaders("")
                .maxAge(3600);
    }
}