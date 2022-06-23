package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.OfflineDetectionConfig;
import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import javafx.util.Pair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 10:29
 */
@Component
@Slf4j
public class MonitorLogic {

    public static final Cacher<String, Long> TIME_CACHER = new CacherBuilder<String, Long>().scheduleName("timeCacher").delay(30, TimeUnit.SECONDS).build();
    public static final Cacher<String, Pair<Long, DmDdt>> FLOP_BONUS_CACHER = new CacherBuilder<String, Pair<Long, DmDdt>>().scheduleName("flopBonusCacher").delay(30, TimeUnit.SECONDS).build();
    public static final String CAPTCHA_FOUND_KEY = "key_captcha_found";
    public static final String FLOP_BONUS_FOUND_KEY = "key_flop_bonus_found";
    public static final long CAPTCHA_DELAY = 5000L;

    @Resource
    private CaptchaLogic captchaLogic;
    @Resource
    private EmailLogic emailLogic;
    @Resource
    private OfflineDetectionLogic offlineDetectionLogic;

    @Resource
    private UserConfig userConfig;
    @Resource
    private OfflineDetectionConfig offlineDetectionConfig;

    @Resource
    private DmDdt defaultDm;

    private Set<Integer> offlineHwndSet;
    private Set<Integer> offsiteHwndSet;

    @PostConstruct
    public void init() {
        startMonitorActionLoopThread();
        offlineHwndSet = new HashSet<>();
        offsiteHwndSet = new HashSet<>();
    }

    /**
     * 开始循环监控
     */
    public void startMonitorActionLoopThread() {
        log.info("监控线程启动！");
        GlobalVariable.THREAD_POOL.execute(this::monitorKeyPressActionLoop);
        GlobalVariable.THREAD_POOL.execute(this::monitorActionLoop);
    }

    /**
     * 开始循环监控
     */
    public void monitorKeyPressActionLoop() {
        while (true) {
            Util.sleep(100L);
            monitorCaptchaAppearAction();
            monitorFlopBonusAction();
        }
    }

    public void monitorActionLoop() {
        MonitorVariable newCaptchaVariable = new MonitorVariable();
        MonitorVariable monitorOfflineDetectVariable = new MonitorVariable();
        while (true) {
            Util.sleep(100L);
            monitorNewCaptchaBind(userConfig.getDetectNewWindowInterval(), newCaptchaVariable);
            monitorOfflineDetect(offlineDetectionConfig.getDelay(), monitorOfflineDetectVariable);
        }
    }

    /**
     * 验证码按键监控
     */
    public void monitorCaptchaAppearAction() {
        String keyPress1 = userConfig.getKeyPressAfterCaptchaShow();
        if (StringUtils.isBlank(keyPress1)) {
            return;
        }
        if (TIME_CACHER.get(CAPTCHA_FOUND_KEY) != null) {
            log.info("[监控线程] 发现验证码");
            if (StringUtils.isBlank(keyPress1)) {
                log.info("[监控线程] 按下按键 {}", keyPress1);
                defaultDm.keyPressChar(keyPress1);
            }

            while (true) {
                Util.sleep(100L);

                if (TIME_CACHER.get(CAPTCHA_FOUND_KEY) != null) {
                    continue;
                }
                log.info("[监控线程] 验证码消失");
                String keyPress2 = userConfig.getKeyPressAfterCaptchaDisappear();
                if (StringUtils.isBlank(keyPress2)) {
                    log.info("[监控线程] 按下按键 {}", keyPress2);
                    defaultDm.keyPressChar(keyPress1);
                }
                break;
            }
        }
    }

