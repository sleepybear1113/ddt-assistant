package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.vo.AppInfoVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
