package cn.sleepybear.ddtassistant.type;

import cn.sleepybear.ddtassistant.base.BindWindowConfig;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.exception.MyInterruptException;
import cn.sleepybear.ddtassistant.utils.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/07/02 19:35
 */
@Getter
@Slf4j
public class BaseType implements Serializable {
    @Serial
    private static final long serialVersionUID = 1162668435055453032L;

    private boolean running;
    private boolean pause;

    private DmDdt dm;

    public BaseType() {
    }

    public BaseType(DmDdt dm) {
        init(dm);
    }

    public void init(DmDdt dm) {
        this.dm = dm;
        this.running = false;
        this.pause = false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseType> T createInstance(Integer hwnd, Class<T> clazz, Boolean bind, BindWindowConfig bindWindowConfig) {
        String key = clazz.getSimpleName() + "_" + hwnd;
        T t = (T) GlobalVariable.BASE_TYPE_MAP.get(key);
        if (t != null) {
            return t;
        }

        try {
            t = clazz.getDeclaredConstructor().newInstance();
            DmDdt dmDdt = DmDdt.createInstance(hwnd);
            t.init(dmDdt);
            if (Boolean.TRUE.equals(bind)) {
                bindWindowConfig = bindWindowConfig == null ? new BindWindowConfig() : bindWindowConfig;
                t.getDm().bind(bindWindowConfig.getMouseTypeEnum(), bindWindowConfig.getKeypadTypeEnum());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException ignored) {
        }
        if (t != null) {
            GlobalVariable.BASE_TYPE_MAP.put(key, t);
        }
        return t;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends BaseType> BaseType remove() {
        return remove(getDm().getHwnd(), getClass());
    }

    public <T extends BaseType> void unbindAndRemove() {
        unbindAndRemove(getDm().getHwnd(), getClass());
    }

    public static <T extends BaseType> void unbindAndRemove(Integer hwnd, Class<T> clazz) {
        BaseType baseType = remove(hwnd, clazz);
        if (baseType == null) {
            return;
        }
        baseType.setRunning(false);
        baseType.getDm().unbind();
    }

    public static <T extends BaseType> BaseType remove(Integer hwnd, Class<T> clazz) {
        String key = clazz.getSimpleName() + "_" + hwnd;
        BaseType baseType = GlobalVariable.BASE_TYPE_MAP.remove(key);
        if (baseType == null) {
            return null;
        }
        baseType.setRunning(false);
        return baseType;
    }

    public static <T extends BaseType> BaseType getBaseType(Integer hwnd, Class<T> clazz) {
        String key = clazz.getSimpleName() + "_" + hwnd;
        return GlobalVariable.BASE_TYPE_MAP.get(key);
    }

    public static <T extends BaseType> boolean isRunning(Integer hwnd, Class<T> clazz) {
        BaseType baseType = getBaseType(hwnd, clazz);
        if (baseType == null) {
            return false;
        }

        return baseType.isRunning();
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseType> List<T> getBaseTypes(Class<T> clazz) {
        Set<Map.Entry<String, BaseType>> entries = GlobalVariable.BASE_TYPE_MAP.entrySet();
        List<T> list = new ArrayList<>();
        for (Map.Entry<String, BaseType> entry : entries) {
            String key = entry.getKey();
            if (key.startsWith(clazz.getSimpleName())) {
                list.add((T) entry.getValue());
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseType> List<T> getBaseTypes(Integer hwnd) {
        Set<Map.Entry<String, BaseType>> entries = GlobalVariable.BASE_TYPE_MAP.entrySet();
        List<T> list = new ArrayList<>();
        for (Map.Entry<String, BaseType> entry : entries) {
            String key = entry.getKey();
            if (key.endsWith(String.valueOf(hwnd))) {
                list.add((T) entry.getValue());
            }
        }
        return list;
    }

    public static void removeAllTypes(Integer hwnd) {
        for (BaseType baseType : BaseType.getBaseTypes(hwnd)) {
            baseType.unbindAndRemove();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void stop() {
        log.info("[{}] 停止运行", this.dm.getHwnd());
        this.setRunning(false);
    }

    public void pause() {
        log.info("[{}] 暂停运行", this.dm.getHwnd());
        this.pause = true;
    }

    public void continueRun() {
        log.info("[{}] 继续运行", this.dm.getHwnd());
        this.pause = true;
    }

    public Integer getHwnd() {
        return getDm().getHwnd();
    }

    public void stopOrPause() {
        if (!this.running) {
            throw new MyInterruptException("中止运行");
        }
        while (this.pause) {
            Util.sleep(50L);
        }
    }
}
