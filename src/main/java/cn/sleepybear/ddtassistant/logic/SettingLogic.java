package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.update.vo.DownloadFileInfoVo;
import cn.sleepybear.ddtassistant.update.vo.MainVersionInfoVo;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.config.AppProperties;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.update.constant.UpdateConstants;
import cn.sleepybear.ddtassistant.update.helper.UpdateHelper;
import cn.sleepybear.ddtassistant.update.vo.UpdateFileInfoVo;
import cn.sleepybear.ddtassistant.update.vo.UpdateInfoVo;
import cn.sleepybear.ddtassistant.utils.Util;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/09 19:05
 */
@Component
@Slf4j
public class SettingLogic {
    @Resource
    private SettingConfig settingConfig;
    @Resource
    private AppProperties appProperties;

    private UpdateInfoVo updateInfoVo;

    @PostConstruct
    public void init() {
        checkUpdate();
    }

    public void checkUpdate() {
        GlobalVariable.THREAD_POOL.execute(() -> {
            Util.sleep(4000L);
            log.info("检测新版本中...");
            if (!Boolean.TRUE.equals(settingConfig.getUpdateConfig().getEnableAutoCheckUpdate())) {
                return;
            }
            UpdateHelper.checkUpdate(appProperties.getVersion(), settingConfig.getUpdateConfig());
        });
    }

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
        UpdateInfoVo updateInfoVo = UpdateHelper.checkUpdate(appProperties.getVersion(), settingConfig.getUpdateConfig());
        this.updateInfoVo = updateInfoVo;
        updateInfoVo.setCurrentVersion(appProperties.getVersion());
        return updateInfoVo;
    }

    public DownloadFileInfoVo updateFile(Long id, Integer versionId, Integer index) {
        if (this.updateInfoVo == null) {
            throw new FrontException("请先获取更新！");
        }
        if (!this.updateInfoVo.getId().equals(id)) {
            throw new FrontException("更新信息过时，请重新获取更新！");
        }
        if (index == null) {
            throw new FrontException("参数错误！");
        }

        MainVersionInfoVo mainVersionInfoVo = null;
        List<MainVersionInfoVo> versionInfoList = this.updateInfoVo.getVersionInfoList();
        for (MainVersionInfoVo m : versionInfoList) {
            if (m.getId().equals(versionId)) {
                mainVersionInfoVo = m;
                break;
            }
        }
        if (mainVersionInfoVo == null) {
            throw new FrontException("没有该版本更新信息！");
        }

        List<UpdateFileInfoVo> updateFileInfoVoList = mainVersionInfoVo.getUpdateListVo().getStatics();
        List<UpdateFileInfoVo> updateFileInfoVoListCopy = new ArrayList<>(updateFileInfoVoList);
        if (index != -1) {
            updateFileInfoVoListCopy.removeIf(f -> !f.getId().equals(index));
        }

        if (CollectionUtils.isEmpty(updateFileInfoVoListCopy)) {
            throw new FrontException("没有要下载更新的文件！");
        }

        DownloadFileInfoVo downloadFileInfoVo = new DownloadFileInfoVo();
        for (int i = 0; i < updateFileInfoVoListCopy.size(); i++) {
            UpdateFileInfoVo updateFileInfoVo = updateFileInfoVoListCopy.get(i);
            String filename = updateFileInfoVo.getFilename();

            UpdateConstants.UpdateStrategyEnum updateStrategyEnumByType = UpdateConstants.UpdateStrategyEnum.getUpdateStrategyEnumByType(updateFileInfoVo.getUpdateStrategy());
            if (UpdateConstants.UpdateStrategyEnum.NO_ACTION.equals(updateStrategyEnumByType)) {
                log.info("文件[{}]没有操作类型", filename);
                continue;
            } else if (UpdateConstants.UpdateStrategyEnum.DOWNLOAD_ONLY_NOT_EXIST.equals(updateStrategyEnumByType) && index == -1) {
                log.info("文件[{}]已存在不需要下载", filename);
                continue;
            }

            log.info("正在处理第{}/{}个文件", (i + 1), updateFileInfoVoListCopy.size());
            if (UpdateConstants.UpdateStrategyEnum.UPDATE_ALL.equals(updateStrategyEnumByType) || UpdateConstants.UpdateStrategyEnum.UPDATE_RECOMMEND.equals(updateStrategyEnumByType)) {
                log.info("下载文件[{}]", filename);
                boolean b = UpdateHelper.downloadFile(updateFileInfoVo);
                downloadFileInfoVo.addCount(b);
            } else if (UpdateConstants.UpdateStrategyEnum.DELETE.equals(updateStrategyEnumByType)) {
                boolean b = updateFileInfoVo.deleteFile();
                downloadFileInfoVo.addCount(b);
                log.info("删除文件[{}], 状态：{}", filename, b);
            }
        }

        return downloadFileInfoVo;
    }
}
