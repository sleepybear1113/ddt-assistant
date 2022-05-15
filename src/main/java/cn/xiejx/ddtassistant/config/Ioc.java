package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.dm.DmDdt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sleepybear
 */
@Configuration
public class Ioc {

    @Bean
    public UserConfig init() {
        return UserConfig.readFromFile();
    }

    @Bean
    public DmDdt initDmDdt() {
        return DmDdt.createInstance(null);
    }
}
