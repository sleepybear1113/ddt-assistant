package cn.sleepybear.ddtassistant.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author sleepybear
 */
@Getter
@Configuration
public class AppProperties {

    private String appVersion;
    private Integer version;

    @Value("${app.version}")
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Value("${app.versionNum}")
    public void setVersion(Integer version) {
        this.version = version;
    }

}
