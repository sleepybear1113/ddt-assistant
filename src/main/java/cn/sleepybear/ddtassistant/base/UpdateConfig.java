package cn.sleepybear.ddtassistant.base;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/03 09:23
 */
@Data
public class UpdateConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 987539769954299743L;

    private String url;

    /**
     * 开启自动检测更新
     */
    private Boolean enableAutoCheckUpdate;

    /**
     * 开启自动更新文件
     */
    private Integer updateVersionType;

    public static UpdateConfig defaultConfig() {
        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setUrl("https://gitee.com/sleepybear1113/ddt-assistant-static/raw/master/version.json");
        updateConfig.setEnableAutoCheckUpdate(true);
        updateConfig.setUpdateVersionType(16);
        return updateConfig;
    }
}
