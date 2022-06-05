package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.config.UserConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.type.Captcha;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import cn.xiejx.ddtassistant.utils.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import cn.xiejx.ddtassistant.vo.BindResultVo;
import cn.xiejx.ddtassistant.vo.StringRet;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Component
@Slf4j
public class CaptchaLogic {
    public static final Cacher<String, Long> TIME_CACHER = new CacherBuilder<String, Long>().scheduleName("timeCacher").delay(30, TimeUnit.SECONDS).build();
    public static final Cacher<String, Pair<Long, DmDdt>> FLOP_BONUS_CACHER = new CacherBuilder<String, Pair<Long, DmDdt>>().scheduleName("flopBonusCacher").delay(30, TimeUnit.SECONDS).build();
    public static final String CAPTCHA_FOUND_KEY = "key_captcha_found";
    public static final String FLOP_BONUS_FOUND_KEY = "key_flop_bonus_found";
    public static final long S = 5000L;
    private static final Random RANDOM = new Random();

    @Resource
    private UserConfig userConfig;
    @Resource
    private DmDdt defaultDm;

    @PostConstruct
    public void init() {
        startMonitorActionLoopThread();
        startMonitorNewCaptchaLoopThread(userConfig);
    }

    public void startMonitorNewCaptchaLoopThread(UserConfig userConfig) {
        GlobalVariable.THREAD_POOL.execute(() -> monitorNewCaptchaLoop(userConfig));
    }

    public void startMonitorActionLoopThread() {
        GlobalVariable.THREAD_POOL.execute(this::monitorActionLoop);
    }

    public TjResponse testCaptcha() {
        TjPredictDto build = TjPredictDto.build(userConfig, "img/captcha-sample.bmp");
        TjResponse response = TjHttpUtil.getTjResponse(build);
        log.info(String.valueOf(response));
        return response;
    }

    public Boolean addNewBind(Long delay) {
        if (!userConfig.validUserInfo()) {
            throw new FrontException("用户名或密码为空");
        }
        if (delay != null && delay > 0) {
            Util.sleep(delay);
        }
        int pointWindowHwnd = defaultDm.getPointWindowHwnd();
        if (!defaultDm.isWindowClassFlashPlayerActiveX(pointWindowHwnd)) {
            throw new FrontException("当前窗口 " + pointWindowHwnd + " 不是 flash 窗口！");
        }

        boolean create = Captcha.startIdentifyCaptcha(pointWindowHwnd, userConfig);
        if (!create) {
            throw new FrontException("当前窗口" + pointWindowHwnd + "已在列表中运行！");
        }

        log.info("当前窗口 hwnd = {}", pointWindowHwnd);
        return true;
    }

    public BindResultVo bindAll() {
        if (!userConfig.validUserInfo()) {
            throw new FrontException("用户名或密码为空");
        }

        List<Integer> ddtWindowHwnd = defaultDm.enumDdtWindowHwnd();
        if (CollectionUtils.isEmpty(ddtWindowHwnd)) {
            throw new FrontException("没有 flash 窗口");
        }

        BindResultVo bindResultVo = new BindResultVo();
        for (int hwnd : ddtWindowHwnd) {
            Util.sleep(150L);
            boolean running = Captcha.isRunning(hwnd);
            if (running) {
                bindResultVo.increaseRunningCount();
            } else {
                bindResultVo.increaseNewAddCount();
                Captcha.startIdentifyCaptcha(hwnd, userConfig);
            }
        }
        Util.sleep(300L);
        log.info(bindResultVo.buildInfo());
        return bindResultVo;
    }

    public StringRet captureCaptchaSampleRegion() {
        Collection<Captcha> captchaList = GlobalVariable.CAPTCHA_MAP.values();
        if (CollectionUtils.isEmpty(captchaList)) {
            return StringRet.buildFail("当前没有正在运行的线程");
        }

        List<String> pathList = new ArrayList<>();
        for (Captcha captcha : captchaList) {
            String path = Constants.RESOURCE_DIR + Captcha.TEMPLATE_PIC_PREFIX + (RANDOM.nextInt(1000) + 100) + Constants.BMP_SUFFIX;
            captcha.captureCountDownSampleRegion(path);
            pathList.add(path);
        }

        return StringRet.buildSuccess(StringUtils.join(pathList, "\n"));
    }

    public void monitorNewCaptchaLoop(UserConfig userConfig) {
        while (true) {
            monitorNewCaptcha(userConfig);
        }
    }

    public void monitorNewCaptcha(UserConfig userConfig) {
        Long detectNewWindowInterval = userConfig.getDetectNewWindowInterval();
        if (detectNewWindowInterval == null || detectNewWindowInterval <= 0) {
            Util.sleep(500L);
            return;
        }
        log.info("[监控线程] 等待 {} 毫秒后检测新增窗口", detectNewWindowInterval);
        Util.sleep(detectNewWindowInterval);
        BindResultVo bindResultVo;
        try {
            bindResultVo = bindAll();
        } catch (FrontException e) {
            log.info("{}", e.getMessage());
            return;
        }
        if (bindResultVo == null) {
            log.info("[监控线程] 无游戏 flash 窗口");
        } else {
            log.info("[监控线程] {}", bindResultVo.buildInfo());
        }
    }

    public void monitorActionLoop() {
        log.info("监控线程启动！");
        while (true) {
            Util.sleep(100L);
            monitorCaptchaAppearAction();
            monitorFlopBonusAction();
        }
    }

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

    public StringRet getTjAccountInfo() {
        String username = userConfig.getUsername();
        String password = userConfig.getPassword();
        return StringRet.buildSuccess(TjHttpUtil.getAccountInfo(username, password));
    }
}
