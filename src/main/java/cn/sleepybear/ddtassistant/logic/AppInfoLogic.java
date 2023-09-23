package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.config.AppProperties;
import cn.sleepybear.ddtassistant.vo.AppInfoVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author sleepybear
 */
@Component
public class AppInfoLogic {

    @Resource
    private AppProperties appProperties;

    public AppInfoVo getAppInfo() {
        AppInfoVo appInfoVo = new AppInfoVo();
        appInfoVo.setAppVersion(appProperties.getAppVersion());
        return appInfoVo;
    }
}
