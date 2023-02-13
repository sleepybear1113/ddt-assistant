package cn.xiejx.ddtassistant.dm;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
public class Dm {
    private static final Logger log = LoggerFactory.getLogger(Dm.class);

    public static final String PROGRAM_ID_DM = "dm.dmsoft";
    public static final String DEFAULT_MOUSE_MODE = "windows";
    public static final String DEFAULT_KEY_PAD_MODE = "windows";

    private String programId = PROGRAM_ID_DM;
    private Dispatch dmDispatch;

    public Dm() {
        newInstance();
    }

    public Dm(String programId) {
        this.programId = programId;
        newInstance();
    }

    public void newInstance(String programId) {
        this.programId = programId;
        ComThread.InitSTA();
        ActiveXComponent dmComponent = new ActiveXComponent(this.programId);
        dmDispatch = dmComponent.getObject();
    }

    public void newInstance() {
        newInstance(this.programId);
    }

    public void release() {
        ComThread.Release();
    }

    public void renewInstance() {
        release();
        newInstance();
    }

    public String getVersion() {
        Variant variant = invoke("Ver");
        String ver = variant.getString();
        variant.safeRelease();
        return ver;
    }

    public int getId() {
        Variant variant = invoke("GetID");
        int id = variant.getInt();
        variant.safeRelease();
        return id;
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

    public int[] enumWindow(int parent, String title, String className, DmConstants.EnumWindowFilter filter) {
        return enumWindow(parent, title, className, filter.getType());
    }

    public void bindWindow(Integer hwnd) {
        bindWindow(hwnd, "dx2", "windows", "windows", 0);
    }

    public void bindWindow(Integer hwnd, String mouse, String keypad) {
        bindWindow(hwnd, "dx2", mouse, keypad, 0);
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

    public void captureJpg(int x1, int y1, int x2, int y2, String path, int quality) {
        if (x2 <= x1 || y2 <= y1) {
            log.error("截图失败：坐标越界！({}, {}), ({}, {})", x1, y1, x2, y2);
            return;
        }
        Variant variant = invoke("CaptureJpg", x1, y1, x2, y2, path, quality);
        variant.safeRelease();
    }

    public int[] findPic(int[] xy, String templatePath, String deltaColor, double threshold, DmConstants.SearchWay searchWay) {
        return findPic(xy[0], xy[1], xy[2], xy[3], templatePath, deltaColor, threshold, searchWay.getType());
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

    /**
     * 查找指定区域内的图片,位图必须是24位色格式,支持透明色,当图像上下左右4个顶点的颜色一样时,则这个颜色将作为透明色处理.
     * 这个函数可以查找多个图片,并且返回所有找到的图像的坐标.
     *
     * @param xy            数组，{区域的左上X坐标, 区域的左上Y坐标, 区域的右下X坐标, 区域的右下Y坐标}
     * @param templatePaths 字符串列表:图片名,可以是多个图片,比如"test.bmp|test2.bmp|test3.bmp"
     * @param deltaColor    字符串: 颜色色偏比如"203040" 表示RGB的色偏分别是20 30 40 (这里是16进制表示)
     * @param threshold     双精度浮点数:相似度,取值范围0.1-1.0
     * @param searchWay     查找方向, {@link DmConstants.SearchWay}
     * @return List<DmDomains.PicEx>
     */
    public List<DmDomains.PicEx> findPicEx(int[] xy, List<String> templatePaths, String deltaColor, double threshold, DmConstants.SearchWay searchWay) {
        if (isEmpty(templatePaths)) {
            return new ArrayList<>();
        }

        String join = join(templatePaths, "|");
        List<int[]> findPicExes = findPicEx(xy, join, deltaColor, threshold, searchWay);
        if (isEmpty(findPicExes)) {
            return new ArrayList<>();
        }

        List<DmDomains.PicEx> list = new ArrayList<>();
        for (int[] find : findPicExes) {
            DmDomains.PicEx picEx = new DmDomains.PicEx(find[0], find[1], find[2], templatePaths.get(find[0]));
            list.add(picEx);
        }
        return list;
    }

    /**
     * 查找指定区域内的图片,位图必须是24位色格式,支持透明色,当图像上下左右4个顶点的颜色一样时,则这个颜色将作为透明色处理.
     * 这个函数可以查找多个图片,并且返回所有找到的图像的坐标.
     *
     * @param xy           数组，{区域的左上X坐标, 区域的左上Y坐标, 区域的右下X坐标, 区域的右下Y坐标}
     * @param templatePath 字符串:图片名,可以是多个图片,比如"test.bmp|test2.bmp|test3.bmp"
     * @param deltaColor   字符串: 颜色色偏比如"203040" 表示RGB的色偏分别是20 30 40 (这里是16进制表示)
     * @param threshold    双精度浮点数:相似度,取值范围0.1-1.0
     * @param searchWay    查找方向, {@link DmConstants.SearchWay}
     * @return list, [index, 区域的左上X坐标, 区域的左上Y坐标]
     */
    public List<int[]> findPicEx(int[] xy, String templatePath, String deltaColor, double threshold, DmConstants.SearchWay searchWay) {
        String s = findPicEx(xy[0], xy[1], xy[2], xy[3], templatePath, deltaColor, threshold, searchWay.getType());
        List<int[]> list = new ArrayList<>();
        if (isBlank(s)) {
            return list;
        }
        String[] split = s.split("\\|");
        for (String s1 : split) {
            String[] search = s1.split(",");
            int[] add = new int[3];
            add[0] = Integer.parseInt(search[0]);
            add[1] = Integer.parseInt(search[1]);
            add[2] = Integer.parseInt(search[2]);
            list.add(add);
        }
        return list;
    }

    /**
     * 查找指定区域内的图片,位图必须是24位色格式,支持透明色,当图像上下左右4个顶点的颜色一样时,则这个颜色将作为透明色处理.
     * 这个函数可以查找多个图片,并且返回所有找到的图像的坐标.
     *
     * @param x1           整形数:区域的左上X坐标
     * @param y1           整形数:区域的左上Y坐标
     * @param x2           整形数:区域的右下X坐标
     * @param y2           整形数:区域的右下Y坐标
     * @param templatePath 字符串:图片名,可以是多个图片,比如"test.bmp|test2.bmp|test3.bmp"
     * @param deltaColor   字符串: 颜色色偏比如"203040" 表示RGB的色偏分别是20 30 40 (这里是16进制表示)
     * @param threshold    双精度浮点数:相似度,取值范围0.1-1.0
     * @param i            整形数:查找方向,0: 从左到右,从上到下 1: 从左到右,从下到上 2: 从右到左,从上到下 3: 从右到左, 从下到上
     * @return 字符串: 返回的是所有找到的坐标格式如下:"id,x,y|id,x,y..|id,x,y" (图片左上角的坐标)
     */
    public String findPicEx(int x1, int y1, int x2, int y2, String templatePath, String deltaColor, double threshold, int i) {
        if (invalidFindPicParam(x1, y1, x2, y2, templatePath)) {
            return null;
        }
        Variant variant = invoke("FindPicEx", x1, y1, x2, y2, templatePath, deltaColor, threshold, i);
        String string = variant.getString();
        variant.safeRelease();
        return string;
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
        if (isBlank(templatePath)) {
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
        sleep(delay);
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
        sleep(delay);
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
        invoke("KeyPressChar", key).safeRelease();
    }

    public void pressKeyChars(String[] keys) {
        pressKeyChars(keys, null);
    }

    public void pressKeyChars(String[] keys, Long delay) {
        for (String key : keys) {
            keyPressChar(key);
            sleep(delay);
        }
    }

    public Variant invoke(String method, Object... params) {
        return Dispatch.call(dmDispatch, method, params);
    }

    public static void sleep(Long t) {
        try {
            if (t != null && t > 0) {
                TimeUnit.MILLISECONDS.sleep(t);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen = cs == null ? 0 : cs.length();
        if (strLen != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static String join(Iterable<?> iterable, String separator) {
        Iterator<?> iterator = iterable == null ? null : iterable.iterator();
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return Objects.toString(first, "");
            } else {
                StringBuilder buf = new StringBuilder(256);
                if (first != null) {
                    buf.append(first);
                }

                while (iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }
}
