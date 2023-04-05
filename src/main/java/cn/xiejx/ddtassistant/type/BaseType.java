package cn.xiejx.ddtassistant.type;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class BaseType implements Serializable {
    private static final long serialVersionUID = 1162668435055453032L;

    private RunningStatus runningStatus;
    private DmDdt dm;
    private Thread thread;

    public void startThread(Runnable runnable) {
        this.thread = new Thread(runnable);
        this.thread.start();
    }

    public BaseType() {
    }

    public BaseType(DmDdt dm) {
        init(dm);
    }

    public void init(DmDdt dm) {
        this.dm = dm;
        runningStatus = RunningStatus.NOT_RUNNING;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseType> T createInstance(Integer hwnd, Class<T> clazz, Boolean bind) {
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
                t.getDm().bind();
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
        baseType.forceStop();
        baseType.getDm().unbind();
    }

    public static <T extends BaseType> BaseType remove(Integer hwnd, Class<T> clazz) {
        String key = clazz.getSimpleName() + "_" + hwnd;
        BaseType baseType = GlobalVariable.BASE_TYPE_MAP.remove(key);
        if (baseType == null) {
            return null;
        }
        baseType.setToNotRunningStatus();
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

    public boolean isRunning() {
        return RunningStatus.RUNNING.equals(this.runningStatus);
    }

    public void setToRunningStatus() {
        this.runningStatus = RunningStatus.RUNNING;
    }

    public void setToNotRunningStatus() {
        this.runningStatus = RunningStatus.NOT_RUNNING;
    }

    @SuppressWarnings("deprecation")
    public boolean suspend() {
        if (!RunningStatus.RUNNING.equals(runningStatus)) {
            return false;
        }
        if (thread == null) {
            return false;
        }
        log.info("[{}] 暂停运行 suspend", this.dm.getHwnd());
        this.runningStatus = RunningStatus.PAUSE;
        GlobalVariable.THREAD_POOL.execute(() -> thread.suspend());
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean resume() {
        if (!RunningStatus.PAUSE.equals(runningStatus)) {
            return false;
        }
        if (thread == null) {
            return false;
        }
        log.info("[{}] 恢复运行 resume", this.dm.getHwnd());
        this.runningStatus = RunningStatus.PAUSE;
        GlobalVariable.THREAD_POOL.execute(() -> thread.resume());
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean forceStop() {
        if (RunningStatus.STOP.equals(runningStatus) || RunningStatus.NOT_RUNNING.equals(runningStatus)) {
            return false;
        }
        if (thread == null) {
            return false;
        }
        log.info("[{}] 强制停止 stop", this.dm.getHwnd());
        this.runningStatus = RunningStatus.STOP;
        GlobalVariable.THREAD_POOL.execute(() -> thread.stop());
        return true;
    }

    public DmDdt getDm() {
        return dm;
    }

    public Integer getHwnd() {
        return getDm().getHwnd();
    }

    public enum RunningStatus {
        NOT_RUNNING(0),
        RUNNING(1),
        PAUSE(2),
        STOP(3),
        ;
        private final Integer status;

        RunningStatus(Integer status) {
            this.status = status;
        }

        public Integer getStatus() {
            return status;
        }
    }

}
