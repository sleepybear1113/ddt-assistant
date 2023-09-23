package cn.sleepybear.ddtassistant.controller;


import cn.sleepybear.ddtassistant.annotation.WithoutLogin;
import cn.sleepybear.ddtassistant.logic.AppInfoLogic;
import cn.sleepybear.ddtassistant.vo.AppInfoVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sleepybear
 */
@RestController
public class AppInfoController {

    @Resource
    private AppInfoLogic appInfoLogic;

    @WithoutLogin
    @RequestMapping("/app/info")
    public AppInfoVo getAppInfo() {
        return appInfoLogic.getAppInfo();
    }
}
