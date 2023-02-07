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
public class UpdateConfig implements Serializable {
    private static final long serialVersionUID = 987539769954299743L;

    private String url;

    /**
     * 开启自动检测更新
     */
    private Boolean enableAutoCheckUpdate;

    /**
     * 开启自动更新文件
     */
    private Boolean enableAutoUpdate;

    public static UpdateConfig defaultConfig() {
        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setUrl("http://yoga:19876/D%3A/XJXCode/Raw/ddt-assistant-static/version.json");
        updateConfig.setEnableAutoCheckUpdate(true);
        updateConfig.setEnableAutoUpdate(false);
        return updateConfig;
    }
}
