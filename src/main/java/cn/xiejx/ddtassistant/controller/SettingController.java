package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.logic.SettingLogic;
import cn.xiejx.ddtassistant.update.helper.UpdateHelper;
import cn.xiejx.ddtassistant.update.vo.UpdateInfoVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/09 19:04
 */
@RestController
public class SettingController {

    @Resource
    private SettingLogic settingLogic;

    @RequestMapping("/setting/update")
    public boolean update(@RequestBody SettingConfig settingConfig) {
        return settingLogic.update(settingConfig);
    }

    @RequestMapping("/setting/get")
    public SettingConfig get() {
        return settingLogic.get();
    }

    @RequestMapping("/setting/getUpdateInfoVo")
    public UpdateInfoVo getUpdateInfoVo() {
        return settingLogic.getUpdateInfoVo();
    }
}
