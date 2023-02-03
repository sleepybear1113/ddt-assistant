package cn.xiejx.ddtassistant.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 09:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateConfig  extends BaseConfig implements Serializable {
    private static final long serialVersionUID = 987539769954299743L;

    /**
     * 开启自动检测更新
     */
    private Boolean enableAutoCheckUpdate;

    /**
     * 开启自动更新文件
     */
    private Boolean enableAutoUpdate;

    @Override
    public String getFileName() {
        return "更新设置";
    }

    @Override
    public BaseConfig defaultConfig() {
        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setEnableAutoCheckUpdate(true);
        updateConfig.setEnableAutoUpdate(false);
        return updateConfig;
    }
}
