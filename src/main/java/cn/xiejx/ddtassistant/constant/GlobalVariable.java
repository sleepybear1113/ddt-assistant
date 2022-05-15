package cn.xiejx.ddtassistant.constant;

import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.Captcha;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author sleepybear
 */
public class GlobalVariable {
    public static final Map<Integer, DmDdt> DM_DDT_MAP = new ConcurrentHashMap<>();
    public static final Map<Integer, Captcha> CAPTCHA_MAP = new ConcurrentHashMap<>();

    public static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(10, 30, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
}
