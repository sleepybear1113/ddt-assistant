package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.update.helper.UpdateHelper;
import cn.xiejx.ddtassistant.update.vo.UpdateInfoVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/09 19:05
 */
@Component
public class SettingLogic {
    @Resource
    private SettingConfig settingConfig;
    @Resource
    private AppProperties appProperties;

    private UpdateInfoVo updateInfoVo;

    public boolean update(SettingConfig settingConfig) {
        if (settingConfig == null) {
            throw new FrontException("参数为空");
        }

        this.settingConfig.update(settingConfig);
        this.settingConfig.save();
        return true;
    }

    public SettingConfig get() {
        return this.settingConfig;
    }

    public UpdateInfoVo getUpdateInfoVo() {
        UpdateInfoVo updateInfoVo = UpdateHelper.checkUpdate(appProperties.getVersion(), settingConfig.getUpdateConfig().getUrl());
        this.updateInfoVo = updateInfoVo;
        return updateInfoVo;
    }

    public Boolean updateFile(Long id, Integer versionId, Long index) {
        if (this.updateInfoVo == null) {
            throw new FrontException("请先获取更新！");
        }
        if (!this.updateInfoVo.getId().equals(id)) {
            throw new FrontException("更新信息过时，请重新获取更新！");
        }

        return true;
    }
}
