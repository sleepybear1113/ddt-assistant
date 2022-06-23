package cn.xiejx.ddtassistant.dm;

import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.utils.Util;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author sleepybear
 */
@Slf4j
public class Dm {

    public static final String PROGRAM_ID_DM = "dm.dmsoft";

    private final Dispatch dmDispatch;

    private int count = 0;

    public Dm() {
        ComThread.InitSTA();
        ComThread.doCoInitialize(0);
        ActiveXComponent dmComponent = new ActiveXComponent(PROGRAM_ID_DM);
        dmDispatch = dmComponent.getObject();
    }

    public int getCount() {
        return count;
    }

    public void clearCount() {
        this.count = 0;
    }

    public String getVersion() {
        Variant variant = Dispatch.call(dmDispatch, "Ver");
        variant.safeRelease();
        return variant.getString();
    }

    public int getId() {
        Variant variant = Dispatch.call(dmDispatch, "GetID");
        variant.safeRelease();
        return variant.getInt();
    }

    public void bindWindow(int hwnd, String display, String mouse, String keypad, int mode) {
        invoke("BindWindow", hwnd, display, mouse, keypad, mode).safeRelease();
    }

    public int getWindow(int hwnd, int flag) {
        Variant variant = invoke("GetWindow", hwnd, flag);
        int i = variant.getInt();
        variant.safeRelease();
        return i;
    }

    public String getWindowClass(int hwnd) {
        Variant variant = invoke("GetWindowClass", hwnd);
        String string = variant.getString();
        variant.safeRelease();
        return string;
    }

    public String getWindowTitle(int hwnd) {
        Variant variant = invoke("GetWindowTitle", hwnd);
        String string = variant.getString();
        variant.safeRelease();
        return string;
    }

    public int setWindowState(int hwnd, int flag) {
        Variant variant = invoke("SetWindowState", hwnd, flag);
        int i = variant.getInt();
        variant.safeRelease();
        return i;
    }

    public int[] getWindowRect(int hwnd) {
        Variant x1 = new Variant(0, true);
        Variant y1 = new Variant(0, true);
        Variant x2 = new Variant(0, true);
        Variant y2 = new Variant(0, true);
        Variant variant = invoke("GetWindowRect", hwnd, x1, y1, x2, y2);
        variant.safeRelease();
        int[] ints = {x1.getInt(), y1.getInt(), x2.getInt(), y2.getInt()};
        x1.safeRelease();
        y1.safeRelease();
        x2.safeRelease();
        y2.safeRelease();
        return ints;
    }

