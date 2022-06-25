package cn.xiejx.ddtassistant.dm;

import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.utils.KeyPadUtil;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author sleepybear
 */
@Slf4j
public class DmDdt extends Dm {
    public static final String BMP_SUFFIX = ".bmp";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String PNG_SUFFIX = ".png";
    public static final String DDT_FLASH_CLASS_NAME = "MacromediaFlashPlayerActiveX";
    private final Integer hwnd;
    private boolean bind;

    private int count = 0;

    /**
     * 游戏的全屏区域
     */
    public final static int[] GAME_FULL_REACT = {0, 0, 1000, 600};

    public Integer getHwnd() {
        return hwnd;
    }

    private DmDdt(Integer hwnd) {
        super();
        this.hwnd = hwnd;
    }

    @Override
    public void renewInstance() {
        release();
        newInstance();
        if (bind) {
            bind();
        }
    }

    public static DmDdt createInstance(Integer hwnd) {
        if (hwnd == null) {
            return new DmDdt(hwnd);
        }
        DmDdt dmDdt = GlobalVariable.DM_DDT_MAP.get(hwnd);
        if (dmDdt == null) {
            dmDdt = new DmDdt(hwnd);
        }
        return dmDdt;
    }

    @Override
    public Variant invoke(String method, Object... params) {
        try {
            count++;
            return super.invoke(method, params);
        } catch (ComFailException e) {
            log.warn("句柄[{}]绑定失败，已解绑，错误日志如下：", hwnd);
            clearCount();
            unbind();
            throw e;
        }
    }

    public int getCount() {
        return count;
    }

    public void clearCount() {
        this.count = 0;
    }

    public void bind() {
        if (bind) {
            return;
        }
        bind = true;
        super.bindWindow(this.hwnd);
        GlobalVariable.DM_DDT_MAP.putIfAbsent(this.hwnd, this);
    }

    public void bind(UserConfig userConfig) {
        if (bind) {
            return;
        }
        bind = true;
        super.bindWindow(this.hwnd, userConfig.getMouseMode(), userConfig.getKeyPadMode());
        GlobalVariable.DM_DDT_MAP.putIfAbsent(this.hwnd, this);
    }

    public void unbind() {
        bind = false;
        super.unBindWindow();
        GlobalVariable.DM_DDT_MAP.remove(this.hwnd);
    }

    public boolean isBind() {
        return bind;
    }

    public static boolean isBind(Integer hwnd) {
        DmDdt dmDdt = GlobalVariable.DM_DDT_MAP.get(hwnd);
        if (dmDdt == null) {
            return false;
        }
        return dmDdt.isBind();
    }

    @Override
    public void keyPressChar(String key) {
        SettingConfig settingConfig = SpringContextUtil.getBean(SettingConfig.class);
        if (settingConfig == null) {
            super.keyPressChar(key);
            return;
        }
        SettingConfig.KeyPressWayEnum keyPadPressWayEnum = settingConfig.getKeyPadPressWayEnum();
        if (SettingConfig.KeyPressWayEnum.DM.equals(keyPadPressWayEnum)) {
            super.keyPressChar(key);
        } else {
            KeyPadUtil.press(key);
        }
    }

    public String getWindowClass() {
        return this.getWindowClass(this.hwnd);
    }

    public String getWindowClass(Integer hwnd) {
        return super.getWindowClass(hwnd);
    }

    public boolean isWindowClassMatchClass(String classname) {
        return isWindowClassMatchClass(this.hwnd, classname);
    }

    public boolean isWindowClassMatchClass(Integer hwnd, String classname) {
        String windowClass = getWindowClass(this.hwnd);
        if (Objects.equals(windowClass, classname)) {
            return true;
        }

        if (windowClass == null || windowClass.length() == 0) {
            return false;
        }
        return windowClass.contains(classname);
    }

    public boolean isDdtWindow(Integer hwnd) {
        if (!isWindowClassFlashPlayerActiveX(hwnd)) {
            return false;
        }

        int[] windowRect = this.getWindowRect(hwnd);
        return Math.abs(windowRect[0] - windowRect[2]) == 1000 && Math.abs(windowRect[1] - windowRect[3]) == 600;
    }

    public boolean isDdtWindow() {
        return this.isDdtWindow(this.hwnd);
    }

    public boolean isWindowClassFlashPlayerActiveX() {
        String windowClass = this.getWindowClass();
        return DDT_FLASH_CLASS_NAME.equalsIgnoreCase(windowClass);
    }

    public boolean isWindowClassFlashPlayerActiveX(Integer hwnd) {
        String windowClass = this.getWindowClass(hwnd);
        return DDT_FLASH_CLASS_NAME.equalsIgnoreCase(windowClass);
    }

    public List<Integer> enumDdtWindowHwnd() {
        int[] hwnds = super.enumWindow(0, "", DDT_FLASH_CLASS_NAME, 2);
        if (hwnds == null) {
            return null;
        }

        List<Integer> res = new ArrayList<>();
        for (int hwnd : hwnds) {
            if (isDdtWindow(hwnd)) {
                res.add(hwnd);
            }
        }

        return res;
    }

    public void capturePicByRegion(String path, int[] region) {
        if (path == null || path.length() == 0) {
            log.warn("图片路径未指定");
            return;
        }
        Util.ensureParentDir(path);
        if (path.endsWith(BMP_SUFFIX)) {
            captureBmp(region[0], region[1], region[2], region[3], path);
        } else if (path.endsWith(PNG_SUFFIX)) {
            capturePng(region[0], region[1], region[2], region[3], path);
        } else if (path.endsWith(JPG_SUFFIX)) {
            captureJpg(region[0], region[1], region[2], region[3], path);
        } else {
            capturePng(region[0], region[1], region[2], region[3], path + ".jpg");
        }
    }

    public void captureFullGamePic(String path) {
        capturePicByRegion(path, GAME_FULL_REACT);
    }
}
