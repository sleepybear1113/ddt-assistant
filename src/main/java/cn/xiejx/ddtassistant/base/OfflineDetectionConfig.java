package cn.xiejx.ddtassistant.base;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 02:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OfflineDetectionConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = 4033525747498353766L;

    private Long delay;
    private Boolean emailRemind;

    @Override
    @JSONField(serialize = false)
    public String getFileName() {
        return "offlineDetection.json";
    }

    @Override
    public BaseConfig defaultConfig() {
        OfflineDetectionConfig offlineDetectionConfig = new OfflineDetectionConfig();
        offlineDetectionConfig.setDelay(null);
        offlineDetectionConfig.setEmailRemind(false);
        return offlineDetectionConfig;
    }
}
