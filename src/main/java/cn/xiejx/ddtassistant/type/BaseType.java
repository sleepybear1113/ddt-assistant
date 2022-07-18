package cn.xiejx.ddtassistant.type;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/07/02 19:35
 */
public class BaseType implements Serializable {
    private static final long serialVersionUID = 1162668435055453032L;

    private boolean running;

    private DmDdt dm;

    private static final List<BaseType> BASE_TYPE_LIST = new ArrayList<>();

    public BaseType() {
    }

    public BaseType(DmDdt dm) {
        init(dm);
    }

    public void init(DmDdt dm) {
        this.dm = dm;
        this.running = false;
        BASE_TYPE_LIST.add(this);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseType> T createInstance(DmDdt dm, Class<T> clazz) {
        String key = clazz.getSimpleName() + "_" + dm.getHwnd();
        T t = (T) GlobalVariable.BASE_TYPE_MAP.get(key);
        if (t != null) {
            return t;
        }

        try {
            t = clazz.getDeclaredConstructor().newInstance();
            t.init(dm);
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

    public static <T extends BaseType> boolean isRunning(Integer hwnd, Class<T> clazz) {
        String key = clazz.getSimpleName() + "_" + hwnd;
        BaseType baseType = GlobalVariable.BASE_TYPE_MAP.get(key);
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

    public static void removeAllTypes(Integer hwnd) {
        removeByType(hwnd, null);
    }

    public static <T extends BaseType> void removeByType(Integer hwnd, Class<T> clazz) {
        for (BaseType baseType : BASE_TYPE_LIST) {
            if (clazz != null && !baseType.getClass().equals(clazz)) {
                continue;
            }
            if (Objects.equals(baseType.getDm().getHwnd(), hwnd)) {
                baseType.remove();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public DmDdt getDm() {
        return dm;
    }

    public Integer getHwnd() {
        return getDm().getHwnd();
    }
}
