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
}
