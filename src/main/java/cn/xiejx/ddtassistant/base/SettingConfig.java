package cn.xiejx.ddtassistant.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private static final long serialVersionUID = 6838662886328568339L;

    private String keyPadPressWay;

    private EmailConfig email;

    @Override
    @JsonIgnore
    public String getFileName() {
        return "设置信息.json";
    }

    @Override
    public SettingConfig defaultConfig() {
        SettingConfig settingConfig = new SettingConfig();
        settingConfig.setKeyPadPressWay("dm");
        settingConfig.setEmail(new EmailConfig());
        return settingConfig;
    }

    @JsonIgnore
    public KeyPressWayEnum getKeyPadPressWayEnum() {
        return KeyPressWayEnum.getWay(this.keyPadPressWay);
    }

    public void setKeyPadPressWayEnum(String keyPadPressWay) {
        this.keyPadPressWay = KeyPressWayEnum.getWay(keyPadPressWay).getWay();
    }

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

        public String getWay() {
            return way;
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
