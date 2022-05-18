package cn.xiejx.ddtassistant.dm;

import cn.xiejx.ddtassistant.config.UserConfig;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
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

    public Integer getHwnd() {
        return hwnd;
    }

    private DmDdt(Integer hwnd) {
        super();
        this.hwnd = hwnd;
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
        super.bindWindow(this.hwnd, userConfig);
        GlobalVariable.DM_DDT_MAP.putIfAbsent(this.hwnd, this);
    }

    public void unbind() {
        bind = false;
        super.unBindWindow();
        GlobalVariable.DM_DDT_MAP.remove(this.hwnd);
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
}
