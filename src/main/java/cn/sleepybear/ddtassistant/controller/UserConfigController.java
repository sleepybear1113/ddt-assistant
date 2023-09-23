package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.base.UserConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sleepybear
 */
@RestController
@Slf4j
public class UserConfigController {
    @Resource
    private UserConfig userConfig;

    @RequestMapping("/config/update")
    public void update(@RequestBody UserConfig userConfig) {
        if (userConfig == null) {
            log.info("userConfig is null");
            return;
        }

        this.userConfig.setUserConfig(userConfig);
        userConfig.save();
    }

    @RequestMapping("/config/get")
    public UserConfig get() {
        return userConfig;
    }

    @RequestMapping("/config/reset")
    public UserConfig reset() {
        this.userConfig.setUserConfig(new UserConfig().defaultConfig());
        this.userConfig.save();
        return this.userConfig;
    }
}
