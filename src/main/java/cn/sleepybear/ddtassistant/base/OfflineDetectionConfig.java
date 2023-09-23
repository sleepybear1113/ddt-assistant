package cn.sleepybear.ddtassistant.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 4033525747498353766L;

    public static final long MIN_DELAY = 5000;

    private Long delay;
    private Boolean emailRemind;

    private Boolean disconnect;
    private Boolean tokenExpired;
    private Boolean offsite;
    private Boolean leaveGame;
    private Boolean whiteScreen;

    public void validMinDelay() {
        if (delay != null && delay < MIN_DELAY) {
            delay = MIN_DELAY;
        }
    }

    @Override
    @JsonIgnore
    public String getFileName() {
        return "掉线检测.json";
    }

    @Override
    public BaseConfig defaultConfig() {
        OfflineDetectionConfig offlineDetectionConfig = new OfflineDetectionConfig();
        offlineDetectionConfig.setDelay(null);
        offlineDetectionConfig.setEmailRemind(false);
        return offlineDetectionConfig;
    }
}
