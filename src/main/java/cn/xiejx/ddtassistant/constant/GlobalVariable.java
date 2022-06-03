package cn.xiejx.ddtassistant.constant;

import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.auction.Auction;
import cn.xiejx.ddtassistant.type.Captcha;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class GlobalVariable {
    public static final Map<Integer, DmDdt> DM_DDT_MAP = new ConcurrentHashMap<>();
    public static final Map<Integer, Captcha> CAPTCHA_MAP = new ConcurrentHashMap<>();
    public static final Map<Integer, Auction> AUCTION_MAP = new ConcurrentHashMap<>();

    public static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(10, 60, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), r -> {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler((t1, e) -> log.error(e.getMessage(), e));
        return t;
    }, new ThreadPoolExecutor.DiscardPolicy());
}
