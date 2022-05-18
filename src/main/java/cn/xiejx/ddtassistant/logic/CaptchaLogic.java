package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.config.UserConfig;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Component
@Slf4j
public class CaptchaLogic {
    public static final Cacher<String, Long> TIME_CACHER = new CacherBuilder<String, Long>().scheduleName("timeCacher").delay(30, TimeUnit.SECONDS).build();
    public static final String HAS_FOUND_KEY = "hasFound";
    public static final long S = 5000L;

    @Resource
    private UserConfig userConfig;
    @Resource
    private DmDdt defaultDm;

    @PostConstruct
    public void init() {
        startMonitorCaptchaAppearActionLoopThread();
        startMonitorNewCaptchaLoopThread(userConfig);
    }

    public void startMonitorNewCaptchaLoopThread(UserConfig userConfig) {
        GlobalVariable.THREAD_POOL.execute(() -> monitorNewCaptchaLoop(userConfig));
    }

    public void startMonitorCaptchaAppearActionLoopThread() {
        GlobalVariable.THREAD_POOL.execute(this::monitorCaptchaAppearActionLoop);
    }

    public TjResponse testCaptcha() {
        TjPredictDto build = TjPredictDto.build(userConfig, "img/captcha-sample.jpg");
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

    public void monitorCaptchaAppearActionLoop() {
        log.info("监控线程启动！");
        while (true) {
            Util.sleep(100L);
            monitorCaptchaAppearAction();
        }
    }

    public void monitorCaptchaAppearAction() {
        String keyPress1 = userConfig.getKeyPressAfterCaptchaShow();
        if (StringUtils.isBlank(keyPress1)) {
            return;
        }
        if (TIME_CACHER.get(HAS_FOUND_KEY) != null) {
            log.info("[监控线程] 发现验证码");
            if (StringUtils.isBlank(keyPress1)) {
                log.info("[监控线程] 按下按键 {}", keyPress1);
                defaultDm.keyPressChar(keyPress1);
            }

            while (true) {
                Util.sleep(100L);

                if (TIME_CACHER.get(HAS_FOUND_KEY) != null) {
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
}