    public int[] enumWindow(int parent, String title, String className, int filter) {
        Variant variant = invoke("EnumWindow", parent, title, className, filter);
        String hwndStrings = variant.getString();
        variant.safeRelease();
        if (hwndStrings == null || hwndStrings.length() == 0) {
            return null;
        }
        String[] split = hwndStrings.split(",");
        int[] hwnds = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            hwnds[i] = Integer.parseInt(split[i]);
        }
        return hwnds;
    }

    public void bindWindow(Integer hwnd) {
        bindWindow(hwnd, "dx2", "windows", "windows", 0);
    }

    public void bindWindow(Integer hwnd, UserConfig userConfig) {
        bindWindow(hwnd, "dx2", userConfig.getMouseMode(), userConfig.getKeyPadMode(), 0);
    }

    public void unBindWindow() {
        invoke("UnBindWindow").safeRelease();
    }

    public int[] getCursorPos() {
        Variant x = new Variant(0, true);
        Variant y = new Variant(0, true);
        invoke("GetCursorPos", x, y).safeRelease();
        int xx = x.getInt();
        int yy = y.getInt();
        x.safeRelease();
        y.safeRelease();
        return new int[]{xx, yy};
    }

    public int getPointWindowHwnd() {
        Variant variant = invoke("GetMousePointWindow");
        int i = variant.getInt();
        variant.safeRelease();
        return i;
    }

    public void captureBmp(int x1, int y1, int x2, int y2, String path) {
        capture(x1, y1, x2, y2, path);
    }

    public void capture(int x1, int y1, int x2, int y2, String path) {
        if (x2 <= x1 || y2 <= y1) {
            log.error("截图失败：坐标越界！({}, {}), ({}, {})", x1, y1, x2, y2);
            return;
        }
        Variant variant = invoke("Capture", x1, y1, x2, y2, path);
        variant.safeRelease();
    }

    public void capturePng(int x1, int y1, int x2, int y2, String path) {
        if (x2 <= x1 || y2 <= y1) {
            log.error("截图失败：坐标越界！({}, {}), ({}, {})", x1, y1, x2, y2);
            return;
        }
        invoke("CapturePng", x1, y1, x2, y2, path).safeRelease();
    }

    public void captureJpg(int x1, int y1, int x2, int y2, String path) {
        if (x2 <= x1 || y2 <= y1) {
            log.error("截图失败：坐标越界！({}, {}), ({}, {})", x1, y1, x2, y2);
            return;
        }
        Variant variant = invoke("CaptureJpg", x1, y1, x2, y2, path);
        variant.safeRelease();
    }

    public int[] findPic(int[] xxyy, String templatePath, String deltaColor, double threshold, int i) {
        return findPic(xxyy[0], xxyy[1], xxyy[2], xxyy[3], templatePath, deltaColor, threshold, i);
    }

    public int[] findPic(int x1, int y1, int x2, int y2, String templatePath, String deltaColor, double threshold, int i) {
        if (invalidFindPicParam(x1, y1, x2, y2, templatePath)) {
            return null;
        }

        Variant x = new Variant(0, true);
        Variant y = new Variant(0, true);
        invoke("FindPic", x1, y1, x2, y2, templatePath, deltaColor, threshold, i, x, y).safeRelease();
        int xx = x.getInt();
        int yy = y.getInt();
        x.safeRelease();
        y.safeRelease();
        return new int[]{xx, yy};
    }

    public React findPicS(int x1, int y1, int x2, int y2, String templatePath, String deltaColor, double threshold, int i) {
        if (invalidFindPicParam(x1, y1, x2, y2, templatePath)) {
            return null;
        }
        Variant x = new Variant(0, true);
        Variant y = new Variant(0, true);
        Variant variant = invoke("FindPicS", x1, y1, x2, y2, templatePath, deltaColor, threshold, i, x, y);
        React react = new React(x.getInt(), y.getInt(), variant.getString());
        variant.safeRelease();
        x.safeRelease();
        y.safeRelease();
        return react;
    }

    public int imageToBmp(String fromPicName, String toBmpName) {
        Variant variant = invoke("ImageToBmp", fromPicName, toBmpName);
        int i = variant.getInt();
        variant.safeRelease();
        return i;
    }

    private boolean invalidFindPicParam(int x1, int y1, int x2, int y2, String templatePath) {
        return !validFindPicParam(x1, y1, x2, y2, templatePath);
    }

    private boolean validFindPicParam(int x1, int y1, int x2, int y2, String templatePath) {
        if (StringUtils.isBlank(templatePath)) {
            log.error("找图失败：没有样图！");
            return false;
        }
        if (x2 <= x1 || y2 <= y1) {
            log.error("找图失败：坐标越界！({}, {}), ({}, {})", x1, y1, x2, y2);
            return false;
        }
        String[] paths = templatePath.split("\\|");
        for (String path : paths) {
            if (!path.endsWith(".bmp")) {
                log.error("找图失败：样图需要为 .bmp 格式！");
                return false;
            }
            if (!new File(path).exists()) {
                log.warn("找不到文件 {}", templatePath);
                return false;
            }
        }

        return true;
    }

    public void leftClick(int[] xy) {
        leftClick(xy[0], xy[1]);
    }

    public void leftClick(int[] xy, long delay) {
        leftClick(xy[0], xy[1], delay);
    }

    public void leftClick(int x, int y) {
        leftClick(x, y, 0);
    }

    public void leftClick(int x, int y, long delay) {
        moveTo(x, y);
        Util.sleep(delay);
        leftClick();
    }

    public void leftClick() {
        invoke("LeftClick");
    }

    public void leftDoubleClick(int[] xy) {
        leftDoubleClick(xy[0], xy[1]);
    }

    public void leftDoubleClick(int[] xy, long delay) {
        leftDoubleClick(xy[0], xy[1], delay);
    }

    public void leftDoubleClick(int x, int y) {
        leftDoubleClick(x, y, 0);
    }

    public void leftDoubleClick(int x, int y, long delay) {
        moveTo(x, y);
        Util.sleep(delay);
        leftDoubleClick();
    }

    public void leftDoubleClick() {
        invoke("LeftDoubleClick").safeRelease();
    }

    public void moveTo(int x, int y) {
        invoke("MoveTo", x, y).safeRelease();
    }

    public String ocr(int x1, int y1, int x2, int y2, String colorFormat, double threshold) {
        if (colorFormat == null) {
            log.error("ocr 失败：没有 colorFormat！");
            return null;
        }
        if (x2 <= x1 || y2 <= y1) {
            log.error("找图失败：坐标越界！({}, {}), ({}, {})", x1, y1, x2, y2);
            return null;
        }
        Variant variant = invoke("OCR", x1, y1, x2, y2, colorFormat, threshold);
        String string = variant.getString();
        variant.safeRelease();
        return string;
    }

    public void keyPressChar(String key) {
        Variant variant = invoke("KeyPressChar", key);
        variant.safeRelease();
    }

    public void pressKeyChars(String[] keys) {
        pressKeyChars(keys, null);
    }

    public void pressKeyChars(String[] keys, Long delay) {
        for (String key : keys) {
            keyPressChar(key);
            Util.sleep(delay);
        }
    }

    public Variant invoke(String method, Object... params) {
        count++;
        return Dispatch.call(dmDispatch, method, params);
    }

    public Variant invoke(String method, String... params) {
        if (params == null || params.length == 0) {
            return Dispatch.call(dmDispatch, method);
        }
        Object[] objects = new Object[params.length];
        System.arraycopy(params, 0, objects, 0, params.length);
        return invoke(method, objects);
    }
}
