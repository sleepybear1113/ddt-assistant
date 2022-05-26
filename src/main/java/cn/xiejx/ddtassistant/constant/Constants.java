package cn.xiejx.ddtassistant.constant;

import cn.xiejx.ddtassistant.dm.Dm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sleepybear
 */
public class Constants {
    public static final Map<Integer, Dm> HWND_DM_MAP = new ConcurrentHashMap<>();

    public static final String RESOURCE_DIR = "img/";

    public static final String PIC_DIR = "pictures/";

    public static final String FLOP_BONUS_DIR = PIC_DIR + "副本翻牌截图/";

    public static final String BMP_SUFFIX = ".bmp";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String PNG_SUFFIX = ".png";
}
