package cn.xiejx.ddtassistant.update.vo;

import cn.xiejx.ddtassistant.update.constant.UpdateConstants;
import cn.xiejx.ddtassistant.update.domain.MainVersion;
import cn.xiejx.ddtassistant.update.domain.MainVersionInfo;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 10:11
 */
@Data
public class UpdateInfoVo implements Serializable {
    private static final long serialVersionUID = 2501852184313825820L;

    private List<MainVersionInfoVo> versionInfoList = new ArrayList<>();

    private String message;
    private Long id;

    public void generateId() {
        this.id = System.currentTimeMillis();
    }

    public static UpdateInfoVo build(MainVersion mainVersion, int currentVersion) {
        UpdateInfoVo updateInfoVo = new UpdateInfoVo();
        updateInfoVo.generateId();
        if (mainVersion == null) {
            updateInfoVo.setMessage("更新信息获取失败！");
            return updateInfoVo;
        }

        List<MainVersionInfoVo> infoList = Util.copyBean(mainVersion.getVersions(), MainVersionInfoVo.class);
        if (CollectionUtils.isNotEmpty(infoList)) {
            infoList.removeIf(o -> o.getVersion() < currentVersion);
            for (MainVersionInfoVo mainVersionInfoVo : infoList) {
                mainVersionInfoVo.build();
            }
        }

        updateInfoVo.setVersionInfoList(infoList);
        return updateInfoVo;
    }
}
