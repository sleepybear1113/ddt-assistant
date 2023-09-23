package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.logic.SettingLogic;
import cn.sleepybear.ddtassistant.update.vo.DownloadFileInfoVo;
import cn.sleepybear.ddtassistant.update.vo.UpdateInfoVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/setting/updateFile")
    public DownloadFileInfoVo updateFile(Long id, Integer versionId, Integer index) {
        return settingLogic.updateFile(id, versionId, index);
    }
}
