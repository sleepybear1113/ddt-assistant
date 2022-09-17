package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.constant.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sleepybear
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
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
}