    /**
     * 副本翻牌监控
     */
    public void monitorFlopBonusAction() {
        String keyPress1 = userConfig.getKeyPressAfterPveFlopBonus();
        if (StringUtils.isBlank(keyPress1)) {
            return;
        }
        // 出现大翻牌等待按下按键的时间
        Pair<Long, DmDdt> timeDmPair = FLOP_BONUS_CACHER.get(FLOP_BONUS_FOUND_KEY);
        if (timeDmPair == null) {
            return;
        }
        DmDdt dmDdt = timeDmPair.getValue();

        Long t = timeDmPair.getKey();
        log.info("[监控线程] 发现副本大翻牌！");
        long firstFoundTime = System.currentTimeMillis();
        Long firstDisappear = null;

        while (true) {
            Util.sleep(100L);
            if (System.currentTimeMillis() - firstFoundTime < t) {
                continue;
            }
            log.info("[监控线程] 按下按键 {}", keyPress1);
            defaultDm.keyPressChar(keyPress1);
            if (Boolean.TRUE.equals(userConfig.getPveFlopBonusCapture())) {
                log.info("截图游戏，使用线程 [{}]", dmDdt.getHwnd());
                String dir = Constants.FLOP_BONUS_DIR + Util.getTimeString(Util.TIME_YMD_FORMAT).replace("_", "") + "/";
                File file = new File(dir);
                if (!file.exists() || !file.isDirectory()) {
                    boolean mkdirs = file.mkdirs();
                }
                dmDdt.captureFullGamePic(dir + Util.getTimeString(Util.TIME_HMS_FORMAT).replace("_", ""));
            }
            break;
        }

        // 大翻牌消失后的按键
        Long pveFlopBonusDisappearDelay = userConfig.getPveFlopBonusDisappearDelay();
        String keyPress2 = userConfig.getKeyPressAfterPveFlopBonusDisappear();
        if (pveFlopBonusDisappearDelay != null && !StringUtils.isBlank(keyPress2)) {
            while (true) {
                Util.sleep(100L);
                if (FLOP_BONUS_CACHER.get(FLOP_BONUS_FOUND_KEY) != null) {
                    continue;
                } else {
                    if (firstDisappear == null) {
                        log.info("[监控线程] 副本大翻牌结束！");
                        firstDisappear = System.currentTimeMillis();
                    }
                }
                if (System.currentTimeMillis() - firstDisappear < pveFlopBonusDisappearDelay) {
                    continue;
                }

                defaultDm.keyPressChar(keyPress2);
                log.info("[监控线程] 按下按键 {}", keyPress2);
                break;
            }
        }
    }

    /**
     * 副本验证码绑定监控
     *
     * @param interval interval
     */
    public void monitorNewCaptchaBind(Long interval, MonitorVariable monitorVariable) {
        if (!monitorVariable.refreshTime(interval)) {
            return;
        }
        log.info("monitorNewCaptchaBind");
        GlobalVariable.THREAD_POOL.execute(() -> {
            try {
                captchaLogic.bindAll();
            } catch (FrontException e) {
                log.info("{}", e.getMessage());
            }
            log.info("[监控线程] 等待 {} 毫秒后检测新增窗口", interval);
            monitorVariable.finish();
        });
    }

    private void monitorOfflineDetect(Long interval, MonitorVariable monitorVariable) {
        if (!monitorVariable.refreshTime(interval)) {
            return;
        }
        log.info("monitorOfflineDetect");

        GlobalVariable.THREAD_POOL.execute(() -> {
            int offlineHwndSetBeforeSize = offlineHwndSet.size();
            int offsiteHwndSetBeforeSize = offsiteHwndSet.size();
            List<Integer> offlineAlerts = offlineDetectionLogic.getOfflineAlerts();
            List<Integer> offsiteAlerts = offlineDetectionLogic.getOffsiteAlerts();
            offlineHwndSet.addAll(offlineAlerts);
            offsiteHwndSet.addAll(offsiteAlerts);
            int offlineHwndSetAfterSize = offlineHwndSet.size();
            int offsiteHwndSetAfterSize = offsiteHwndSet.size();
            int offlineHwndSetSub = offlineHwndSetAfterSize - offlineHwndSetBeforeSize;
            int offsiteHwndSetSub = offsiteHwndSetAfterSize - offsiteHwndSetBeforeSize;
            int totalSub = offlineHwndSetSub + offsiteHwndSetSub;
            if (totalSub > 0) {
                try {
                    log.warn("掉线数量：{}, 异地数量：{}", offlineHwndSetSub, offsiteHwndSetSub);
                    if (Boolean.TRUE.equals(offlineDetectionConfig.getEmailRemind())) {
                        log.warn("发送掉线邮件");
                        emailLogic.sendOfflineRemindEmail(offlineHwndSetSub, offsiteHwndSetSub);
                    }
                } catch (Exception e) {
                    log.info(e.getMessage(), e);
                }
            }
            monitorVariable.finish();
        });
    }

    @Data
    static class MonitorVariable {
        long lastTime = 0;
        boolean running = false;

        public boolean refreshTime(Long interval) {
            if (interval == null || interval <= 0) {
                return false;
            }
            if (running) {
                return false;
            }
            if (System.currentTimeMillis() - lastTime < interval) {
                return false;
            }
            running = true;
            lastTime = System.currentTimeMillis();
            return true;
        }

        public void finish() {
            running = false;
        }
    }
}
