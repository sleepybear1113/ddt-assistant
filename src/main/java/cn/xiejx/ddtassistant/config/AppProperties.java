package cn.xiejx.ddtassistant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author sleepybear
 */
@Configuration
public class AppProperties {

    private String appVersion;
    private Integer version;

    public String getAppVersion() {
        return appVersion;
    }

    @Value("${app.version}")
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Value("${app.versionNum}")
    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }
}
