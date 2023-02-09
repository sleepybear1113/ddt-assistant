package cn.xiejx.ddtassistant.update.vo;

import cn.xiejx.ddtassistant.update.constant.UpdateConstants;
import cn.xiejx.ddtassistant.update.domain.FileInfo;
import cn.xiejx.ddtassistant.update.domain.UpdateList;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/06 14:30
 */
@Data
public class UpdateListVo implements Serializable {
    private static final long serialVersionUID = 6185148525628900837L;

    private String appVersion;
    private Integer version;

    private String baseUrl;
    private List<FileInfoVo> statics;

    public static UpdateListVo build(UpdateList updateList) {
        if (updateList == null) {
            return null;
        }

        UpdateListVo updateListVo = new UpdateListVo();
        updateListVo.appVersion = updateList.getAppVersion();
        updateListVo.version = updateList.getVersion();
        updateListVo.baseUrl = updateList.getBaseUrl();
        if (CollectionUtils.isNotEmpty(updateList.getStatics())) {
            List<FileInfoVo> list = new ArrayList<>();
            for (FileInfo fileInfo : updateList.getStatics()) {
                list.add(FileInfoVo.build(fileInfo, updateListVo.baseUrl));
            }
            list.removeIf(FileInfoVo::getSame);
            list.removeIf(vo -> UpdateConstants.UpdateStrategyEnum.getUpdateStrategyEnumByType(vo.getUpdateStrategy()).equals(UpdateConstants.UpdateStrategyEnum.NO_ACTION));
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setId(i + 1);
            }
            updateListVo.statics = list;
        }

        return updateListVo;
    }
}
