package cn.sleepybear.ddtassistant.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/09 19:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettingConfig extends BaseConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 6838662886328568339L;

    private String keyPadPressWay;

    private BindWindowConfig bindWindowConfig;

    private EmailConfig email;

    private LoginConfig loginConfig;

    private UpdateConfig updateConfig;

    public boolean enableLogin() {
        return loginConfig != null && Boolean.TRUE.equals(loginConfig.getEnableLogin());
    }

    @Override
    @JsonIgnore
    public String getFileName() {
        return "设置信息.json";
    }

    @Override
    public SettingConfig defaultConfig() {
        SettingConfig settingConfig = new SettingConfig();
        settingConfig.setKeyPadPressWay("dm");
        settingConfig.setBindWindowConfig(BindWindowConfig.defaultConfig());
        settingConfig.setEmail(new EmailConfig());
        settingConfig.setLoginConfig(new LoginConfig());
        settingConfig.setUpdateConfig(UpdateConfig.defaultConfig());
        return settingConfig;
    }

    @JsonIgnore
    public KeyPressWayEnum getKeyPadPressWayEnum() {
        return KeyPressWayEnum.getWay(this.keyPadPressWay);
    }

    public void setKeyPadPressWayEnum(String keyPadPressWay) {
        this.keyPadPressWay = KeyPressWayEnum.getWay(keyPadPressWay).getWay();
    }

    public BindWindowConfig getBindWindowConfig() {
        return bindWindowConfig == null ? BindWindowConfig.defaultConfig() : bindWindowConfig;
    }

    @Getter
    public enum KeyPressWayEnum {
        /**
         * 按键枚举
         */
        DM("dm"),
        JAVA_ROBOT("robot"),
        ;
        private final String way;

        KeyPressWayEnum(String way) {
            this.way = way;
        }

        public static KeyPressWayEnum getWay(String way) {
            for (KeyPressWayEnum value : values()) {
                if (value.getWay().equals(way)) {
                    return value;
                }
            }
            return DM;
        }
    }

}