package cn.sleepybear.ddtassistant.base;

import cn.sleepybear.ddtassistant.dm.DmConstants;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/22 21:05
 */
@Data
public class BindWindowConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 4647138698971532422L;

    private String displayType;
    private String mouseType;
    private String keypadType;

    public DmConstants.BindWindowDisplayTypeEnum getDisplayTypeEnum() {
        return DmConstants.BindWindowDisplayTypeEnum.getType(displayType);
    }

    public String getDisplayType() {
        DmConstants.BindWindowDisplayTypeEnum displayTypeEnum = getDisplayTypeEnum();
        return displayTypeEnum == null ? DmConstants.BindWindowDisplayTypeEnum.DX2.getType() : displayTypeEnum.getType();
    }

    public DmConstants.BindWindowMouseTypeEnum getMouseTypeEnum() {
        return DmConstants.BindWindowMouseTypeEnum.getType(mouseType);
    }

    public String getMouseType() {
        DmConstants.BindWindowMouseTypeEnum mouseTypeEnum = getMouseTypeEnum();
        return mouseTypeEnum == null ? DmConstants.BindWindowMouseTypeEnum.WINDOWS.getType() : mouseTypeEnum.getType();
    }

    public DmConstants.BindWindowKeypadTypeEnum getKeypadTypeEnum() {
        return DmConstants.BindWindowKeypadTypeEnum.getType(keypadType);
    }

    public String getKeypadType() {
        DmConstants.BindWindowKeypadTypeEnum keypadTypeEnum = getKeypadTypeEnum();
        return keypadTypeEnum == null ? DmConstants.BindWindowKeypadTypeEnum.WINDOWS.getType() : keypadTypeEnum.getType();
    }

    public static BindWindowConfig defaultConfig() {
        BindWindowConfig bindWindowConfig = new BindWindowConfig();
        bindWindowConfig.setDisplayType(bindWindowConfig.getDisplayType());
        bindWindowConfig.setMouseType(bindWindowConfig.getMouseType());
        bindWindowConfig.setKeypadType(bindWindowConfig.getKeypadType());
        return bindWindowConfig;
    }
}
