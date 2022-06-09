package cn.xiejx.ddtassistant.base;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.utils.Util;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/09 19:10
 */
@Data
public class SettingConfig implements Serializable {

    private static final long serialVersionUID = 6838662886328568339L;
    public static final String PATH = Constants.CONFIG_DIR + "setting.json";

    private String keyPadPressWay;

    public void update(SettingConfig settingConfig) {
        BeanUtils.copyProperties(settingConfig, this);
    }

    public static SettingConfig load() {
        SettingConfig defaultConfig = SettingConfig.defaultConfig();
        if (!new File(PATH).exists()) {
            return defaultConfig;
        }
        String s = Util.readFile(PATH);
        if (s == null || s.length() == 0) {
            return defaultConfig;
        }

        try {
            return JSON.parseObject(s, SettingConfig.class);
        } catch (Exception e) {
            return defaultConfig;
        }
    }

    public void save() {
        String s = JSON.toJSONString(this);
        Util.writeFile(s, PATH);
    }

    private static SettingConfig defaultConfig() {
        SettingConfig settingConfig = new SettingConfig();
        settingConfig.setKeyPadPressWay("dm");
        return settingConfig;
    }

    @JSONField(serialize = false)
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
