package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.update.constant.UpdateConstants;
import cn.xiejx.ddtassistant.update.helper.UpdateHelper;
import cn.xiejx.ddtassistant.update.vo.DownloadFileInfoVo;
import cn.xiejx.ddtassistant.update.vo.FileInfoVo;
import cn.xiejx.ddtassistant.update.vo.MainVersionInfoVo;
import cn.xiejx.ddtassistant.update.vo.UpdateInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

        List<FileInfoVo> fileInfoVoList = mainVersionInfoVo.getUpdateListVo().getStatics();
        List<FileInfoVo> fileInfoVoListCopy = new ArrayList<>(fileInfoVoList);
        if (index != -1) {
            fileInfoVoListCopy.removeIf(f -> !f.getId().equals(index));
        }

        if (CollectionUtils.isEmpty(fileInfoVoListCopy)) {
            throw new FrontException("没有要下载更新的文件！");
        }

        DownloadFileInfoVo downloadFileInfoVo = new DownloadFileInfoVo();
        for (int i = 0; i < fileInfoVoListCopy.size(); i++) {
            FileInfoVo fileInfoVo = fileInfoVoListCopy.get(i);

            UpdateConstants.UpdateStrategyEnum updateStrategyEnumByType = UpdateConstants.UpdateStrategyEnum.getUpdateStrategyEnumByType(fileInfoVo.getUpdateStrategy());
            if (UpdateConstants.UpdateStrategyEnum.NO_ACTION.equals(updateStrategyEnumByType)) {
                continue;
            } else if (UpdateConstants.UpdateStrategyEnum.DOWNLOAD_ONLY_NOT_EXIST.equals(updateStrategyEnumByType) && index == -1) {
                continue;
            }

            log.info("正在处理第{}/{}个文件", (i + 1), fileInfoVoListCopy.size());
            if (UpdateConstants.UpdateStrategyEnum.UPDATE_ALL.equals(updateStrategyEnumByType) || UpdateConstants.UpdateStrategyEnum.UPDATE_RECOMMEND.equals(updateStrategyEnumByType)) {
                log.info("下载文件[{}]", fileInfoVoListCopy.get(i).getFilename());
                boolean b = UpdateHelper.downloadFile(fileInfoVo);
                if (b) {
                    downloadFileInfoVo.addSuccessCount();
                } else {
                    downloadFileInfoVo.addFailCount();
                }
            } else if (UpdateConstants.UpdateStrategyEnum.DELETE.equals(updateStrategyEnumByType)) {
                log.info("删除文件[{}]", fileInfoVoListCopy.get(i).getFilename());
            }
        }

        return downloadFileInfoVo;
    }
}
