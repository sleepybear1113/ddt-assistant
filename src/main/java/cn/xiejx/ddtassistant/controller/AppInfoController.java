package cn.xiejx.ddtassistant.controller;


import cn.xiejx.ddtassistant.logic.AppInfoLogic;
import cn.xiejx.ddtassistant.vo.AppInfoVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sleepybear
 */
@RestController
public class AppInfoController {

    @Resource
    private AppInfoLogic appInfoLogic;

    @RequestMapping("/app/info")
    public AppInfoVo getAppInfo() {
        return appInfoLogic.getAppInfo();
    }
}
